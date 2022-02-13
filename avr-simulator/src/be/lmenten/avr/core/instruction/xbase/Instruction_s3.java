package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.flow.sreg.BRBC;
import be.lmenten.avr.core.instruction.flow.sreg.BRBS;
import be.lmenten.avr.core.register.StatusRegister;

public abstract class Instruction_s3
	extends Instruction
{
	private StatusRegister s;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_s3( StatusRegister s )
	{
		this.s = s;
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setFlag( StatusRegister s )
	{
		this.s = s;
	}

	public StatusRegister getFlag()
	{
		return s;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public String getOperand1( Core core )
	{
		if( (getInstructionSetEntry() == InstructionSet.BCLR)
				|| (getInstructionSetEntry() == InstructionSet.BSET)
				|| (getInstructionSetEntry() == InstructionSet.BRBC)
				|| (getInstructionSetEntry() == InstructionSet.BRBS) )
		{
			return Integer.toString( s.ordinal() );
		}

		return null;
	}

	@Override
	public String getComment( Core core )
	{
		if( (getInstructionSetEntry() == InstructionSet.BCLR)				
				|| (getInstructionSetEntry() == InstructionSet.BSET) )
		{
			return "(SREG." + s.name() + ")";
		}

		else if( this instanceof BRBC )
		{
			return "(SREG." + s.name() + " == 0)";
		}
		else if( this instanceof BRBS )
		{
			return "(SREG." + s.name() + " == 1)";
		}

		else if( (getInstructionSetEntry() == InstructionSet.CLI)
				|| (getInstructionSetEntry() == InstructionSet.SEI) )
		{
			return getInstructionSetEntry().getDescription();
		}
	
		return null;
	}
}
