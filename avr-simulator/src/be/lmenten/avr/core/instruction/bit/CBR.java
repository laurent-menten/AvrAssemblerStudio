package be.lmenten.avr.core.instruction.bit;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.logic.ANDI;
import be.lmenten.avr.core.register.UpperRegister;

@InstructionDescriptor
(
	opcode = "0111 KKKK dddd KKKK",
	isAlias = true,
	alias = "ANDI Rd, ($FF-K)",
	statusRegister = "---*0**-",
	syntax = "CBRR Rd[16..31], K[0.255]",
	description = "Clear bits in register"
)
public class CBR
	extends ANDI
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public CBR( UpperRegister rd, int K )
	{
		super( rd, ~K );
	}

	public static CBR newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.CBR.extractOperand( OperandType.d, opcode );
		int K = InstructionSet.CBR.extractOperand( OperandType.K, opcode );

		return new CBR( UpperRegister.lookup( d ), ~K );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.CBR;
	}
}
