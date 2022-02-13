package be.lmenten.avr.core.instruction.bit;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.logic.ORI;
import be.lmenten.avr.core.register.UpperRegister;

@InstructionDescriptor
(
	opcode = "0110 KKKK dddd KKKK",
	isAlias = true,
	alias = "ORI Rd, K",
	statusRegister = "---*0**-",
	syntax = "SBR Rd[16.31], K[0..255]",
	description = "Set bits in register"
)
public class SBR
	extends ORI
{
	// ========================================================================
	// ===
	// ========================================================================

	public SBR( UpperRegister rd, int K )
	{
		super( rd, K );
	}

	public static SBR newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.SBR.extractOperand( OperandType.d, opcode );
		int K = InstructionSet.SBR.extractOperand( OperandType.K, opcode );

		return new SBR( UpperRegister.lookup( d ), K );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.SBR;
	}
}
