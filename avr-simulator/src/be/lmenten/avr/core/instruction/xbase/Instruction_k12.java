package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.descriptor.CoreMemory;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.flow.FlowInstruction;

public abstract class Instruction_k12
	extends Instruction
	implements FlowInstruction
{
	private int k;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_k12( int k )
	{
		setAddress( k );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setAddress( int k )
	{
		if( (k<(-2*1024)) || (k>(2*1024)) )
		{
			throw new IllegalArgumentException( "Invalid offset value " + k );
		}
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
		return String.format( "%s%d", (getOffset() >= 0 ? "+" : ""), getOffset() );
	}
}
