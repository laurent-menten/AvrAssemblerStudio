package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.register.UpperRegisterPair;

public abstract class Instruction_Rd2
	extends Instruction
{
	private UpperRegisterPair rd;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_Rd2( UpperRegisterPair rd )
	{
		setRd( rd );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setRd( UpperRegisterPair rd )
	{
		this.rd = rd;
	}

	public UpperRegisterPair getRd()
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
