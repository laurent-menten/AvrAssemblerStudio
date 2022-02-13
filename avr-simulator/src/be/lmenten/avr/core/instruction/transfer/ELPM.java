package be.lmenten.avr.core.instruction.transfer;

import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;

@InstructionDescriptor
(
	opcode = "1001 0101 1101 1000",
	statusRegister = "--------",
	syntax = "EPLM",
	description = "Extended load program memory (from PS(RAMPZ:Z) to R0)"
)
public class ELPM
	extends Instruction
{
	// ========================================================================
	// ===
	// ========================================================================

	public ELPM()
	{
		setOpcode( InstructionSet.ELPM.getOpcodeMaskValue() );
	}

	public static ELPM newInstance( Integer opcode, Integer opcode2 )
	{
		return new ELPM();
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.ELPM;
	}
}
