package be.lmenten.avr.core.instruction.flow.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1111 01kk kkkk k111",
	isAlias = true,
	statusRegister = "--------",
	syntax = "BRID k[-64..63]",
	description = "Branch if global interrupt is disabled"
)
public class BRID
	extends BRBC
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BRID( int k )
	{
		super( StatusRegister.I, k );
	}

	public static BRID newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.BRID.extractOperand( OperandType.k, opcode );
		if( (k & 0b0100_0000) == (0b0100_0000) )
		{
			k |= ~0b0111_1111;
		}

		return new BRID( k );
	}
}
