// = ======================================================================== =
// = === AVR Programmer Studio ======= Copyright (c) 2020+ Laurent Menten === =
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

package be.lmenten.avr.core.data;

import be.lmenten.avr.core.CoreMemoryCell;

/**
 * 
 * @author Laurent Menten
 * @since 1.0
 */
public class CoreData
	extends CoreMemoryCell
{
	// ========================================================================
	// === CONSTRUCTOR(s) =====================================================
	// ========================================================================

	public CoreData( int address )
	{
		this( address, (byte) 0, (byte) 0 ); 
	}

	public CoreData( int address, byte value )
	{
		this( address, value, (byte) 0 );
	}

	public CoreData( int address, byte value, byte defaultValue )
	{
		super( address, defaultValue );

		setData( value );
	}

	// ------------------------------------------------------------------------

	@Override
	public final int getDataWidth()
	{
		return 8;
	}

	// ========================================================================
	// === ACTION(s) ==========================================================
	// ========================================================================

	/**
	 * Reset this data cell to its default value.
	 */
	@Override
	public void reset()
	{
		super.reset();
	}

	// ========================================================================
	// === Object =============================================================
	// ========================================================================

	@Override
	public String toString()
	{
		String fmt = " [initial=0x%0" + (getDataWidth() / 4)  + "X]";

		return super.toString() + String.format( fmt, getInitialData() & getDataMask() );
	}
}
