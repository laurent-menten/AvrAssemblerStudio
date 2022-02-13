package be.lmenten.avr.core.instruction.flow;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_A5b3;

@InstructionDescriptor
(
	opcode = "1001 1011 AAAA Abbb",
	statusRegister = "--------",
	syntax = "SBIS A[0..31], b[0..7]",
	description = "Skip if bit in I/O register set"
)
public class SBIS
	extends Instruction_A5b3
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public SBIS( int A, int b )
	{
		super( A, b );

		int opc = InstructionSet.SBIS.getOpcodeMaskValue();
		opc = InstructionSet.SBIS.insertOperand( OperandType.A, opc, getAddress() );
		opc = InstructionSet.SBIS.insertOperand( OperandType.b, opc, getBit() );
		setOpcode( opc );
	}

	public static SBIS newInstance( Integer opcode, Integer opcode2 )
	{
		int A = InstructionSet.SBIS.extractOperand( OperandType.A, opcode );
		int b = InstructionSet.SBIS.extractOperand( OperandType.b, opcode );

		return new SBIS( A, b );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.SBIS;
	}

	// ========================================================================
	// === Simulation =========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreRegister io = core.getIORegisterByAddress( getAddress() );

		if( io.bit( getBit() ) )
		{
			Instruction i = core.getCurrentInstruction();
			
			core.updateProgramCounter( i.getOpcodeSize() * 2 );
			core.updateClockCyclesCounter( i.getOpcodeSize() );
		}

		core.updateClockCyclesCounter( 1l );
	}
}
