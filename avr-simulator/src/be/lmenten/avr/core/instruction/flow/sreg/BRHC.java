package be.lmenten.avr.core.instruction.flow.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1111 01kk kkkk k101",
	isAlias = true,
	statusRegister = "--------",
	syntax = "BRHC k[-64..63]",
	description = "Branch if half carry flag cleared"
)
public class BRHC
	extends BRBC
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BRHC( int k )
	{
		super( StatusRegister.H, k  );
	}

	public static BRHC newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.BRHC.extractOperand( OperandType.k, opcode );
		if( (k & 0b0100_0000) == (0b0100_0000) )
		{
			k |= ~0b0111_1111;
		}

		return new BRHC( k );
	}
}
