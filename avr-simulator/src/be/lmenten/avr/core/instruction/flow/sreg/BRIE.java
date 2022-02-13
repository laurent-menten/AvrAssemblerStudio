package be.lmenten.avr.core.instruction.flow.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1111 00kk kkkk k111",
	isAlias = true,
	statusRegister = "--------",
	syntax = "BRIE k[-64..63]",
	description = "Branch if global interrupt is enabled"
)
public class BRIE
	extends BRBS
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BRIE( int k )
	{
		super( StatusRegister.I, k );
	}

	public static BRIE newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.BRIE.extractOperand( OperandType.k, opcode );
		if( (k & 0b0100_0000) == (0b0100_0000) )
		{
			k |= ~0b0111_1111;
		}

		return new BRIE( k );
	}
}
