package be.lmenten.avr.core.instruction.flow.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1111 00kk kkkk k001",
	isAlias = true,
	statusRegister = "--------",
	syntax = "BREQ k[-64..63]",
	description = "Branch if equal"
)
public class BREQ
	extends BRBS
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BREQ( int k )
	{
		super( StatusRegister.Z, k );
	}

	public static BREQ newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.BREQ.extractOperand( OperandType.k, opcode );
		if( (k & 0b0100_0000) == (0b0100_0000) )
		{
			k |= ~0b0111_1111;
		}

		return new BREQ( k );
	}
}
