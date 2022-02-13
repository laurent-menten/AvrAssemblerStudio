package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.register.Register;

public abstract class Instruction_Rr5
	extends Instruction
{
	private Register rr;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_Rr5( Register rr )
	{
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
	public String getOperand1( Core core )
	{
		return getRr().toString();
	}
}
