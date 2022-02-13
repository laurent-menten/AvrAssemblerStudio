package be.lmenten.avr.core.instruction.flow;

import static be.lmenten.avr.core.descriptor.CoreVersion.AVRe_PLUS;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRxm;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRxt;

import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;

@InstructionDescriptor
(
	opcode = "1001 0101 0001 1001",
	coreVersionSpecific = { AVRe_PLUS, AVRxm, AVRxt },
	statusRegister = "--------",
	syntax = "EICALL",
	description = "Extended indirect call to subroutine"
)
public class EICALL
	extends Instruction
{
	// ========================================================================
	// ===
	// ========================================================================

	public EICALL()
	{
		setOpcode( InstructionSet.EICALL.getOpcodeMaskValue() );
	}

	public static EICALL newInstance( Integer opcode, Integer opcode2 )
	{
		return new EICALL();
	}
	
	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.EICALL;
	}
}
