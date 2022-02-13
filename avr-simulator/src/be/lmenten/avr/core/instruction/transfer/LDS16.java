// = ======================================================================== =
// = === AVR Simulator =============== Copyright (c) 2020+ Laurent Menten === =
// = ======================================================================== =
// = = This program is free software: you can redistribute it and/or modify = =
// = = it under the terms of the GNU General Public License as published by = =
// = = the Free Software Foundation, either version 3 of the License, or    = =
// = = (at your option) any later version.                                  = =
// = =                                                                      = =
// = = This program is distributed in the hope that it will be useful, but  = =
// = = WITHOUT ANY WARRANTY; without even the implied warranty of           = =
// = = MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU    = =
// = = General Public License for more details.                             = =
// = =                                                                      = =
// = = You should have received a copy of the GNU General Public License    = =
// = = along with this program. If not, see                                 = =
// = = <https://www.gnu.org/licenses/>.                                     = =
// = ======================================================================== =

package be.lmenten.avr.core.instruction.transfer;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreData;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.event.CoreEvent;
import be.lmenten.avr.core.event.CoreEventType;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd4k7;
import be.lmenten.avr.core.register.UpperRegister;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "1010 0kkk dddd kkkk",
	statusRegister = "--------",
	syntax = "LDS Rd, k[0..127]",
	description = "Load direct"
)
public class LDS16
	extends Instruction_Rd4k7
	implements TransferInstruction
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public LDS16( UpperRegister rd, int k )
	{
		super( rd, k );

		int opc = InstructionSet.LDS16.getOpcodeMaskValue();
		opc = InstructionSet.LDS16.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		opc = InstructionSet.LDS16.insertOperand( OperandType.k, opc, k );
		setOpcode( opc );
	}

	public static LDS16 newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.LDS16.extractOperand( OperandType.d, opcode );
		int k = InstructionSet.LDS16.extractOperand( OperandType.k, opcode );

		return new LDS16( UpperRegister.lookup( d ), k );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.LDS16;
	}

	// ------------------------------------------------------------------------

	@Override
	public int getAddress()
	{
		Value address = new Value( super.getAddress(), 7 );
		Value address2 = new Value( 0x00, 7 );

		address2.bit0( address.bit0() );
		address2.bit1( address.bit1() );
		address2.bit2( address.bit2() );
		address2.bit3( address.bit3() );

		address2.bit4( address.bit5() );
		address2.bit5( address.bit6() );
		address2.bit6( address.bit4() );
		address2.bit7( !address.bit4() );

		return address2.getData();
	}

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
		int address = getTargetAddress();
		if( (address < 0x40) || (address > 0xBF) )
		{
			CoreEvent ev = new CoreEvent( CoreEventType.LDS16_ADDRESS_OUT_OF_RANGE, core );
			ev.setInstruction( this );
			core.fireCoreEvent( ev );
		}

		CoreData _v = core.getSramCell( address );
		Value v = _v.getValue();
		_v.recordReadAccess( core,  this );

		CoreRegister _rd = core.getRegister( this.getRd() );

		// --------------------------------------------------------------------

		_rd.setValue( v );
		_rd.recordWriteAccess( core, this );

		// --------------------------------------------------------------------

		core.updateClockCyclesCounter( 2l );
		incrementExecutionsCount();
	}
}
