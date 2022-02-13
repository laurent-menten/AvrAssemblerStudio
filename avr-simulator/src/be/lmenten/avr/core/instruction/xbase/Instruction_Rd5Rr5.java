package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.register.Register;

public abstract class Instruction_Rd5Rr5
	extends Instruction_Rd5
{
	private Register rr;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_Rd5Rr5( Register rd, Register rr )
	{
		super( rd );

		setRr( rr );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setRr( Register rr )
	{
		this.rr = rr;
	}

	public Register getRr()
	{
		return rr;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public String getOperand2( Core core )
	{
		return  getRr().toString();
	}
}
