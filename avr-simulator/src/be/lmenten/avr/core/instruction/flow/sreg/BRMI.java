package be.lmenten.avr.core.instruction.flow.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1111 00kk kkkk k010",
	isAlias = true,
	statusRegister = "--------",
	syntax = "BRMI k[-64..63]",
	description = "Branch if minus"
)
public class BRMI
	extends BRBS
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BRMI( int k )
	{
		super( StatusRegister.N, k );
	}

	public static BRMI newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.BRMI.extractOperand( OperandType.k, opcode );
		if( (k & 0b0100_0000) == (0b0100_0000) )
		{
			k |= ~0b0111_1111;
		}

		return new BRMI( k );
	}
}
