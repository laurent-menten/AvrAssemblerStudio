package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.descriptor.CoreMemory;
import be.lmenten.avr.core.instruction.DataInstruction;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.flow.FlowInstruction;

public abstract class Instruction_k22
	extends Instruction
	implements FlowInstruction
{
	private int k;
	private DataInstruction secondWord;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_k22( int k )
	{
		setAddress( k );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public void setCellAddress( int address )
	{
		super.setCellAddress(address);

		secondWord.setCellAddress( address + 2 );
	}

	protected void setAddress( int k )
	{
		if( (k<0) || (k>0x3FFFFF) )
		{
			throw new IllegalArgumentException( "Invalid offset value " + k );
		}
		this.k = k;

		secondWord = new DataInstruction( k & 0xFFFF );
	}

	public int getAddress()
	{
		return k;
	}

	public DataInstruction getSecondWord()
	{
		return secondWord;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public int getTargetAddress()
	{
		return getAddress();
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
		int address = getTargetAddress();

		if( core != null )
		{
			String symbol = core.getSymbol( CoreMemory.FLASH, address );
			if( symbol != null )
			{
				return String.format( "0x%05X", address );
			}
		}

		return null;
	}
}
