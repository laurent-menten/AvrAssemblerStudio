package be.lmenten.avr.core.instruction.transfer;

import static be.lmenten.avr.core.descriptor.CoreVersion.AVRxm;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd5;
import be.lmenten.avr.core.register.Register;

@InstructionDescriptor
(
	opcode = "1001 001d dddd 0100",
	coreVersionSpecific = { AVRxm },
	statusRegister = "--------",
	syntax = "XCH Z, Rd",
	description = "Exchange"
)
public class XCH
	extends Instruction_Rd5
{
	// ========================================================================
	// ===
	// ========================================================================

	public XCH( Register rd )
	{
		super( rd );

		int opc = InstructionSet.XCH.getOpcodeMaskValue();
		opc = InstructionSet.XCH.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		setOpcode( opc );
	}

	public static XCH newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.XCH.extractOperand( OperandType.d, opcode );

		return new XCH( Register.lookup( d ) );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.XCH;
	}
}
