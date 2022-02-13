package be.lmenten.avr.core.instruction.flow.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1111 00kk kkkk k110",
	isAlias = true,
	statusRegister = "--------",
	syntax = "BRTS k[-64..63]",
	description = "Branch if T flag is set"
)
public class BRTS
	extends BRBS
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BRTS( int k )
	{
		super( StatusRegister.T, k );
	}

	public static BRTS newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.BRTS.extractOperand( OperandType.k, opcode );
		if( (k & 0b0100_0000) == (0b0100_0000) )
		{
			k |= ~0b0111_1111;
		}

		return new BRTS( k );
	}
}
