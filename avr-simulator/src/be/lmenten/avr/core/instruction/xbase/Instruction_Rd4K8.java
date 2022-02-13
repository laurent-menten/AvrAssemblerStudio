package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.register.UpperRegister;

public abstract class Instruction_Rd4K8
	extends Instruction_Rd4
{
	private int K;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_Rd4K8( UpperRegister rd, int K )
	{
		super( rd );

		setImmediate( K );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setImmediate( int K )
	{
		if( (K<0) || (K>255) )
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
		return formatImmediate( getImmediate(), 8 );
	}
}
