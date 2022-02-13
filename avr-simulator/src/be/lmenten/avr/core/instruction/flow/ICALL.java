package be.lmenten.avr.core.instruction.flow;

import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;

@InstructionDescriptor
(
	opcode = "1001 0101 0000 1001",
	statusRegister = "--------",
	syntax = "ICALL",
	description = "Indirect call to subroutine"
)
public class ICALL
	extends Instruction
{
	// ========================================================================
	// ===
	// ========================================================================

	public ICALL()
	{
		setOpcode( InstructionSet.ICALL.getOpcodeMaskValue() );
	}

	public static ICALL newInstance( Integer opcode, Integer opcode2 )
	{
		return new ICALL();
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.ICALL;
	}
}
