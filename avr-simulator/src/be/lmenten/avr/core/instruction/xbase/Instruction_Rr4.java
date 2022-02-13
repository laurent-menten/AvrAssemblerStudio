package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.register.UpperRegister;

public abstract class Instruction_Rr4
	extends Instruction
{
	private UpperRegister rr;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_Rr4( UpperRegister rr )
	{
		setRd( rr );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setRd( UpperRegister rr)
	{
		this.rr = rr;
	}

	public UpperRegister getRr()
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
