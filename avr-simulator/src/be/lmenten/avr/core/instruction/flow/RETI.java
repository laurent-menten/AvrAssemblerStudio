package be.lmenten.avr.core.instruction.flow;

import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;

@InstructionDescriptor
(
	opcode = "1001 0101 0001 1000",
	statusRegister = "--------",
	syntax = "RETI",
	description = "Return from interrupt service routine"
)
public class RETI
	extends Instruction
{
	// ========================================================================
	// ===
	// ========================================================================

	public RETI()
	{
		setOpcode( InstructionSet.RETI.getOpcodeMaskValue() );
	}

	public static RETI newInstance( Integer opcode, Integer opcode2 )
	{
		return new RETI();
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.RETI;
	}
}
