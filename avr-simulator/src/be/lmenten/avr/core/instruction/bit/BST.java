package be.lmenten.avr.core.instruction.bit;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.data.CoreStatusRegister;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rr5b3;
import be.lmenten.avr.core.register.Register;

@InstructionDescriptor
(
	opcode = "1111 101r rrrr 0bbb",
	statusRegister = "--------",
	syntax = "BST Rr, s[0..7]",
	description = "Bit store from bit in register to T flag in SREG"
)
public class BST
	extends Instruction_Rr5b3
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BST( Register rr, int b )
	{
		super( rr, b );

		int opc = InstructionSet.BST.getOpcodeMaskValue();
		opc = InstructionSet.BST.insertOperand( OperandType.r, opc, rr.getOperandIndex() );
		opc = InstructionSet.BST.insertOperand( OperandType.b, opc, getBit() );
		setOpcode( opc );
	}

	public static BST newInstance( Integer opcode, Integer opcode2 )
	{
		int r = InstructionSet.BST.extractOperand( OperandType.r, opcode );
		int b = InstructionSet.BST.extractOperand( OperandType.b, opcode );

		return new BST( Register.lookup( r ), b );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.BST;
	}
	
	// ========================================================================
	// === Simulation =========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreStatusRegister sreg = core.getStatusRegisterCopy();

		CoreRegister _rr = core.getRegister( this.getRr() );
		Value rr = _rr.getValue();
		
		// --------------------------------------------------------------------

		sreg.t( rr.bit( getBit() ) );

		core.setStatusRegister( sreg );

		// --------------------------------------------------------------------
		
		core.updateClockCyclesCounter( 1l );
	}
}
