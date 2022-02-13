package be.lmenten.avr.utils;

public class NumberUtils
{
	// ========================================================================
	// = 
	// ========================================================================
	
	public static int q7( double f )
	{
		return toFractional( f, 8 );
	}

	public static int q15( double f )
	{
		return toFractional( f, 16 );
	}

	public static int toFractional( double value, int size )
	{
		double v = value;
		if( (value < 0.0d) || (value >= 2.0) )
		{
			throw new IllegalArgumentException( "Fractional number should be in the [0.0 .. 2.0[ range" );
		}

		int r = 0x0000;

		if( v > 1 )
		{
			r |= 1;
			v -= 1;
		}

		for( int i = 1 ; i < size ; i++ )
		{
			r <<= 1;

			v /= 0.5d;
			if( v >= 1.0d )
			{
				r |= 1;
				v -= 1.0d;
			}
		}

		return r;
	}

	// ------------------------------------------------------------------------

	public static double q7( int value )
	{
		return fromFractional( value, 8 );
	}

	public static double q15( int value )
	{
		return fromFractional( value, 8 );
	}

	public static double fromFractional( int value, int size )
	{
		double r = 0.0d;

		for( int i = size-1 ; i >= 0 ; i-- )
		{
			int mask = 1 << i;
			if( (value & mask) == mask )
			{
				r += Math.pow( 2, -(size-i-1) );
			}
		}

		return r;
	}

	// ========================================================================
	// = 
	// ========================================================================
	
	/**
	 * 
	 * @param b
	 * @return
	 */
	public static int propagateMSB( int b )
	{
		int value = b & 0xFF;
		if( ((value & 0b1000_0000) == 0b1000_0000) )
		{
			value |=  ~0xFF;
		}

		return value;
	}

	public static int lowByte( int data )
	{
		return data & 0xFF;
	}

	public static int highByte( int data )
	{
		return (data >> 8) & 0xFF;
	}

	/**
	 * Get the state of a single bit.
	 * 
	 * @param bit
	 * @return
	 */
	public static boolean bit( int data, int bit )
	{
		if( (bit < 0) || (bit >= 32) )
		{
			throw new IllegalArgumentException( "Bit number " + bit + " out of range" );
		}

		return ((data & (1<<bit)) == (1<<bit));
	}

	public static boolean bit0( int data ) { return bit( data, 0 ); }
	public static boolean bit1( int data ) { return bit( data, 1 ); }
	public static boolean bit2( int data ) { return bit( data, 2 ); }
	public static boolean bit3( int data ) { return bit( data, 3 ); }
	public static boolean bit4( int data ) { return bit( data, 4 ); }
	public static boolean bit5( int data ) { return bit( data, 5 ); }
	public static boolean bit6( int data ) { return bit( data, 6 ); }
	public static boolean bit7( int data ) { return bit( data, 7 ); }

	public static boolean bit8( int data ) { return bit( data, 8 ); }
	public static boolean bit9( int data ) { return bit( data, 9 ); }
	public static boolean bit10( int data ) { return bit( data, 10 ); }
	public static boolean bit11( int data ) { return bit( data, 11 ); }
	public static boolean bit12( int data ) { return bit( data, 12 ); }
	public static boolean bit13( int data ) { return bit( data, 13 ); }
	public static boolean bit14( int data ) { return bit( data, 14 ); }
	public static boolean bit15( int data ) { return bit( data, 15 ); }

	/**
	 * Set the state of a single bit.
	 * 
	 * @param bit
	 * @param state
	 */
	public static void bit( int data, int bit, final boolean state )
	{
		if( (bit < 0) || (bit >= 32) )
		{
			throw new IllegalArgumentException( "Bit number " + bit + " out of range" );
		}

		if( state )
		{
			data |= (1<<bit);
		}
		else
		{
			data &= ~(1<<bit);
		}
	}

	public static void bit0( int data, boolean state ) { bit( data, 0, state ); }
	public static void bit1( int data, boolean state ) { bit( data, 1, state ); }
	public static void bit2( int data, boolean state ) { bit( data, 2, state ); }
	public static void bit3( int data, boolean state ) { bit( data, 3, state ); }
	public static void bit4( int data, boolean state ) { bit( data, 4, state ); }
	public static void bit5( int data, boolean state ) { bit( data, 5, state ); }
	public static void bit6( int data, boolean state ) { bit( data, 6, state ); }
	public static void bit7( int data, boolean state ) { bit( data, 7, state ); }

	public static void bit8( int data, boolean state ) { bit( data, 8, state ); }
	public static void bit9( int data, boolean state ) { bit( data, 9, state ); }
	public static void bit10( int data, boolean state ) { bit( data, 10, state ); }
	public static void bit11( int data, boolean state ) { bit( data, 11, state ); }
	public static void bit12( int data, boolean state ) { bit( data, 12, state ); }
	public static void bit13( int data, boolean state ) { bit( data, 13, state ); }
	public static void bit14( int data, boolean state ) { bit( data, 14, state ); }
	public static void bit15( int data, boolean state ) { bit( data, 15, state ); }
}
