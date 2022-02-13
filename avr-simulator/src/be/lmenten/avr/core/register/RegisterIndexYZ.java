package be.lmenten.avr.core.register;

import be.lmenten.avr.core.data.CoreControlRegister;

public enum RegisterIndexYZ
	implements IRegisterIndex
{
	Y					( 1, Register.R28, Register.R29 ),
	Z					( 0, Register.R30, Register.R31 ),
	;

	private int code;
	private final Register lowerRegister;
	private final Register upperRegister;

	private RegisterIndexYZ( int code, Register lowerRegister, Register upperRegister )
	{
		this.code = code;
		this.lowerRegister = lowerRegister;
		this.upperRegister = upperRegister;
	}

	public static RegisterIndexYZ lookup( int code )
	{
		switch( code )
		{
			case 0: return Z;
			case 1: return Y;
		}

		// FIXME change text
		throw new IllegalArgumentException( "invalid" );
	}

	@Override
	public int getIndex()
	{
		return lowerRegister.getIndex();
	}

	@Override
	public int getOperandIndex()
	{
		return code;
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
		throw new RuntimeException();
	}

	@Override
	public String getExtendedRegisterName()
	{
		throw new RuntimeException();
	}

	@Override
	public String toString()
	{
		switch( this )
		{
			case Y:	return "Y";
			case Z:	return "Z";
		}

		return "???";
	}
}
