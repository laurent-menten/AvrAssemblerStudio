package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.register.LowUpperRegister;

public abstract class Instruction_Rd3
	extends Instruction
{
	private LowUpperRegister rd;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_Rd3( LowUpperRegister rd )
	{
		setRd( rd );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setRd( LowUpperRegister rd )
	{
		this.rd = rd;
	}

	public LowUpperRegister getRd()
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
