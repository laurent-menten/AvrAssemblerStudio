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
	opcode = "1111 01kk kkkk ksss",
	statusRegister = "--------",
	syntax = "BRBC s[C,Z,N,V,S,H,I], k[-64..63]",
	description = "Branch is bit in SREG is cleared"
)
public class BRBC
	extends Instruction_k7s3
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public BRBC( StatusRegister s, int k )
	{
		super( s, k );

		int opc = InstructionSet.BRBC.getOpcodeMaskValue();
		opc = InstructionSet.BRBC.insertOperand( OperandType.s, opc, getFlag().ordinal() );
		opc = InstructionSet.BRBC.insertOperand( OperandType.k, opc, getOffset() );
		setOpcode( opc );
	}

	public static BRBC newInstance( Integer opcode, Integer opcode2 )
	{
		int k = InstructionSet.BRBC.extractOperand( OperandType.k, opcode );
		if( (k & 0b0100_0000) == (0b0100_0000) )
		{
			k |= ~0b0111_1111;
		}

		int s = InstructionSet.BRBC.extractOperand( OperandType.s, opcode );
		switch( StatusRegister.lookup( s ) )
		{
			case C: return new BRSH( k );
			case Z:	return new BRNE( k );
			case N:	return new BRPL( k );
			case V:	return new BRVC( k );
			case S:	return new BRGE( k );
			case H:	return new BRHC( k );
			case T:	return new BRTC( k );
			case I:	return new BRID( k );
		}

		throw new RuntimeException( "!!! UNEXPECTED ERROR !!!" );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return getFlag().getBRBCAlias();
	}

	// ========================================================================
	// === Simulation =========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreStatusRegister sreg = core.getStatusRegisterCopy();

		if( ! sreg.bit( getFlag().ordinal() ) )
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
