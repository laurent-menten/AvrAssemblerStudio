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

package be.lmenten.avr.core.driver.mcu;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.analysis.AccessEvent;
import be.lmenten.avr.core.analysis.AccessEventListener;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.driver.DriverBase;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten<a>
 * @version 1.0
 * @since 1.0 - 2022 / 02 / 13
 */
public class WatchdogDriver
	extends DriverBase
	implements AccessEventListener
{
	private final CoreRegister WDRCSR;

	private final int interruptVector;

	// ========================================================================
	// = Constructor ==========================================================
	// ========================================================================

	public WatchdogDriver( String id, Core core )
	{
		super( id, core );

		WDRCSR = core.getRegister( "WDTCSR" );
		WDRCSR.addAccessListener( this );

		interruptVector = core.getDescriptor().getInterruptVector( "WDT" );
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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTick( long ticksCount )
	{
	}

	// ========================================================================
	// = AccessEventListener interface ========================================
	// ========================================================================

	/**
	 * WDRCSR content has changed.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void onAccessEvent( AccessEvent event )
	{
	}	
}
