package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.register.RegisterPair;

public abstract class Instruction_RdP4
	extends Instruction
{
	private RegisterPair rd;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_RdP4( RegisterPair rd )
	{
		setRd( rd );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setRd( RegisterPair rd )
	{
		this.rd = rd;
	}

	public RegisterPair getRd()
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
