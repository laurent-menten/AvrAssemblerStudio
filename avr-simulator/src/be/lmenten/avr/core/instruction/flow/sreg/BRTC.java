package be.lmenten.avr.core.instruction.flow.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1111 01kk kkkk k110",
	isAlias = true,
	statusRegister = "--------",
	syntax = "BRTC k[-64..63]",
	description = "Branch if T flag is cleared"
)
public class BRTC
	extends BRBC
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BRTC( int k )
	{
		super( StatusRegister.T, k );
	}

	public static BRTC newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.BRTC.extractOperand( OperandType.k, opcode );
		if( (k & 0b0100_0000) == (0b0100_0000) )
		{
			k |= ~0b0111_1111;
		}

		return new BRTC( k );
	}
}
