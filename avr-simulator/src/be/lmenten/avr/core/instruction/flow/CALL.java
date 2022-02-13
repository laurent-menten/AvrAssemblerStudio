package be.lmenten.avr.core.instruction.flow;

import static be.lmenten.avr.core.descriptor.CoreVersion.AVRe;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRe_PLUS;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRxm;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRxt;

import java.util.logging.Logger;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_k22;

@InstructionDescriptor
(
	opcode = "1001 010k kkkk 111k",	 is32bits = true,
	coreVersionSpecific = { AVRe, AVRe_PLUS, AVRxm, AVRxt },
	statusRegister = "--------",
	syntax = "CALL k[0..4M]",
	description = "Long call to a subroutine"
)
public class CALL
	extends Instruction_k22
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public CALL( int k )
	{
		super( k );

		int opc = InstructionSet.CALL.getOpcodeMaskValue();
		opc = InstructionSet.CALL.insertOperand( OperandType.k, opc, k >> 16 );
		setOpcode( opc );
	}

	public static CALL newInstance( Integer opcode, Integer opcode2 )
	{
		int k0 = InstructionSet.CALL.extractOperand( OperandType.k, opcode );

		int k = (k0 << 16) | (opcode2 & 0xFFFF);

		return new CALL( k );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.CALL;
	}

	// ========================================================================
	// === Simulation =========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		core.pushProgramCounter();
		core.setProgramCounter( getAddress() * 2 );		

		int pcSize = core.getDescriptor().getProgramCounterWidth();
		if( pcSize <= 16 )
		{
			core.updateClockCyclesCounter( 4l );			
		}
		else if( pcSize <= 24 )
		{
			core.updateClockCyclesCounter( 5l );			
		}
		else
		{
			LOG.warning( "Unexpected PC width : " + pcSize );
		}
	}

	// ========================================================================
	// === LOGGING ============================================================
	// ========================================================================

	private static final Logger LOG
		= Logger.getLogger( CALL.class.getName() );
}
