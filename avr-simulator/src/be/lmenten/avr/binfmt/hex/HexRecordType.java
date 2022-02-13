package be.lmenten.avr.binfmt.hex;

public enum HexRecordType
{
    DATA( 0x00 ),
    EOF( 0x01 ),
    EXTENDED_SEGMENT_ADDRESS( 0x02 ),
    START_SEGMENT_ADDRESS( 0x03 ),
    EXTENDED_LINEAR_ADDRESS( 0x04 ),
    START_LINEAR_ADDRESS( 0x05 ),
    ;

	private final int code;

	// ========================================================================
	// ===
	// ========================================================================

	private HexRecordType( int code )
	{
		this.code = code;
	}

	// ========================================================================
	// ===
	// ========================================================================

	public static HexRecordType lookup( int code )
	{
		for( HexRecordType type : values() )
		{
			if( type.code == code )
			{
				return type;
			}
		}

		return null;
	}
}
