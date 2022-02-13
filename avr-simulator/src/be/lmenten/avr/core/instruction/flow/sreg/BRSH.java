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
	syntax = "BRSH k[-64..63]",
	description = "Branch if same or higher (unsigned)"
)
public class BRSH
	extends BRBC
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BRSH( int k )
	{
		super( StatusRegister.C, k );
	}

	public static BRSH newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.BRSH.extractOperand( OperandType.k, opcode );
		if( (k & 0b0100_0000) == (0b0100_0000) )
		{
			k |= ~0b0111_1111;
		}

		return new BRSH( k );
	}
}
