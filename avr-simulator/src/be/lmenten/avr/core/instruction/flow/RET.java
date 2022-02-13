package be.lmenten.avr.core.instruction.flow;

import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;

@InstructionDescriptor
(
	opcode = "1001 0101 0000 1000",
	statusRegister = "--------",
	syntax = "RET",
	description = "Return from subroutine"
)
public class RET
	extends Instruction
{
	// ========================================================================
	// ===
	// ========================================================================

	public RET()
	{
		setOpcode( InstructionSet.RET.getOpcodeMaskValue() );
	}

	public static RET newInstance( Integer opcode, Integer opcode2 )
	{
		return new RET();
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.RET;
	}
}
