package be.lmenten.avr.core.instruction.flow.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1111 01kk kkkk k011",
	isAlias = true,
	statusRegister = "--------",
	syntax = "BRVC k[-64..63]",
	description = "Branch if overflow flag is cleared"
)
public class BRVC
	extends BRBC
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BRVC( int k )
	{
		super( StatusRegister.V, k );
	}

	public static BRVC newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.BRVC.extractOperand( OperandType.k, opcode );
		if( (k & 0b0100_0000) == (0b0100_0000) )
		{
			k |= ~0b0111_1111;
		}

		return new BRVC( k );
	}
}
