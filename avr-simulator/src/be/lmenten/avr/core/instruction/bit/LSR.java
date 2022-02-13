package be.lmenten.avr.core.instruction.bit;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd5;
import be.lmenten.avr.core.register.Register;

@InstructionDescriptor
(
	opcode = "1001 010d dddd 0110",
	statusRegister = "---**0**",
	syntax = "LSR Rd",
	description = "Logical shift right"
)
public class LSR
	extends Instruction_Rd5
{
	// ========================================================================
	// ===
	// ========================================================================

	public LSR( Register rd )
	{
		super( rd );

		int opc = InstructionSet.LSR.getOpcodeMaskValue();
		opc = InstructionSet.LSR.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		setOpcode( opc );
	}

	public static LSR newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.LSR.extractOperand( OperandType.d, opcode );

		return new LSR( Register.lookup( d ) );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.LSR;
	}
}
