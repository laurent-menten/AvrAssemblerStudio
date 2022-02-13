package be.lmenten.avr.core.instruction.flow;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreControlRegister;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.register.Register;

@InstructionDescriptor
(
	opcode = "1001 0100 0000 1001",
	statusRegister = "--------",
	syntax = "IJMP",
	description = "Indirect jump"
)
public class IJMP
	extends Instruction
{
	// ========================================================================
	// ===
	// ========================================================================

	public IJMP()
	{
		setOpcode( InstructionSet.IJMP.getOpcodeMaskValue() );
	}

	public static IJMP newInstance( Integer opcode, Integer opcode2 )
	{
		return new IJMP();
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.IJMP;
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

		// --------------------------------------------------------------------

		core.setProgramCounter( address );

		core.updateClockCyclesCounter( 2l );	}
}
