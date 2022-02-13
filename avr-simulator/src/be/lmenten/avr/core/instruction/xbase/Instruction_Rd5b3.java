package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.register.Register;

public abstract class Instruction_Rd5b3
	extends Instruction_Rd5
{
	private int b;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_Rd5b3( Register rd, int b )
	{
		super( rd );

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

