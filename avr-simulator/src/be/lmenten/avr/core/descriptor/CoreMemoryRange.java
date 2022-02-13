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

package be.lmenten.avr.core.descriptor;

import java.util.logging.Logger;

import org.json.simple.JSONObject;

import be.lmenten.avr.utils.StringUtils;

/**
 * 
 * @author Laurent Menten
 * @since 1.0
 */
public class CoreMemoryRange
{
	public static final String REGISTERS_RANGE_NAME = "registers";
	public static final String IO_RANGE_NAME = "io";
	public static final String EXTENDED_IO_RANGE_NAME = "extended_io";
	public static final String SRAM_RANGE_NAME = "sram";
	public static final String EXTENDED_SRAM_RANGE_NAME = "extended_sram";

	public static final String FLASH_RANGE_NAME = "flash";

	// ------------------------------------------------------------------------
	// - 
	// ------------------------------------------------------------------------

	private static final String KEY_BASE = "base";
	private static final String KEY_SIZE = "size";
	private static final String KEY_LIMIT = "limit";

	// ------------------------------------------------------------------------

	private final CoreMemory memory;
	private final String name;
	private final int base;
	private final int size;
	private final int limit;

	// ========================================================================
	// === CONSTRUCTOR(s) =====================================================
	// ========================================================================

	/*package*/ CoreMemoryRange( CoreMemory memory, String name, JSONObject o )
	{
		String tmp;

		this.memory = memory;
		this.name = name;

		tmp = (String) o.get( KEY_BASE );
		if( tmp == null )
		{
			throw new RuntimeException( "Memory range '" + name + "' has no base" );
		}

		this.base = (int) StringUtils.parseNumber( tmp );

		tmp = (String) o.get( KEY_SIZE );
		if( tmp == null )
		{
			tmp = (String) o.get( KEY_LIMIT );
			if( tmp == null )
			{
				throw new RuntimeException( "Memory range '" + name + "' has neither size nor limit" );
			}

			this.limit = (int) StringUtils.parseNumber( tmp );
			this.size = ((limit + 1) - base);
		}
		else
		{
			this.size = (int) StringUtils.parseNumber( tmp );
			this.limit = ((base + size) - 1);

			if( o.get( KEY_LIMIT ) != null )
			{
				log.warning( "Memory range '" + name + "' has a size, limit ignored" );
			}
		}
	}

	/*package*/ CoreMemoryRange( CoreMemory memory, String name, int base, int size )
	{
		this.memory = memory;
		this.name = name;
		this.base = base;
		this.size = size;
		this.limit = ((base + size) - 1);
	}

	// ========================================================================
	// ===
	// ========================================================================

	public CoreMemory getMemoryZone()
	{
		return memory;
	}
	
	public String getRangeName()
	{
		return name;
	}

	public int getRangeBase()
	{
		return base;
	}

	public int getRangeSize()
	{
		return size;
	}

	public int getRangeLimit()
	{
		return limit;
	}

	// ========================================================================
	// === LOGGING ============================================================
	// ========================================================================

	private static final Logger log
		= Logger.getLogger( CoreMemoryRange.class.getName() );
}

