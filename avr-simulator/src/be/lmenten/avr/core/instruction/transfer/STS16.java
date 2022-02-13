package be.lmenten.avr.core.instruction.transfer;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rr4k7;
import be.lmenten.avr.core.register.UpperRegister;

@InstructionDescriptor
(
	opcode = "1010 1kkk rrrr kkkk",
	statusRegister = "--------",
	syntax = "STS k[0..127], Rr",
	description = "Store direct"
)
public class STS16
	extends Instruction_Rr4k7
	implements TransferInstruction
{
	// ========================================================================
	// ===
	// ========================================================================

	public STS16( UpperRegister rr, int k )
	{
		super( rr, k );

		int opc = InstructionSet.STS16.getOpcodeMaskValue();
		opc = InstructionSet.STS16.insertOperand( OperandType.r, opc, rr.getOperandIndex() );
		opc = InstructionSet.STS16.insertOperand( OperandType.k, opc, k );
		setOpcode( opc );
	}

	public static STS16 newInstance( Integer opcode, Integer opcode2 )
	{
		int r = InstructionSet.STS16.extractOperand( OperandType.r, opcode );
		int k = InstructionSet.STS16.extractOperand( OperandType.k, opcode );

		return new STS16( UpperRegister.lookup( r ), k );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.STS16;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public int getTargetAddress()
	{
		return getAddress();
	}
}
