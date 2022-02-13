package be.lmenten.avr.core.register;

import be.lmenten.avr.core.data.CoreControlRegister;

public enum RegisterYZ
	implements IRegisterIndex
{
	Y( "RAMPY", CoreControlRegister.RAMY, Register.R28, Register.R29 ),
	Z( "RAMPZ", CoreControlRegister.RAMZ, Register.R30, Register.R31 ),
	;

	private final CoreControlRegister extendedRegister;
	private final String extendedRegisterName;
	private final Register lowerRegister;
	private final Register upperRegister;

	private RegisterYZ( String extendedRegisterName, CoreControlRegister extendedRegister, Register lowerRegister, Register upperRegister )
	{
		this.extendedRegister = extendedRegister;
		this.extendedRegisterName = extendedRegisterName;
		this.lowerRegister = lowerRegister;
		this.upperRegister = upperRegister;
	}

	public RegisterPair rebase()
	{
		return RegisterPair.valueOf( name() );
	}

	public static RegisterYZ lookup( int r )
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
		return null;
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
	public CoreControlRegister getExtendedRegister()
	{
		return extendedRegister;
	}

	@Override
	public String getExtendedRegisterName()
	{
		return extendedRegisterName;
	}
}
