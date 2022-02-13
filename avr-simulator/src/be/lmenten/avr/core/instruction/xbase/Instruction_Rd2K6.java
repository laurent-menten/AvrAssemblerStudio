package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.register.UpperRegisterPair;

public abstract class Instruction_Rd2K6
	extends Instruction_Rd2
{
	private int K;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_Rd2K6( UpperRegisterPair rd, int K )
	{
		super( rd );

		setImmediate( K );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setImmediate( int K )
	{
		if( (K < 0) || (K>63) )
		{
			throw new IllegalArgumentException( "Invalid immediate value " + K );
		}
		this.K = K;
	}

	public int getImmediate()
	{
		return K;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public String getOperand2( Core core )
	{
		return String.format( "0x%02X", getImmediate() );
	}

	@Override
	public String getComment( Core core )
	{
		return formatImmediate( getImmediate(), 6 );
	}
}
