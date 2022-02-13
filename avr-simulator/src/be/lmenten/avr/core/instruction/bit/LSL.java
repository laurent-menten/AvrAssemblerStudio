package be.lmenten.avr.core.instruction.bit;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.arithmetic.ADD;
import be.lmenten.avr.core.register.Register;

@InstructionDescriptor(
	opcode = "0000 11rd dddd rrrr",
	isAlias = true,
	alias = "ADD Rd, Rd",
	statusRegister = "--******",
	syntax = "LSL Rd",
	description = "Logical shift left"
)
public class LSL
	extends ADD
{
	// ========================================================================
	// ===
	// ========================================================================

	public LSL( Register rd )
	{
		super( rd, rd );
	}

	public static LSL newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.ADD.extractOperand( OperandType.d, opcode );
		int r = InstructionSet.ADD.extractOperand( OperandType.r, opcode );

		if( r != d )
		{
			throw new RuntimeException( "LSL: Rd != Rr"  );
		}

		return new LSL( Register.lookup(d) );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.LSL;
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
