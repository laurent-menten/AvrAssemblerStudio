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
	opcode = "1111 111r rrrr 0bbb",
	statusRegister = "--------",
	syntax = "SBRS Rr, b[0..7]",
	description = "Skip if bit in register is set"
)
public class SBRS
	extends Instruction_Rr5b3
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public SBRS( Register rr, int b )
	{
		super( rr, b );

		int opc = InstructionSet.SBRS.getOpcodeMaskValue();
		opc = InstructionSet.SBRS.insertOperand( OperandType.r, opc, rr.getOperandIndex() );
		opc = InstructionSet.SBRS.insertOperand( OperandType.b, opc, b );
		setOpcode( opc );
	}

	public static SBRS newInstance( Integer opcode, Integer opcode2 )
	{
		int r = InstructionSet.SBRS.extractOperand( OperandType.r, opcode );
		int b = InstructionSet.SBRS.extractOperand( OperandType.b, opcode );

		return new SBRS( Register.lookup( r ), b );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.SBRS;
	}

	// ========================================================================
	// === Simulation =========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreRegister rr = core.getRegister( getRr() );

		if( rr.bit( getBit() ) )
		{
			Instruction i = core.getCurrentInstruction();
			
			core.updateProgramCounter( i.getOpcodeSize() * 2 );
			core.updateClockCyclesCounter( i.getOpcodeSize() );
		}

		core.updateClockCyclesCounter( 1l );
	}
}
