package be.lmenten.avr.simulator.ui.instructionview;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionSet;

public class FillerInstruction
	extends Instruction
{
	private final int size;

	public FillerInstruction( int address, int size )
	{
		setCellAddress( address );
		this.size = size;
	}

	public int getSize()
	{
		return size;
	}

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.NOP;
	}

	@Override
	public String getComment( Core core )
	{
		if( size > 0 )
		{
			return String.format( "%d bytes of uninitialised space.", getSize() );
		}

		return null;
	}
}
