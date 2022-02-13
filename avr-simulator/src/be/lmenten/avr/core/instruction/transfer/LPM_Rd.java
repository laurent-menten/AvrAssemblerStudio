package be.lmenten.avr.core.instruction.transfer;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.event.CoreEvent;
import be.lmenten.avr.core.event.CoreEventType;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd5;
import be.lmenten.avr.core.register.Register;

@InstructionDescriptor
(
	opcode = "1001 000d dddd 010x",
	statusRegister = "--------",
	syntax = "LPM",
	description = "Load program memory (from PS(Z) to Rd)"
)
public class LPM_Rd
	extends Instruction_Rd5
{
	private boolean postIncrement;

	// ========================================================================
	// ===
	// ========================================================================

	public LPM_Rd( Register rd, boolean postIncrement )
	{
		super( rd );

		this.postIncrement = postIncrement;

		int opc = InstructionSet.LPM_Rd.getOpcodeMaskValue();
		opc = InstructionSet.LPM_Rd.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		opc = InstructionSet.LPM_Rd.insertOperand( OperandType.x, opc, this.postIncrement ? 1 : 0 );
		setOpcode( opc );
	}

	public static LPM_Rd newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.LPM_Rd.extractOperand( OperandType.d, opcode );
		int x = InstructionSet.LPM_Rd.extractOperand( OperandType.x, opcode );

		return new LPM_Rd( Register.lookup( d ), x == 0 ? false : true );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.LPM_Rd;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public String getMnemonic()
	{
		return InstructionSet.LPM.name();
	}

	@Override
	public String getOperand1( Core core )
	{
		if( (getRd() == Register.R0) && ! postIncrement )
		{
			return null;
		}

		return super.getOperand1(core);
	}

	@Override
	public String getOperand2( Core core )
	{
		if( (getRd() == Register.R0) && ! postIncrement )
		{
			return null;
		}

		return "Z" + (postIncrement ? "+" : "");
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreRegister _rd = core.getRegister( this.getRd() );

		CoreRegister _rrl = core.getRegister( Register.ZL );
		int rrl = _rrl.getData() & 0xFF;
		_rrl.recordReadAccess( core, this );
		CoreRegister _rrh = core.getRegister( Register.ZH );
		int rrh = _rrh.getData() & 0xFF;
		_rrh.recordReadAccess( core, this );

		int address = ((rrh & 0xFF) << 8) | (rrl & 0xFF);

		// --------------------------------------------------------------------

		Instruction i = core.getInstruction( address );
		if( i == null )
		{
			CoreEvent event = new CoreEvent( CoreEventType.FLASH_MEMORY_UNINITIALIZED, core );
			event.setInstruction( this );
			event.setTargetAddress( address );
			core.fireCoreEvent( event );

			throw new RuntimeException( "Flash memory uninitialised" );
		}

		Value v = i.getValue();
		i.recordReadAccess( core, this );

		_rd.setData( (address & 1) == 1 ? (v.getData() >> 8) & 0xFF : v.getData() &  0xFF );
		_rd.recordWriteAccess( core, this );

		if( postIncrement )
		{
			address++;

			if( core.getDescriptor().getOnChipSramSize() > 256 )
			{
				_rrh.setData( (address >> 8) & 0xFF );
				_rrh.recordWriteAccess( core, this );
			}

			_rrl.setData( address & 0xFF );
			_rrl.recordWriteAccess( core, this );
		}

		// --------------------------------------------------------------------

		core.updateClockCyclesCounter( 3 );
		incrementExecutionsCount();
	}
}
