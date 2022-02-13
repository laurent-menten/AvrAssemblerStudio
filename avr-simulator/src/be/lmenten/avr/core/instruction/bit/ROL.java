package be.lmenten.avr.core.instruction.bit;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.arithmetic.ADC;
import be.lmenten.avr.core.register.Register;

@InstructionDescriptor(
	opcode = "0001 11rd dddd rrrr",
	isAlias = true,
	alias = "ADC Rd, Rd",
	statusRegister = "--******",
	syntax = "ROL Rd",
	description = "Rotate left through carry"
)
public class ROL
	extends ADC
{
	// ========================================================================
	// ===
	// ========================================================================

	public ROL( Register rd )
	{
		super( rd, rd );
	}

	public static ROL newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.ADC.extractOperand( OperandType.d, opcode );
		int r = InstructionSet.ADC.extractOperand( OperandType.r, opcode );

		if( r != d )
		{
			throw new RuntimeException( "ROL: Rd != Rr"  );
		}

		return new ROL( Register.lookup(d) );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.ROL;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public String getOperand2( Core core )
	{
		return null;
	}
}
