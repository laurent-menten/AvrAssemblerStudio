package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.register.UpperRegister;

public abstract class Instruction_Rd4
	extends Instruction
{
	private UpperRegister rd;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_Rd4( UpperRegister rd )
	{
		setRd( rd );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setRd( UpperRegister rd )
	{
		this.rd = rd;
	}

	public UpperRegister getRd()
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
