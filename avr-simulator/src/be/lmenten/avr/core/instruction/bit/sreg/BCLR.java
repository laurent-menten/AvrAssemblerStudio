package be.lmenten.avr.core.instruction.bit.sreg;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreStatusRegister;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_s3;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1001 0100 1sss 1000",
	statusRegister = "????????",
	syntax = "BCLR s[C,Z,N,V,S,H,T,I]",
	description = "Bit clear in SREG"
)
public class BCLR
	extends Instruction_s3
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BCLR( StatusRegister s )
	{
		super( s );

		int opc = InstructionSet.BCLR.getOpcodeMaskValue();
		opc = InstructionSet.BCLR.insertOperand( OperandType.s, opc, getFlag().getOperandIndex() );
		setOpcode( opc );
	}

	public static BCLR newInstance( Integer opcode, Integer opcode2 )
	{
		int s = InstructionSet.BCLR.extractOperand( OperandType.s, opcode );
		switch( StatusRegister.lookup( s ) )
		{
			case C: return new CLC();
			case Z:	return new CLZ();
			case N:	return new CLN();
			case V:	return new CLV();
			case S:	return new CLS();
			case H:	return new CLH();
			case T:	return new CLT();
			case I:	return new CLI();
		}

		throw new RuntimeException( "!!! UNEXPECTED ERROR !!!" );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return getFlag().getBCLRAlias();
	}

	// ========================================================================
	// === Simulation =========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreStatusRegister sreg = core.getStatusRegisterCopy();

		sreg.bit( getFlag().ordinal(), false );

		core.setStatusRegister( sreg );

		core.updateClockCyclesCounter( 1l );
		incrementExecutionsCount();
	}
}
