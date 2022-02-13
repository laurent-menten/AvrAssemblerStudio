package be.lmenten.avr.core.instruction.flow.sreg;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreStatusRegister;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_k7s3;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1111 00kk kkkk ksss",
	statusRegister = "--------",
	syntax = "BRBS s[C,Z,N,V,S,H,I], k[-64..63]",
	description = "Branch is bit in SREG is set"
)
public class BRBS
	extends Instruction_k7s3
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BRBS( StatusRegister s, int k )
	{
		super( s, k );

		int opc = InstructionSet.BRBS.getOpcodeMaskValue();
		opc = InstructionSet.BRBS.insertOperand( OperandType.s, opc, getFlag().ordinal() );
		opc = InstructionSet.BRBS.insertOperand( OperandType.k, opc, getOffset() );
		setOpcode( opc );
	}

	public static BRBS newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.BRBS.extractOperand( OperandType.k, opcode );
		if( (k & 0b0100_0000) == (0b0100_0000) )
		{
			k |= ~0b0111_1111;
		}

		int s = InstructionSet.BRBS.extractOperand( OperandType.s, opcode );
		switch( StatusRegister.lookup( s ) )
		{
			case C: return new BRLO( k );
			case Z:	return new BREQ( k );
			case N:	return new BRMI( k );
			case V:	return new BRVS( k );
			case S:	return new BRLT( k );
			case H:	return new BRHS( k );
			case T:	return new BRTS( k );
			case I:	return new BRIE( k );
		}

		throw new RuntimeException( "!!! UNEXPECTED ERROR !!!" );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return getFlag().getBRBSAlias();
	}

	// ========================================================================
	// === Simulation =========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreStatusRegister sreg = core.getStatusRegisterCopy();

		if( sreg.bit( getFlag().ordinal() ) )
		{
			core.updateProgramCounter( getOffset() * 2 );
			core.updateClockCyclesCounter( 2l );
		}
		else
		{
			core.updateClockCyclesCounter( 1l );
		}

	}
}
