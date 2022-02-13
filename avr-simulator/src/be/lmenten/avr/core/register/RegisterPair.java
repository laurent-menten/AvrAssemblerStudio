package be.lmenten.avr.core.register;

public enum RegisterPair
	implements IRegisterPair
{
	R1_R0( Register.R0, Register.R1 ),
	R3_R2( Register.R2, Register.R3 ),
	R5_R4( Register.R4, Register.R5 ),
	R7_R6( Register.R6, Register.R7 ),
	R9_R8( Register.R8, Register.R9 ),

	R11_R10( Register.R10, Register.R11 ),
	R13_R12( Register.R12, Register.R13 ),
	R15_R14( Register.R14, Register.R15 ),
	R17_R16( Register.R16, Register.R17 ),
	R19_R18( Register.R18, Register.R19 ),

	R21_R20( Register.R20, Register.R21 ),
	R23_R22( Register.R22, Register.R23 ),
	R25_R24( Register.R24, Register.R25 ),
	R27_R26( Register.R26, Register.R27 ),
	R29_R28( Register.R28, Register.R29 ),

	R31_R30( Register.R30, Register.R31 ),
	;

	private final Register lowerRegister;
	private final Register upperRegister;
	private final String alias;

	private RegisterPair( Register lowerRegister, Register upperRegister )
	{
		this( lowerRegister, upperRegister, null );
	}

	private RegisterPair( Register lowerRegister, Register upperRegister, String alias )
	{
		this.lowerRegister = lowerRegister;
		this.upperRegister = upperRegister;
		this.alias = alias;
	}

	// ------------------------------------------------------------------------

	public static RegisterPair lookup( int r )
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
		return upperRegister.toString() + ":" + lowerRegister.toString();
	}
}
