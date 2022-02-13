package be.lmenten.avr.core.instruction.bit;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.data.CoreStatusRegister;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd5b3;
import be.lmenten.avr.core.register.Register;

@InstructionDescriptor
(
	opcode = "1111 100d dddd 0bbb",
	statusRegister = "--------",
	syntax = "BLD Rd, b[0..7]",
	description = "Bit load from T flag in SREG to bit in register"
)
public class BLD
	extends Instruction_Rd5b3
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BLD( Register rd, int b )
	{
		super( rd, b );

		int opc = InstructionSet.BLD.getOpcodeMaskValue();
		opc = InstructionSet.BLD.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		opc = InstructionSet.BLD.insertOperand( OperandType.b, opc, getBit() );
		setOpcode( opc );
	}

	public static BLD newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.BLD.extractOperand( OperandType.d, opcode );
		int b = InstructionSet.BLD.extractOperand( OperandType.b, opcode );

		return new BLD( Register.lookup( d ), b );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.BLD;
	}
	
	// ========================================================================
	// === Simulation =========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreStatusRegister sreg = core.getStatusRegisterCopy();

		CoreRegister _rd = core.getRegister( this.getRd() );
		Value rd = _rd.getValue();
		
		// --------------------------------------------------------------------

		rd.bit( getBit(), sreg.t() );

		_rd.setValue( rd );
		
		// --------------------------------------------------------------------
		
		core.updateClockCyclesCounter( 1l );
	}
}
