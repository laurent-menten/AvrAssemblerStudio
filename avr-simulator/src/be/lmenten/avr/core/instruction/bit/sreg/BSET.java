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
	opcode = "1001 0100 0sss 1000",
	statusRegister = "????????",
	syntax = "BSET s[C,Z,N,V,S,H,T,I]",
	description = "Bit set in SREG"
)
public class BSET
	extends Instruction_s3
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BSET( StatusRegister s )
	{
		super( s );

		int opc = InstructionSet.BSET.getOpcodeMaskValue();
		opc = InstructionSet.BSET.insertOperand( OperandType.s, opc, getFlag().getOperandIndex() );
		setOpcode( opc );
	}

	public static BSET newInstance( Integer opcode, Integer opcode2 )
	{
		int s = InstructionSet.BSET.extractOperand( OperandType.s, opcode );
		switch( StatusRegister.lookup( s ) )
		{
			case C: return new SEC();
			case Z:	return new SEZ();
			case N:	return new SEN();
			case V:	return new SEV();
			case S:	return new SES();
			case H:	return new SEH();
			case T:	return new SET();
			case I:	return new SEI();
		}

		throw new RuntimeException( "!!! UNEXPECTED ERROR !!!" );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return getFlag().getBSETAlias();
	}

	// ========================================================================
	// === Simulation =========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreStatusRegister sreg = core.getStatusRegisterCopy();

		sreg.bit( getFlag().ordinal(), true );

		core.setStatusRegister( sreg );

		core.updateClockCyclesCounter( 1l );
		incrementExecutionsCount();
	}
}
