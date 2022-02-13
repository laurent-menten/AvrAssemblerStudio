package be.lmenten.avr.core.instruction.flow.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1111 01kk kkkk k010",
	isAlias = true,
	statusRegister = "--------",
	syntax = "BRPL k[-64..63]",
	description = "Branch if plus"
)
public class BRPL
	extends BRBC
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BRPL( int k )
	{
		super( StatusRegister.N, k );
	}

	public static BRPL newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.BRPL.extractOperand( OperandType.k, opcode );
		if( (k & 0b0100_0000) == (0b0100_0000) )
		{
			k |= ~0b0111_1111;
		}

		return new BRPL( k );
	}
}
