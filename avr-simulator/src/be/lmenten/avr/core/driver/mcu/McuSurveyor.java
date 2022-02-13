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
import be.lmenten.avr.core.data.CoreControlRegister;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.driver.DriverBase;
import be.lmenten.avr.core.event.CoreEvent;
import be.lmenten.avr.core.event.CoreEventType;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten<a>
 * @version 1.0
 * @since 1.0 - 2022 / 02 / 13
 */
public class McuSurveyor
	extends DriverBase
{
	private final CoreRegister SPL;
	private final CoreRegister SPH;

	private int currentStackPointer;

	// ========================================================================
	// = Constructor ==========================================================
	// ========================================================================

	public McuSurveyor( String id, Core core )
	{
		super( id, core );

		SPL = core.getRegister( CoreControlRegister.SPL );
		SPL.addAccessListener( checkStack );

		SPH = core.getRegister( CoreControlRegister.SPH );
		if( SPH != null )
		{
			SPH.addAccessListener( checkStack );
		}
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

	private final AccessEventListener checkStack = new AccessEventListener()
	{
		@Override
		public void onAccessEvent( AccessEvent event )
		{
			if( event.getSource() == SPL )
			{
				currentStackPointer = (currentStackPointer & 0xFF00 )
						| (SPL.silentGetData() & 0x00FF)
				;
			}
			else if( event.getSource() == SPH )
			{
				currentStackPointer = (currentStackPointer & 0x00FF )
						| ((SPH.silentGetData() << 8) & 0xFF00)
				;
			}

			if( currentStackPointer < core.getDescriptor().getSramBase() )
			{
				CoreEvent ev = new CoreEvent( CoreEventType.STACK_OVERFLOW, core );
				core.fireCoreEvent( ev );
			}
		}
	};
}
