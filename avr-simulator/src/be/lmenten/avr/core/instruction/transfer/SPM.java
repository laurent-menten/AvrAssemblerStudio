package be.lmenten.avr.core.instruction.transfer;

import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;

@InstructionDescriptor
(
	opcode = "1001 0101 111x 1000",
	statusRegister = "--------",
	syntax = "SPM",
	description = "Store program memory"
)
public class SPM
	extends Instruction
{
	@SuppressWarnings("unused")
	private boolean postIncrement;

	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public SPM()
	{
		this( false );
	}

	public SPM( boolean postIncrement )
	{
		this.postIncrement = postIncrement;

		int opc = InstructionSet.SPM.getOpcodeMaskValue();
		opc = InstructionSet.SPM.insertOperand( OperandType.x, opc, postIncrement ? 1 : 0 );
		setOpcode( opc );
	}

	public static SPM newInstance( Integer opcode, Integer opcode2 )
	{
		int x = InstructionSet.SPM.extractOperand( OperandType.x, opcode );

		return new SPM( x == 0 ? false : true );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.SPM;
	}
}
