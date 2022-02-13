package be.lmenten.avr.core.instruction.transfer;

import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;

@InstructionDescriptor
(
	opcode = "1001 0101 1100 1000",
	statusRegister = "--------",
	syntax = "LPM",
	description = "Load program memory (from PS(Z) to R0)"
)
public class LPM
	extends Instruction
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public LPM()
	{
		setOpcode( InstructionSet.LPM.getOpcodeMaskValue() );
	}

	public static LPM newInstance( Integer opcode, Integer opcode2 )
	{
		return new LPM();
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.LPM;
	}
}
