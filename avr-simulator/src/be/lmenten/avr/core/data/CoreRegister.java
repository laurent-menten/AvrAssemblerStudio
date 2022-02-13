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

import java.util.StringJoiner;

import be.lmenten.avr.core.descriptor.CoreRegisterDescriptor;
import be.lmenten.avr.utils.StringUtils;

/**
 * 
 * @author Laurent Menten
 * @since 1.0
 */
public class CoreRegister
	extends CoreData
{
	private final CoreRegisterDescriptor rdesc;

	// ========================================================================
	// === CONSTRUCTOR(s) =====================================================
	// ========================================================================

	public CoreRegister( int address, CoreRegisterDescriptor desc )
	{
		this( address, desc, desc.getDefaultValue() );
	}

	public CoreRegister( int address, CoreRegisterDescriptor desc, byte value )
	{
		super( address, value, desc.getDefaultValue()  );

		this.rdesc = desc;
	}
	
	// ========================================================================
	// === ACCESSOR(s) ========================================================
	// ========================================================================

	public CoreRegisterDescriptor getRegisterDescriptor()
	{
		return rdesc;
	}

	// ------------------------------------------------------------------------

	public CoreRegisterType getType()
	{
		return rdesc.getType();
	}

	public String getName()
	{
		return rdesc.getName();
	}

	public String getBitName( int bit )
	{
		return rdesc.getBitName( bit );
	}

	public String getBitDescription( int bit )
	{
		return rdesc.getBitDescription( bit );
	}

	// ------------------------------------------------------------------------

	public int getBitByName( String name )
	{
		return rdesc.getBitByName( name );
	}

	// ========================================================================
	// === ACTION(s) ==========================================================
	// ========================================================================

	public byte mask( String ... names )
	{
		if( names == null )
		{
			throw new NullPointerException();
		}

		byte mask = 0b0000_0000;
		for( String name : names )
		{
			if( name == null )
			{
				throw new NullPointerException();
			}

			int bit = getBitByName( name );
			if( bit < 0 )
			{
				throw new IllegalArgumentException( "Bit " + getName() + "." + name + " not found !!!" );
			}

			mask |= (1 << bit);			
		}

		return (byte) mask( mask );
	}

	// ------------------------------------------------------------------------

	public boolean bit( String name )
	{
		if( name == null )
		{
			throw new NullPointerException();
		}

		int bit = getBitByName( name );
		if( bit < 0 )
		{
			throw new IllegalArgumentException( "Bit " + getName() + "." + name + " not found !!!" );
		}

		return bit( bit );
	}

	public byte bits( String ... names )
	{
		if( names == null )
		{
			throw new NullPointerException();
		}

		byte mask = 0b0000_0000;
		for( String name : names )
		{
			if( name == null )
			{
				throw new NullPointerException();
			}

			int bit = getBitByName( name );
			if( bit < 0 )
			{
				throw new IllegalArgumentException( "Bit " + getName() + "." + name + " not found !!!" );
			}

			mask |= (1 << bit);			
		}


		return (byte) bits( mask );
	}

	// -------------------------------------------------------------------------

	public void bit( String name, boolean state )
	{
		if( name == null )
		{
			throw new NullPointerException();
		}

		int bit = getBitByName( name );
		if( bit < 0 )
		{
			throw new IllegalArgumentException( "Bit " + getName() + "." + name + " not found !!!" );
		}

		bit( bit, state );
	}

	// ========================================================================
	// === Object =============================================================
	// ========================================================================

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();

		s.append( getName() )
		 .append( "=" )
		 .append( StringUtils.toBinaryString( getData(), 8 ) )
		 ;

		if( rdesc != null )
		{
			s.append( " { " );

			StringJoiner sj = new StringJoiner( ", " );
			for( int i = 7 ; i >= 0 ; i-- )
			{
				if( getBitName(i) != null )
				{
					sj.add( getBitName(i) + "=" + (bit(i) ? "1" : "0") );
				}
			}

			s.append( sj )
			 .append( String.format( "} [initial=0x%02X]", getInitialData() & getDataMask() ) )
			 ;
		}

		return s.toString();
	}
}
