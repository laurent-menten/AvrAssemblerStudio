package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.Instruction;

public abstract class Instruction_K4
	extends Instruction
{
	private int K;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_K4( int K )
	{
		setRound( K );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setRound( int K )
	{
		if( (K<0) || (K>15) )
		{
			throw new IllegalArgumentException( "Invalid round value " + K );
		}
		this.K = K;
	}

	public int getRound()
	{
		return K;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public String getOperand1( Core core )
	{
		return Integer.toString( getRound() );
	}
}
