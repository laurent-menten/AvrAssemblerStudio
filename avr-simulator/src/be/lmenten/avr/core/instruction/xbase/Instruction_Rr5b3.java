package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.register.Register;

public abstract class Instruction_Rr5b3
	extends Instruction_Rr5
{
	private int b;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_Rr5b3( Register rr, int b )
	{
		super( rr );

		if( (b<0) || (b>7) )
		{
			throw new RuntimeException( "Invalid bit number " + b + " for " + getInstructionSetEntry() );			
		}
		this.b = b;
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setBit( int b )
	{
		this.b = b;
	}

	public int getBit()
	{
		return b;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public String getOperand2( Core core )
	{
		return Integer.toString( getBit() );
	}
}
