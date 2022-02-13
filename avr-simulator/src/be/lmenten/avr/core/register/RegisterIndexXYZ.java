package be.lmenten.avr.core.register;

import be.lmenten.avr.core.data.CoreControlRegister;

public enum RegisterIndexXYZ
	implements IRegisterIndex
{
	X					( 0b1100, "RAMPX", CoreControlRegister.RAMY, Register.R26, Register.R27 ),
	X_POSTINCREMENT		( 0b1101, "RAMPX", CoreControlRegister.RAMY, Register.R26, Register.R27, false, true ),
	X_PREDECREMENT		( 0b1110, "RAMPX", CoreControlRegister.RAMY, Register.R26, Register.R27, true, false ),
	
	Y_POSTINCREMENT		( 0b1001, "RAMPY", CoreControlRegister.RAMY, Register.R28, Register.R29, false, true  ),
	Y_PREDECREMENT		( 0b1010, "RAMPY", CoreControlRegister.RAMY, Register.R28, Register.R29, true, false ),

	Z_POSTINCREMENT		( 0b0001, "RAMPZ", CoreControlRegister.RAMY, Register.R30, Register.R31, false, true  ),
	Z_PREDECREMENT		( 0b0010, "RAMPZ", CoreControlRegister.RAMY, Register.R30, Register.R31, true, false ),
	;

	private int code;
	private final CoreControlRegister extendedRegister;
	private final String extendedRegisterName;
	private final Register lowerRegister;
	private final Register upperRegister;
	private final boolean preDecrement;
	private final boolean postIncrement;

	private RegisterIndexXYZ( int code, String extendedRegisterName, CoreControlRegister extendedRegister, Register lowerRegister, Register upperRegister )
	{
		this( code, extendedRegisterName, extendedRegister, lowerRegister, upperRegister, false, false );
	}

	private RegisterIndexXYZ( int code, String extendedRegisterName, CoreControlRegister extendedRegister, Register lowerRegister, Register upperRegister, boolean preDecrement, boolean postIncrement )
	{
		this.code = code;
		this.extendedRegister = extendedRegister;
		this.extendedRegisterName = extendedRegisterName;
		this.lowerRegister = lowerRegister;
		this.upperRegister = upperRegister;

		this.preDecrement = preDecrement;
		this.postIncrement = postIncrement;
	}

	public static RegisterIndexXYZ lookup( int code )
	{
		for( RegisterIndexXYZ r : RegisterIndexXYZ.values() )
		{
			if( r.code == code )
			{
				return r;
			}
		}

		return null;
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

	public boolean isPreDecrement()
	{
		return preDecrement;
	}

	public boolean isPostIncrement()
	{
		return postIncrement;
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

	@Override
	public String toString()
	{
		switch( this )
		{
			case X:					return "X";
			case X_POSTINCREMENT:	return "X+";
			case X_PREDECREMENT:	return "-X";
			case Y_POSTINCREMENT:	return "Y+";
			case Y_PREDECREMENT:	return "-Y";
			case Z_POSTINCREMENT:	return "Z+";
			case Z_PREDECREMENT:	return "-Z";
		}

		return "???";
	}
}
