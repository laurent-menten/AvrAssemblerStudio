package be.lmenten.avr.core.descriptor;

/*
 * 
 * As defined in DS40002198B AVR InstructionSet manual (p.18)
 *
 */
public enum CoreVersion
{
	AVR			( "AVR" ),			// Original instruction set from 1995
	AVRe		( "AVRe" ),		// + MOVW, LPM.
	AVRe_PLUS	( "AVRe+" ),		// + xMULxx, EICALL, EIJMP, ELPM.
	AVRxm		( "AVRxm" ),		// + LAC/LAS/LAT, DES, SPM Z+2. Improved timing.
	AVRxt		( "AVRxt" ),		// AVRe+ & AVRxm. Improved timing.
	AVRrc		( "AVRrc" ),		// Reduced instruction set and register file.
	;

	// -------------------------------------------------------------------------

	private final String code;

	// =========================================================================
	// === CONSTRUCTOR(S) ======================================================
	// =========================================================================

	private CoreVersion( String code )
	{
		this.code = code;
	}

	// =========================================================================
	// === 
	// =========================================================================

	public static CoreVersion lookup( String code )
	{
		for( CoreVersion version : CoreVersion.values() )
		{
			if( version.code.equals( code ) )
			{
				return version;
			}
		}

		throw new IllegalArgumentException( "No CoreVersion enum constant for code \"" + code + "\"" );
	}

	// =========================================================================
	// === 
	// =========================================================================

	public String getCode()
	{
		return code;
	}
}
