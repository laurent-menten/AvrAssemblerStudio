package be.lmenten.avr.core.instruction.bit;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.data.CoreStatusRegister;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd5;
import be.lmenten.avr.core.register.Register;

@InstructionDescriptor
(
	opcode = "1001 010d dddd 0101",
	statusRegister = "---*****", 
	syntax = "ASR Rd",
	description = "Arithmetic shift right",
	remark = "Effectively divides a signed value by 2 without changing its sign"
)
public class ASR
	extends Instruction_Rd5
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public ASR( Register rd )
	{
		super( rd );

		int opc = InstructionSet.ASR.getOpcodeMaskValue();
		opc = InstructionSet.ASR.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		setOpcode( opc );
	}

	public static ASR newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.ASR.extractOperand( OperandType.d, opcode );

		return new ASR( Register.lookup( d ) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.ASR;
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

		Value r = rd.compute( (a) -> { return ( (a & 0b1000_0000) | (a >> 1) ); } );

		sreg.c( rd.bit0() );
		sreg.n( r.bit7() );
		sreg.v( sreg.n() ^ sreg.c() );
		sreg.s( sreg.n() ^ sreg.v() );
		sreg.z( r.isZero() );

		_rd.setValue( r );
		core.setStatusRegister( sreg );

		// --------------------------------------------------------------------
		
		core.updateClockCyclesCounter( 1l );
	}
}
