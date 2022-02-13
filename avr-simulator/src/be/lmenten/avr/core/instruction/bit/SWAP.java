package be.lmenten.avr.core.instruction.bit;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd5;
import be.lmenten.avr.core.register.Register;

@InstructionDescriptor
(
	opcode = "1001 001d dddd 0010",
	statusRegister = "--------",
	syntax = "SWAP Rd",
	description = "Swap nibbles"
)
public class SWAP
	extends Instruction_Rd5
{
	// ========================================================================
	// ===
	// ========================================================================

	public SWAP( Register rd )
	{
		super( rd );

		int opc = InstructionSet.SWAP.getOpcodeMaskValue();
		opc = InstructionSet.SWAP.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		setOpcode( opc );
	}

	public static SWAP newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.SWAP.extractOperand( OperandType.d, opcode );

		return new SWAP( Register.lookup( d ) );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.SWAP;
	}
}
