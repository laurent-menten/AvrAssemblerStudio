package be.lmenten.avr.core.instruction.transfer;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreData;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rr5k16;
import be.lmenten.avr.core.register.Register;

@InstructionDescriptor
(
	opcode = "1001 001r rrrr 0000",
	is32bits = true,
	statusRegister = "--------",
	syntax = "STS k[0..65535], Rr",
	description = "Store direct"
)
public class STS
	extends Instruction_Rr5k16
	implements TransferInstruction
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public STS( Register rr, int k )
	{
		super( rr, k );

		int opc = InstructionSet.STS.getOpcodeMaskValue();
		opc = InstructionSet.STS.insertOperand( OperandType.r, opc, rr.getOperandIndex() );
		setOpcode( opc );
	}

	public static STS newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.STS.extractOperand( OperandType.r, opcode );

		return new STS( Register.lookup( d ), opcode2 );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.STS;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public int getTargetAddress()
	{
		return getAddress();
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreData _v = core.getSramCell( getTargetAddress() );

		CoreRegister _rr = core.getRegister( this.getRr() );
		Value v = _rr.getValue();

		// --------------------------------------------------------------------

		_v.setValue( v );
		_v.recordWriteAccess( core, this );

		// --------------------------------------------------------------------

		core.updateClockCyclesCounter( getCyclesCount( core, getTargetAddress() ) );
		incrementExecutionsCount();
	}

	protected long getCyclesCount( Core core, int address )
	{
		long cycles = 2l;

		switch( core.getDescriptor().getCoreVersion() )
		{
			case AVRe:
			{
				cycles = 2l;
				break;
			}

			case AVRxm:
			case AVRxt:
			{
				if( address < core.getDescriptor().getSramBase() )
				{
					cycles = 2l;
				}
				else
				{
					cycles = 3l;
				}
				break;
			}


			default:
		}

		return cycles;
	}
}
