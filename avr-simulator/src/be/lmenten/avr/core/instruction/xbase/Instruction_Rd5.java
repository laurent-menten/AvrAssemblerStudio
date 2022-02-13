package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.register.Register;

public abstract class Instruction_Rd5
	extends Instruction
{
	private Register rd;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_Rd5( Register rd )
	{
		setRd( rd );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setRd( Register rd )
	{
		this.rd = rd;
	}

	public Register getRd()
	{
		return rd;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public String getOperand1( Core core )
	{
		return getRd().toString();
	}
}
