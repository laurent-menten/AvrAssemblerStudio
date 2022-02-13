package be.lmenten.avr.core.instruction.flow.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1111 01kk kkkk k001",
	isAlias = true,
	statusRegister = "--------",
	syntax = "BRNE k[-64..63]",
	description = "Branch if not equal"
)
public class BRNE
	extends BRBC
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BRNE( int k )
	{
		super( StatusRegister.Z, k );
	}

	public static BRNE newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.BRNE.extractOperand( OperandType.k, opcode );
		if( (k & 0b0100_0000) == (0b0100_0000) )
		{
			k |= ~0b0111_1111;
		}

		return new BRNE( k );
	}
}
