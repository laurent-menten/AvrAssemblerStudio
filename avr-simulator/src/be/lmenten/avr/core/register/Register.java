package be.lmenten.avr.core.register;

public enum Register
	implements IRegister
{
	R0,
	R1,
	R2,
	R3,
	R4,
	R5,
	R6,
	R7,
	R8,
	R9,

	R10,
	R11,
	R12,
	R13,
	R14,
	R15,
	R16,
	R17,
	R18,
	R19,

	R20,
	R21,
	R22,
	R23,
	R24,
	R25,
	R26( "XL" ),
	R27( "XH" ),
	R28( "YL" ),
	R29( "YH" ),

	R30( "ZL" ),
	R31( "ZH" ),
	;

	// ------------------------------------------------------------------------

	public static final Register XL = R26;
	public static final Register XH = R27;
	public static final Register YL = R28;
	public static final Register YH = R29;
	public static final Register ZL = R30;
	public static final Register ZH = R31;

	// ------------------------------------------------------------------------

	private final String alias;

	private Register()
	{
		this( null );
	}

	private Register( String alias )
	{
		this.alias = alias;
	}

	public static Register lookup( int r )
	{
		return values() [r];
	}

	@Override
	public int getIndex()
	{
		return ordinal();
	}

	@Override
	public int getOperandIndex()
	{
		return ordinal();
	}

	@Override
	public String getAlias()
	{
		return alias;
	}

	@Override
	public String toString()
	{
		if( alias != null )
		{
			return getAlias();
		}
	
		return name();
	}
}
