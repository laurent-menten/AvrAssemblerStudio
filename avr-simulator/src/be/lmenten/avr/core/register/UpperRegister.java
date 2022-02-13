package be.lmenten.avr.core.register;

public enum UpperRegister
	implements IRegister
{
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

	private final String alias;

	private UpperRegister()
	{
		this( null );
	}

	private UpperRegister( String alias )
	{
		this.alias = alias;
	}

	public static UpperRegister lookup( int r )
	{
		return values() [r];
	}

	@Override
	public int getIndex()
	{
		return 16 + ordinal();
	}

	@Override
	public int getOperandIndex()
	{
		return ordinal();
	}

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
