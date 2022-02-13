package be.lmenten.avr.core.instruction.flow;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_k12;

@InstructionDescriptor
(
	opcode = "1100 kkkk kkkk kkkk",
	statusRegister = "--------",
	syntax = "RJMP k[-2K..2K]",
	description = "Relative jump"
)
public class RJMP
	extends Instruction_k12
{
	// ========================================================================
	// ===
	// ========================================================================

	public RJMP( int k )
	{
		super( k );

		int opc = InstructionSet.RJMP.getOpcodeMaskValue();
		opc = InstructionSet.RJMP.insertOperand( OperandType.k, opc, k );
		setOpcode( opc );
	}

	public static RJMP newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.RJMP.extractOperand( OperandType.k, opcode );
		if( (k & 0b0000_1000_0000_0000) == 0b0000_1000_0000_0000 )
		{
			k |= ~0xFFF;
		}

		return new RJMP( k );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.RJMP;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		core.updateProgramCounter( getOffset() * 2 );

		core.updateClockCyclesCounter( 2l );
	}
}
