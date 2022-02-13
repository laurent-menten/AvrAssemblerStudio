package be.lmenten.avr.core.instruction.flow.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1111 00kk kkkk k011",
	isAlias = true,
	statusRegister = "--------",
	syntax = "BRVS k[-64..63]",
	description = "Branch if overflow flag is set"
)
public class BRVS
	extends BRBS
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BRVS( int k )
	{
		super( StatusRegister.V, k );
	}

	public static BRVS newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.BRVS.extractOperand( OperandType.k, opcode );
		if( (k & 0b0100_0000) == (0b0100_0000) )
		{
			k |= ~0b0111_1111;
		}

		return new BRVS( k );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public String toString()
	{
		StringBuffer s = new StringBuffer();

		s.append( super.toString() )
		 .append( ' ' )
		 .append( getOffset() )
		 ;

		return s.toString();
	}
}
