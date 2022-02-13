package be.lmenten.avr.core.instruction.bit;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd5;
import be.lmenten.avr.core.register.Register;

@InstructionDescriptor
(
	opcode = "1001 010d dddd 0111",
	statusRegister = "---*****",
	syntax = "ROR Rd",
	description = "Rotate right through carry"
)
public class ROR
	extends Instruction_Rd5
{
	// ========================================================================
	// ===
	// ========================================================================

	public ROR( Register rd )
	{
		super( rd );

		int opc = InstructionSet.ROR.getOpcodeMaskValue();
		opc = InstructionSet.ROR.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		setOpcode( opc );
	}

	public static ROR newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.ROR.extractOperand( OperandType.d, opcode );

		return new ROR( Register.lookup( d ) );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.ROR;
	}
}
