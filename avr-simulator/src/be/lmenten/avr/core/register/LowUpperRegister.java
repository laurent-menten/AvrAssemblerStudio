package be.lmenten.avr.core.register;

public enum LowUpperRegister
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
	;

	public static LowUpperRegister lookup( int r )
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

	@Override
	public String getAlias()
	{
		return null;
	}
}
