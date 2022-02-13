package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.register.RegisterPair;

public abstract class Instruction_RdP4RrP4
	extends Instruction_RdP4
{
	private RegisterPair rr;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_RdP4RrP4( RegisterPair rd, RegisterPair rr )
	{
		super( rd );

		setRr( rr );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setRr( RegisterPair rr )
	{
		this.rr = rr;
	}

	public RegisterPair getRr()
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
