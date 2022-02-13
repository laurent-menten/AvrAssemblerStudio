package be.lmenten.avr.core.instruction.flow;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.event.CoreEvent;
import be.lmenten.avr.core.event.CoreEventType;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd5Rr5;
import be.lmenten.avr.core.register.Register;

@InstructionDescriptor(
		opcode = "0001 00rd dddd rrrr",
		statusRegister = "--------",
		syntax = "CPSE Rd, Rr",
		description = "Compare and skip if equal"
)
public class CPSE
	extends Instruction_Rd5Rr5
{
	// ========================================================================
	// ===
	// ========================================================================

	public CPSE( Register rd, Register rr )
	{
		super( rd, rr );

		int opc = InstructionSet.CPSE.getOpcodeMaskValue();
		opc = InstructionSet.CPSE.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		opc = InstructionSet.CPSE.insertOperand( OperandType.r, opc, rr.getOperandIndex() );
		setOpcode( opc );
	}

	public static CPSE newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.CPSE.extractOperand( OperandType.d, opcode );
		int r = InstructionSet.CPSE.extractOperand( OperandType.r, opcode );

		return new CPSE( Register.lookup(d), Register.lookup(r) );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.CPSE;
	}

	// ========================================================================
	// === Simulation =========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreRegister _rd = core.getRegister( getRd() );
		Value rd = _rd.getValue();
		_rd.recordReadAccess( core, this );

		CoreRegister _rr = core.getRegister( getRr() );
		Value rr = _rr.getValue();
		_rr.recordReadAccess( core, this );

		// --------------------------------------------------------------------

		long cycles = 1;

		if( rr.equals(  rd ) )
		{
			Instruction i = core.getFollowingInstruction();
			if( i == null )
			{
				CoreEvent event = new CoreEvent( CoreEventType.FLASH_MEMORY_UNINITIALIZED, core );
				event.setInstruction( this );
				core.fireCoreEvent( event );

				throw new RuntimeException( "Flash memory uninitialised" );
			}

			cycles += (i.getOpcodeSize() == 1 ? 1 : 2);

			core.updateProgramCounter( i.getOpcodeBytesSize() );
		}

		// --------------------------------------------------------------------

		core.updateClockCyclesCounter( cycles );
		incrementExecutionsCount();
	}
}
