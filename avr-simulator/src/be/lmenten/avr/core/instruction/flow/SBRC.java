package be.lmenten.avr.core.instruction.flow;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rr5b3;
import be.lmenten.avr.core.register.Register;

@InstructionDescriptor
(
	opcode = "1111 110r rrrr 0bbb",
	statusRegister = "--------",
	syntax = "SBRC Rr, b[0..7]",
	description = "Skip if bit in register is cleared"
)
public class SBRC
	extends Instruction_Rr5b3
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public SBRC( Register rr, int b )
	{
		super( rr, b );

		int opc = InstructionSet.SBRC.getOpcodeMaskValue();
		opc = InstructionSet.SBRC.insertOperand( OperandType.r, opc, rr.getOperandIndex() );
		opc = InstructionSet.SBRC.insertOperand( OperandType.b, opc, b );
		setOpcode( opc );
	}

	public static SBRC newInstance( Integer opcode, Integer opcode2 )
	{
		int r = InstructionSet.SBRC.extractOperand( OperandType.r, opcode );
		int b = InstructionSet.SBRC.extractOperand( OperandType.b, opcode );

		return new SBRC( Register.lookup( r ), b );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.SBRC;
	}

	// ========================================================================
	// === Simulation =========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreRegister rr = core.getRegister( getRr() );

		if( ! rr.bit( getBit() ) )
		{
			Instruction i = core.getCurrentInstruction();
			
			core.updateProgramCounter( i.getOpcodeSize() * 2 );
			core.updateClockCyclesCounter( i.getOpcodeSize() );
		}

		core.updateClockCyclesCounter( 1l );
	}
}
