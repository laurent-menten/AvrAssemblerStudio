package be.lmenten.avr.core.instruction.flow;

import static be.lmenten.avr.core.descriptor.CoreVersion.AVRe;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRe_PLUS;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRxm;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRxt;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_k22;

@InstructionDescriptor
(
	opcode = "1001 010k kkkk 110k",	 is32bits = true,
	coreVersionSpecific = { AVRe, AVRe_PLUS, AVRxm, AVRxt },
	statusRegister = "--------",
	syntax = "JMP k[0..4M]",
	description = "Long jump"
)
public class JMP
	extends Instruction_k22
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public JMP( int k )
	{
		super( k );

		int opc = InstructionSet.JMP.getOpcodeMaskValue();
		opc = InstructionSet.JMP.insertOperand( OperandType.k, opc, k >> 16 );
		setOpcode( opc );
	}

	public static JMP newInstance( Integer opcode, Integer opcode2 )
	{
		int k0 = InstructionSet.JMP.extractOperand( OperandType.k, opcode );

		int k = (k0 << 16) | (opcode2 & 0xFFFF);

		return new JMP( k );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.JMP;
	}

	// ========================================================================
	// === Simulation =========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		core.setProgramCounter( getAddress() * 2 );		

		core.updateClockCyclesCounter( 3l );
	}
}
