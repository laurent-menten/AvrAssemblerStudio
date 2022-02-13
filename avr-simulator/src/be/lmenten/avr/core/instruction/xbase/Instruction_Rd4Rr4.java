package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.register.UpperRegister;

public abstract class Instruction_Rd4Rr4
	extends Instruction_Rd4
{
	private UpperRegister rr;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_Rd4Rr4( UpperRegister rd, UpperRegister rr )
	{
		super( rd );

		setRr( rr );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setRr( UpperRegister rr )
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
	public String getOperand2( Core core )
	{
		return getRr().toString();
	}
}
