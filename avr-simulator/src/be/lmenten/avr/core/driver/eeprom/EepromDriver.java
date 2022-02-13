// = ======================================================================== =
// = === AVR Simulator =============== Copyright (c) 2022+ Laurent Menten === =
// = ======================================================================== =
// = = This program is free software: you can redistribute it and/or modify = =
// = = it under the terms of the GNU General Public License as published by = =
// = = the Free Software Foundation, either version 3 of the License, or    = =
// = = (at your option) any later version.                                  = =
// = =                                                                      = =
// = = This program is distributed in the hope that it will be useful, but  = =
// = = WITHOUT ANY WARRANTY; without even the implied warranty of           = =
// = = MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU    = =
// = = General Public License for more details.                             = =
// = =                                                                      = =
// = = You should have received a copy of the GNU General Public License    = =
// = = along with this program. If not, see                                 = =
// = = <https://www.gnu.org/licenses/>.                                     = =
// = ======================================================================== =

package be.lmenten.avr.core.driver.eeprom;

import java.util.logging.Logger;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.analysis.AccessEvent;
import be.lmenten.avr.core.analysis.AccessEventListener;
import be.lmenten.avr.core.data.CoreData;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.driver.DriverBase;
import be.lmenten.avr.core.driver.MemoryDriver;
import be.lmenten.avr.core.event.CoreEvent;
import be.lmenten.avr.core.event.CoreEventType;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten<a>
 * @version 1.0
 * @since 1.0 - 2022 / 02 / 13
 */
public class EepromDriver
	extends DriverBase
	implements MemoryDriver, AccessEventListener
{
	private final CoreRegister EECR;
	private final CoreRegister EEARL;
	private final CoreRegister EEARH;
	private final CoreRegister EEDR;

	private final int interruptVector;

	// ------------------------------------------------------------------------

	private final int eepromSize;
	private final CoreData eeprom [];

	// ------------------------------------------------------------------------

	private boolean writePending;
	private int writeMode;
	private double programmingTime;

	private long ticksCount;
	private long interruptsCount;

	// ========================================================================
	// = Constructor ==========================================================
	// ========================================================================

	public EepromDriver( String id, Core core )
	{
		super( id, core );

		// --------------------------------------------------------------------

		EECR = core.getRegister( "EECR" );
		EECR.addAccessListener( this );

		EEARL = core.getRegister( "EEARL" );
		EEARH = core.getRegister( "EEARH" );
		EEDR = core.getRegister( "EEDR" );

		interruptVector = core.getDescriptor().getInterruptVector( "EE_READY" );

		// --------------------------------------------------------------------

		eepromSize = core.getDescriptor().getEepromSize();

		eeprom = new CoreData [ eepromSize ];
		for( int addr = 0 ; addr < eeprom.length ; addr++ )
		{
			int address = addr;
			CoreData data = new CoreData( address ); 
			eeprom[ address ] = data;
		}		

		LOG.info( " > Eeprom size = " + eepromSize + " bytes" );
	}

	// ========================================================================
	// = Driver interface =====================================================
	// ========================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onReset()
	{
		writePending = false;
		writeMode = 0b00;
		programmingTime = 0.0d;

		ticksCount = 0l;
		interruptsCount = 0l;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTick( long ticksCount )
	{
		// --------------------------------------------------------------------
		// - Master write enable time out -------------------------------------
		// --------------------------------------------------------------------

		if( writePending )
		{
			if( ((ticksCount - this.ticksCount) > 4)
					|| (core.getInterruptsCount() != this.interruptsCount) )
			{
				EECR.bit( "EEMPE", false );
				writePending = false;
			}

			return;
		}

		// --------------------------------------------------------------------
		// - Simulate eeprom programming time ---------------------------------
		// --------------------------------------------------------------------

		if( EECR.bit( "EEPE" )
				&& (ticksCount - this.ticksCount) >= core.nanosToTicks( programmingTime ) )
		{
			EECR.bit( "EEPE", false );

			if( EECR.bit("EEIE") )
			{
				core.interrupt( interruptVector );
			}
		}
	}

	// ========================================================================
	// = MemoryDriver interface ===============================================
	// ========================================================================

	@Override
	public int getManagedMemoryBase()
	{
		return cdesc.getEepromBase();
	}

	@Override
	public int getManagedMemorySize()
	{
		return cdesc.getEepromSize();
	}

	@Override
	public int getManagedMemoryLimit()
	{
		return cdesc.getEepromLimit();
	}

	// ------------------------------------------------------------------------

	@Override
	public CoreData getMemoryCell( int address )
	{
		if( (address < getManagedMemoryBase()) || (address > getManagedMemoryLimit()) )
		{
			throw new IndexOutOfBoundsException( address );			
		}

		return eeprom[ address ];
	}

	// ========================================================================
	// = AccessEventListener interface ========================================
	// ========================================================================

	/**
	 * EECR (control register) content has changed.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void onAccessEvent( AccessEvent event )
	{
		// --------------------------------------------------------------------
		// - Eeprom read ------------------------------------------------------
		// --------------------------------------------------------------------

		if( EECR.bit( "EERE" ) )
		{
			int address = ((EEARH.silentGetData() & 0xFF) << 8)
					| (EEARL.silentGetData() & 0xFF);

			if( (address < 0) || (address > eeprom.length) )
			{
				CoreEvent ev = new CoreEvent( CoreEventType.EEPROM_ADDRESS_OUT_OF_RANGE, core );
				ev.setValue( address );
				core.fireCoreEvent( ev );
				return;
			}

			EEDR.setData( eeprom[ address ].getData(), true );

			core.updateClockCyclesCounter( 4l );
		}

		// --------------------------------------------------------------------
		// - Eeprom write -----------------------------------------------------
		// --------------------------------------------------------------------

		else if( EECR.bit("EEMPE") && ! EECR.bit( "EEPE" )  )
		{
			writeMode = EECR.bits( "EEPM1", "EEPM0" );
			writePending = true;
		}

		else if( writePending && EECR.bit("EEPE") )
		{
			int address = ((EEARH.silentGetData() & 0xFF) << 8)
					| (EEARL.silentGetData() & 0xFF);

			if( (address < 0) || (address > eeprom.length) )
			{
				CoreEvent ev = new CoreEvent( CoreEventType.EEPROM_ADDRESS_OUT_OF_RANGE, core );
				ev.setValue( address );
				core.fireCoreEvent( ev );
				return;
			}

			switch( writeMode )
			{
				// ------------------------------------------------------------

				case 0b00:		// Atomic erase and write
					programmingTime = 3.6d;

					eeprom[ address ].setData( 0, true );
					eeprom[ address ].setData( EEDR.silentGetData(), true );

					writePending = false;
					core.updateClockCyclesCounter( 2l );
					break;

					// ------------------------------------------------------------

				case 0b10:		// Write only
					programmingTime = 1.8d;

					eeprom[ address ].setData( EEDR.silentGetData(), true );

					writePending = false;
					core.updateClockCyclesCounter( 2l );
					break;

				// ------------------------------------------------------------

				case 0b01:		// Erase only
					programmingTime = 1.8d;

					eeprom[ address ].setData( 0, true );

					writePending = false;
					core.updateClockCyclesCounter( 2l );
					break;

				// ------------------------------------------------------------

				case 0b11:
				default:
					throw new RuntimeException( "Invalid eeprom write mode" );
			}
		}
	}

	// ========================================================================
	// = Utilities ============================================================
	// ========================================================================

	private static final Logger LOG
		= Logger.getLogger( EepromDriver.class.getName() );
}
