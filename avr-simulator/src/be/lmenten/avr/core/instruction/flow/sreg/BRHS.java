package be.lmenten.avr.core.instruction.flow.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1111 00kk kkkk k101",
	isAlias = true,
	statusRegister = "--------",
	syntax = "BRHS k[-64..63]",
	description = "Branch if half carry flag set"
)
public class BRHS
	extends BRBS
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BRHS( int k )
	{
		super( StatusRegister.H, k );
	}

	public static BRHS newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.BRHS.extractOperand( OperandType.k, opcode );
		if( (k & 0b0100_0000) == (0b0100_0000) )
		{
			k |= ~0b0111_1111;
		}

		return new BRHS( k );
	}
}
