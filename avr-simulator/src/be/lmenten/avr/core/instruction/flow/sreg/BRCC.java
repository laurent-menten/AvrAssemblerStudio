package be.lmenten.avr.core.instruction.flow.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1111 01kk kkkk k000",
	isAlias = true,
	statusRegister = "--------",
	syntax = "BRCC k[-64..63]",
	description = "Branch if carry is cleared"
)
public class BRCC
	extends BRBC
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BRCC( int k )
	{
		super( StatusRegister.C, k );
	}

	public static BRCC newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.BRCC.extractOperand( OperandType.k, opcode );
		if( (k & 0b0100_0000) == (0b0100_0000) )
		{
			k |= ~0b0111_1111;
		}

		return new BRCC( k );
	}
}
