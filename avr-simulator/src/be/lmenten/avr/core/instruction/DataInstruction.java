package be.lmenten.avr.core.instruction;

import be.lmenten.avr.core.Core;

public class DataInstruction
	extends Instruction
{
	// ========================================================================
	// ===
	// ========================================================================

	public DataInstruction( int value )
	{
		setOpcode( value );
	}

	public static DataInstruction newInstance( Integer opcode, Integer opcode2 )
	{
		return new DataInstruction( opcode2 & 0xFFFF );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.DATA;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public String getMnemonic()
	{
		return "DATA";
	}

	@Override
	public String getOperand1( Core core )
	{
		return String.format( "0x%04X", getOpcode() );
	}
}
