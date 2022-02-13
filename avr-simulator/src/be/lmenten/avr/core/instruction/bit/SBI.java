package be.lmenten.avr.core.instruction.bit;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_A5b3;

@InstructionDescriptor
(
	opcode = "1001 1010 AAAA Abbb",
	statusRegister = "--------",
	syntax = "SBI A[0..31], b[0..7]",
	description = "Set bit in I/O register"
)
public class SBI
	extends Instruction_A5b3
{
	// ========================================================================
	// ===
	// ========================================================================

	public SBI( int A, int b )
	{
		super( A, b );

		int opc = InstructionSet.SBI.getOpcodeMaskValue();
		opc = InstructionSet.SBI.insertOperand( OperandType.A, opc, getAddress() );
		opc = InstructionSet.SBI.insertOperand( OperandType.b, opc, getBit() );
		setOpcode( opc );
	}

	public static SBI newInstance( Integer opcode, Integer opcode2 )
	{
		int A = InstructionSet.SBI.extractOperand( OperandType.A, opcode );
		int b = InstructionSet.SBI.extractOperand( OperandType.b, opcode );

		return new SBI( A, b );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.SBI;
	}
}
