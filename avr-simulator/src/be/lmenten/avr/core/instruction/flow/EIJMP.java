package be.lmenten.avr.core.instruction.flow;

 import static be.lmenten.avr.core.descriptor.CoreVersion.AVRe_PLUS;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRxm;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRxt;

 import be.lmenten.avr.core.Core;
 import be.lmenten.avr.core.data.CoreControlRegister;
 import be.lmenten.avr.core.data.CoreRegister;
 import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
 import be.lmenten.avr.core.register.Register;

@InstructionDescriptor
(
	opcode = "1001 0100 0001 1001",
	coreVersionSpecific = { AVRe_PLUS, AVRxm, AVRxt },
	statusRegister = "--------",
	syntax = "EIJMP",
	description = "Extended indirect jump"
)
public class EIJMP
	extends Instruction
{
	// ========================================================================
	// ===
	// ========================================================================

	public EIJMP()
	{
		setOpcode( InstructionSet.EIJMP.getOpcodeMaskValue() );
	}

	public static EIJMP newInstance( Integer opcode, Integer opcode2 )
	{
		return new EIJMP();
	}
	
	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.EIJMP;
	}

	// ========================================================================
	// === Simulation =========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreRegister _rrl = core.getRegister( Register.ZL );
		int rrl = _rrl.getData() & 0xFF;
		_rrl.recordReadAccess( core, this );
		CoreRegister _rrh = core.getRegister( Register.ZH );
		int rrh = _rrh.getData() & 0xFF;
		_rrh.recordReadAccess( core, this );

		int address = ((rrh & 0xFF) << 8) | (rrl & 0xFF);

		CoreRegister _eind = core.getRegister( CoreControlRegister.EIND );
		if( _eind != null )
		{
			int eind = _eind.getData() & 0xFF;
			_eind.recordReadAccess( core, this );

			address |= (eind << 16);
		}

		// --------------------------------------------------------------------

		core.setProgramCounter( address );

		core.updateClockCyclesCounter( 2l );
	}
}
