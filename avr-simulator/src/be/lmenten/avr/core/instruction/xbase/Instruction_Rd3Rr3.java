package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.register.LowUpperRegister;

public abstract class Instruction_Rd3Rr3
	extends Instruction_Rd3
{
	private LowUpperRegister rr;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_Rd3Rr3( LowUpperRegister rd, LowUpperRegister rr )
	{
		super( rd );

		setRr( rr );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setRr( LowUpperRegister rr )
	{
		this.rr = rr;
	}

	public LowUpperRegister getRr()
	{
		return rr;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public String getOperand2( Core core )
	{
		return getRr().toString();
	}
}
