package be.lmenten.avr.core.instruction.bit;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_A5b3;

@InstructionDescriptor
(
	opcode = "1001 1000 AAAA Abbb",
	statusRegister = "--------",
	syntax = "CBI A[0..31], b[0..7]",
	description = "Clear bit in I/O register"
)
public class CBI
	extends Instruction_A5b3
{
	// ========================================================================
	// ===
	// ========================================================================

	public CBI( int A, int b )
	{
		super( A, b );

		int opc = InstructionSet.CBI.getOpcodeMaskValue();
		opc = InstructionSet.CBI.insertOperand( OperandType.A, opc, getAddress() );
		opc = InstructionSet.CBI.insertOperand( OperandType.b, opc, getBit() );
		setOpcode( opc );
	}

	public static CBI newInstance( Integer opcode, Integer opcode2 )
	{
		int A = InstructionSet.CBI.extractOperand( OperandType.A, opcode );
		int b = InstructionSet.CBI.extractOperand( OperandType.b, opcode );

		return new CBI( A, b );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.CBI;
	}
}
