package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.Instruction;

public abstract class Instruction_b3
	extends Instruction
{
	private int b;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_b3( int b )
	{
		setBit( b );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setBit( int b )
	{
		if( (b<0) || (b>7) )
		{
			throw new IllegalArgumentException( "Invalid bit index " + b );
		}
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
	public String getOperand1( Core core )
	{
		return Integer.toString( getBit() );
	}
}
