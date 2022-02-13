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

package be.lmenten.avr.core;

/**
 * A value in the context of an AVR core.
 *
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten<a>
 * @version 1.0
 * @since 1.0 - 2022 / 02 / 13
 */
public abstract class CoreMemoryValue
{
	private int data = 0;

	// ========================================================================
	// = GETTERS / SETTERS ====================================================
	// ========================================================================

	/**
	 * Get the size of this memory cell value in bits.
	 *
	 * @return the data width
	 */
	public abstract int getDataWidth();

	/**
	 * Get the full mask for this memory cell value.
	 *
	 * @return the data mask
	 */
	public final int getDataMask()
	{
		return ((1 << getDataWidth()) - 1);
	}

	// ------------------------------------------------------------------------

	/**
	 * Set the data of this memory cell.
	 *
	 * @param data the value
	 */
	protected void setData( int data )
	{
		this.data = data;
	}

	/**
	 * Get the data of this memory cell.
	 *
	 * @return the value
	 */
	protected int getData()
	{
		return data;
	}

	// ------------------------------------------------------------------------

	/**
	 * Set the data of this memory cell from a Value object.
	 *
	 * @param value the value
	 */
	public void setValue( Value value )
	{
		this.data = value.getData();
	}

	/**
	 * Get the data of this memory cell as a Value object.
	 *
	 * @return the value
	 */
	public Value getValue()
	{
		return new Value( this.data, getDataWidth() );
	}

	// ========================================================================
	// = Utilities ============================================================
	// ========================================================================

	/**
	 * Check if the value of this memory cell is equal to zero.
	 *
	 * @return true if value equals zero.
	 */
	public boolean isZero()
	{
		return (data & getDataMask()) == 0;
	}

	/**
	 * Get the state of this memory cell value sign bit.
	 *
	 * @return true if sign bit is set.
	 */
	public boolean signBit()
	{
		return bit( getDataWidth() - 1 );
	}

	// ========================================================================
	// =
	// ========================================================================

	/**
	 * <p>
	 * Return the value of this data cell ANDed with the given mask.
	 *
	 * <p>
	 * !!! Does not trigger event or record accesses.
	 *
	 * @param mask the mask
	 * @return the masked value
	 */
	public int mask( int mask )
	{
		return (this.data & mask) & getDataMask();
	}

	/**
	 * <p>
	 * Get the value of this memory cell. The mask indicates
	 * the bits to keep, if the mask is not continuous the bits are
	 * packed.
	 *
	 * <p>
	 * !!! Does not trigger event or record accesses.
	 *
	 * @param mask the mask
	 * @return the packed value
	 */
	public int bits( int mask )
	{
		int rc = 0;

		for( int from=0, to=0 ; from < getDataWidth() ; from++ )
		{
			if( (mask & (1 << from)) == (1 << from) )
			{
				if( (this.data & (1 << from)) == (1 << from) )
				{
					rc |= (1 << to);
				}
				else
				{
					rc &= ~(1 << to);
				}

				to++;
			}
		}

		return rc;
	}

	/**
	 * <p>
	 * Set the value of this memory cell. The mask indicates
	 * the bits to set, if the mask is not continuous the bits are
	 * unpacked.
	 *
	 * <p>
	 * !!! Does not trigger event or record accesses.
	 *
	 * @param value the value
	 * @param mask the mask
	 */
	public void bits( int value, int mask )
	{
		int data = this.data;

		for( int from=0, to=0 ; to < getDataWidth() ; to++ )
		{
			if( (mask & (1 << to)) == (1 << to) )
			{
				if( (value & (1 << from)) == (1 << from) )
				{
					data |= (1 << to);
				}
				else
				{
					data &= ~(1 << to);
				}

				from++;
			}
		}

		this.data = data;
	}

	// ========================================================================
	// =
	// ========================================================================

	/**
	 * Get the state of a single bit.
	 *
	 * @param bit the bit number
	 * @return the state of the bit
	 */
	public boolean bit( int bit )
	{
		if( (bit < 0) || (bit >= getDataWidth()) )
		{
			throw new IllegalArgumentException( "Bit number " + bit + " out of range" );
		}

		return ((this.data & (1<<bit)) == (1<<bit));
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the state of bit 0.
	 *
	 *  @return the bit state
	 */
	public boolean bit0()	{ return bit( 0 ); }

	/**
	 * Get the state of bit 1.
	 *
	 *  @return the bit state
	 */
	public boolean bit1()	{ return bit( 1 ); }

	/**
	 * Get the state of bit 2.
	 *
	 *  @return the bit state
	 */
	public boolean bit2()	{ return bit( 2 ); }

	/**
	 * Get the state of bit 3.
	 *
	 *  @return the bit state
	 */
	public boolean bit3()	{ return bit( 3 ); }

	/**
	 * Get the state of bit 4.
	 *
	 *  @return the bit state
	 */
	public boolean bit4()	{ return bit( 4 ); }

	/**
	 * Get the state of bit 5.
	 *
	 *  @return the bit state
	 */
	public boolean bit5()	{ return bit( 5 ); }

	/**
	 * Get the state of bit 6.
	 *
	 *  @return the bit state
	 */
	public boolean bit6()	{ return bit( 6 ); }

	/**
	 * Get the state of bit 7.
	 *
	 *  @return the bit state
	 */
	public boolean bit7()	{ return bit( 7 ); }

	/**
	 * Get the state of bit 8.
	 *
	 *  @return the bit state
	 */
	public boolean bit8() { return bit( 8 ); }

	/**
	 * Get the state of bit 9.
	 *
	 *  @return the bit state
	 */
	public boolean bit9() { return bit( 9 ); }

	/**
	 * Get the state of bit 10.
	 *
	 *  @return the bit state
	 */
	public boolean bit10() { return bit( 10 ); }

	/**
	 * Get the state of bit 11.
	 *
	 *  @return the bit state
	 */
	public boolean bit11() { return bit( 11 ); }

	/**
	 * Get the state of bit 12.
	 *
	 *  @return the bit state
	 */
	public boolean bit12() { return bit( 12 ); }

	/**
	 * Get the state of bit 13.
	 *
	 *  @return the bit state
	 */
	public boolean bit13() { return bit( 13 ); }

	/**
	 * Get the state of bit 14.
	 *
	 *  @return the bit state
	 */
	public boolean bit14() { return bit( 14 ); }

	/**
	 * Get the state of bit 15.
	 *
	 *  @return the bit state
	 */
	public boolean bit15() { return bit( 15 ); }

	// ------------------------------------------------------------------------

	/**
	 * Set the state of a single bit.
	 *
	 * @param bit the bit number
	 * @param state the state of the bit
	 */
	public void bit( int bit, final boolean state )
	{
		if( (bit < 0) || (bit >= getDataWidth()) )
		{
			throw new IllegalArgumentException( "Bit number " + bit + " out of range" );
		}

		if( state )
		{
			this.data = this.data | (1<<bit);
		}
		else
		{
			this.data = this.data & ~(1<<bit);
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Set the state of bit 0.
	 *
	 * @param state the state of the bit
	 */
	public void bit0( boolean state )	{ bit( 0, state ); }

	/**
	 * Set the state of bit 1.
	 *
	 * @param state the state of the bit
	 */
	public void bit1( boolean state )	{ bit( 1, state ); }

	/**
	 * Set the state of bit 2.
	 *
	 * @param state the state of the bit
	 */
	public void bit2( boolean state )	{ bit( 2, state ); }

	/**
	 * Set the state of bit 3.
	 *
	 * @param state the state of the bit
	 */
	public void bit3( boolean state )	{ bit( 3, state ); }

	/**
	 * Set the state of bit 4.
	 *
	 * @param state the state of the bit
	 */
	public void bit4( boolean state )	{ bit( 4, state ); }

	/**
	 * Set the state of bit 5.
	 *
	 * @param state the state of the bit
	 */
	public void bit5( boolean state )	{ bit( 5, state ); }

	/**
	 * Set the state of bit 6.
	 *
	 * @param state the state of the bit
	 */
	public void bit6( boolean state )	{ bit( 6, state ); }

	/**
	 * Set the state of bit 7.
	 *
	 * @param state the state of the bit
	 */
	public void bit7( boolean state )	{ bit( 7, state ); }

	/**
	 * Set the state of bit 8.
	 *
	 * @param state the state of the bit
	 */
	public void bit8( boolean state ) { bit( 8, state ); }

	/**
	 * Set the state of bit 9.
	 *
	 * @param state the state of the bit
	 */
	public void bit9( boolean state ) { bit( 9, state ); }

	/**
	 * Set the state of bit 10.
	 *
	 * @param state the state of the bit
	 */

	public void bit10( boolean state ) { bit( 10, state ); }
	/**
	 * Set the state of bit 11.
	 *
	 * @param state the state of the bit
	 */

	public void bit11( boolean state ) { bit( 11, state ); }
	/**
	 * Set the state of bit 12.
	 *
	 * @param state the state of the bit
	 */

	public void bit12( boolean state ) { bit( 12, state ); }
	/**
	 * Set the state of bit 13.
	 *
	 * @param state the state of the bit
	 */
	public void bit13( boolean state ) { bit( 13, state ); }

	/**
	 * Set the state of bit 14.
	 *
	 * @param state the state of the bit
	 */
	public void bit14( boolean state ) { bit( 14, state ); }

	/**
	 * Set the state of bit 15.
	 *
	 * @param state the state of the bit
	 */
	public void bit15( boolean state ) { bit( 15, state ); }

	// ========================================================================
	// = Value class used for calculations ====================================
	// ========================================================================

	/**
	 * A specialisation of CoreMemoryValue used for computation in
	 * instructions simulation.
	 *
	 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten<a>
	 * @version 1.0
	 * @since 1.0 - 2022 / 02 / 13
	 */
	public class Value
			extends CoreMemoryValue
	{
		private final int dataWidth;

		// ====================================================================
		// = Constructor(s) ===================================================
		// ====================================================================

		public Value( int data, int dataWidth )
		{
			CoreMemoryValue.this.data = data;
			this.dataWidth = dataWidth;

			extendSignBit();
		}

		public Value( CoreMemoryValue v )
		{
			CoreMemoryValue.this.data = v.data;
			this.dataWidth = v.getDataWidth();

			extendSignBit();
		}

		public Value( CoreMemoryValue v1, CoreMemoryValue v2 )
		{
			CoreMemoryValue.this.data = ((v1.data & v1.getDataMask()) << v1.getDataWidth() )
					| (v2.data & v2.getDataMask());
			this.dataWidth = Math.max( v1.getDataWidth(), v2.getDataWidth() );

			extendSignBit();
		}

		// --------------------------------------------------------------------

		private void extendSignBit()
		{
			if( bit(dataWidth - 1) )
			{
				data |= -(1 << dataWidth);
			}
		}

		// ========================================================================
		// = GETTERS / SETTERS ====================================================
		// ========================================================================

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getDataWidth()
		{
			return dataWidth;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getData()
		{
			return super.getData();
		}

		// ========================================================================
		// = Computations =========================================================
		// ========================================================================

		public interface UnaryOperation
		{
			int compute( int value1 );
		}

		/**
		 *
		 * @param operation what to do
		 * @return the computed value
		 */
		public Value compute( UnaryOperation operation )
		{
			return new Value( operation.compute( data ), getDataWidth() );
		}

		// ------------------------------------------------------------------------

		public interface BinaryOperation
		{
			int compute( int value1, int value2 );
		}

		/**
		 *
		 * @param operation what to do
		 * @param operand the other value
		 * @return the computed value
		 */
		public Value compute( BinaryOperation operation, CoreMemoryValue operand )
		{
			return new Value( operation.compute( data, operand.data ), getDataWidth() );
		}

		// ------------------------------------------------------------------------

		public interface BinaryOperationWithCarry
		{
			int compute( int value1, int value2, int c );
		}

		/**
		 *
		 * @param operation what to do
		 * @param operand the other value
		 * @param c carry flag value
		 * @return the computed value
		 */
		public Value compute( BinaryOperationWithCarry operation, CoreMemoryValue operand, boolean c )
		{
			return new Value( operation.compute( data, operand.data, c ? 1 : 0 ), getDataWidth() );
		}
	}

	// ========================================================================
	// === Object =============================================================
	// ========================================================================

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();

		String fmt = "0x%0" + (getDataWidth() / 4)  + "X /%i";

		s.append( String.format( fmt, (this.data & getDataMask()), getDataWidth() ) )
			.append( " (" )
		;

		for( int i = 0 ; i < getDataWidth() ; i++ )
		{
			s.append( bit(i) ? '1' : '0' );
		}

		s.append( ")" );

		return s.toString();
	}
}
