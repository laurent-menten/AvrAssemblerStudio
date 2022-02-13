package be.lmenten.avr.core.instruction.flow;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_k12;

@InstructionDescriptor
(
	opcode = "1101 kkkk kkkk kkkk",
	statusRegister = "--------",
	syntax = "RCALL k[-2K..2K]",
	description = "Relative call to subroutine"
)
public class RCALL
	extends Instruction_k12
{
	// ========================================================================
	// ===
	// ========================================================================

	public RCALL( int k )
	{
		super( k );

		int opc = InstructionSet.RCALL.getOpcodeMaskValue();
		opc = InstructionSet.RCALL.insertOperand( OperandType.k, opc, k );
		setOpcode( opc );
	}

	public static RCALL newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.RCALL.extractOperand( OperandType.k, opcode );
		if( (k & 0b0000_1000_0000_0000) == 0b0000_1000_0000_0000 )
		{
			k |= ~0xFFF;
		}

		return new RCALL( k );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.RCALL;
	}
	
	// ========================================================================
	// === 
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		core.pushProgramCounter();
		core.updateProgramCounter( getOffset() * 2 );

		core.updateClockCyclesCounter( 2l );
	}
}
