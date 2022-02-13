package be.lmenten.avr.core.register;

public enum UpperRegisterPair
	implements IRegisterPair
{
	R25_R24( Register.R24, Register.R25 ),
	R27_R26( Register.R26, Register.R27, "X" ),
	R29_R28( Register.R28, Register.R29, "Y" ),

	R31_R30( Register.R30, Register.R31, "Z" ),
	;

	private final Register lowerRegister;
	private final Register upperRegister;
	private final String alias;

	private UpperRegisterPair( Register lowerRegister, Register upperRegister )
	{
		this( lowerRegister, upperRegister, null );
	}
	
	private UpperRegisterPair( Register lowerRegister, Register upperRegister, String alias )
	{
		this.lowerRegister = lowerRegister;
		this.upperRegister = upperRegister;
		this.alias = alias;
	}

	// ------------------------------------------------------------------------

	public static UpperRegisterPair lookup( int r )
	{
		return values() [r];
	}

	@Override
	public int getIndex()
	{
		return lowerRegister.getIndex();
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
	public Register getLowerRegister()
	{
		return lowerRegister;
	}

	@Override
	public int getUpperIndex()
	{
		return getUpperRegister().getIndex();
	}

	@Override
	public Register getUpperRegister()
	{
		return upperRegister;
	}

	@Override
	public String toString()
	{
		if( alias != null )
		{
			return getAlias();
		}
	
		return upperRegister.name() + ":" + lowerRegister.name();
	}
}
