package be.lmenten.avr.core.instruction.flow.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1111 00kk kkkk k000",
	isAlias = true,
	statusRegister = "--------",
	syntax = "BRCS k[-64..63]",
	description = "Branch if carry is set"
)
public class BRCS
	extends BRBS
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BRCS( int k )
	{
		super( StatusRegister.C, k );
	}

	public static BRCS newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.BRCS.extractOperand( OperandType.k, opcode );
		if( (k & 0b0100_0000) == (0b0100_0000) )
		{
			k |= ~0b0111_1111;
		}

		return new BRCS( k );
	}
}
