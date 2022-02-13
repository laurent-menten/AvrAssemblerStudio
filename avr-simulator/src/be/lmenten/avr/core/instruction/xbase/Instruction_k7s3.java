package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.descriptor.CoreMemory;
import be.lmenten.avr.core.instruction.flow.FlowInstruction;
import be.lmenten.avr.core.register.StatusRegister;

public abstract class Instruction_k7s3
	extends Instruction_s3
	implements FlowInstruction
{
	private int k;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_k7s3( StatusRegister s, int k )
	{
		super( s );

		if( (k<-64) || (k>63) )
		{
			throw new RuntimeException( "Invalid offset " + k + " for " + getInstructionSetEntry() );			
		}
		this.k = k;
	}

	// ========================================================================
	// ===
	// ========================================================================

	public void getOffset( int k )
	{
		this.k = k;
	}

	public int getOffset()
	{
		return k;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public int getTargetAddress()
	{
		return getCellAddress() + ((getOpcodeSize() + getOffset()) * 2);
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public String getOperand1( Core core )
	{
		int address = getTargetAddress();

		if( core != null )
		{
			String symbol = core.getSymbol( CoreMemory.FLASH, address );
			if( symbol != null )
			{
				return symbol;
			}
		}

		return String.format( "0x%05X", address );
	}
	
	@Override
	public String getComment( Core core )
	{
		String text = String.format( "%s%d", (getOffset() >= 0 ? "+" : ""), getOffset() );

		String comment = super.getComment( core );
		if( comment != null )
		{
			text = comment + ",  " + text;
		}

		return text;
	}
}
