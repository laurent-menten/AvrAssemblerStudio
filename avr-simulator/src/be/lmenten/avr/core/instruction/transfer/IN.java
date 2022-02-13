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
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd5A6;
import be.lmenten.avr.core.register.Register;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "1011 0AAd dddd AAAA",
	statusRegister = "--------",
	syntax = "IN Rd, A[0..63]",
	description = "Load an I/O location to register"
)
public class IN
	extends Instruction_Rd5A6
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public IN( Register rd, int A )
	{
		super( rd, A );

		int opc = InstructionSet.IN.getOpcodeMaskValue();
		opc = InstructionSet.IN.insertOperand( OperandType.d, opc, rd.getIndex() );
		opc = InstructionSet.IN.insertOperand( OperandType.A, opc, getAddress() );

		setOpcode( opc );
	}

	public static IN newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.IN.extractOperand( OperandType.d, opcode );
		int A = InstructionSet.IN.extractOperand( OperandType.A, opcode );

		return new IN( Register.lookup( d ), A );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.IN;
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreRegister _io = core.getIORegisterByAddress( getAddress() );
		Value io = _io.getValue();
		_io.recordReadAccess( core, this );

		CoreRegister _rd = core.getRegister( this.getRd() );

		// --------------------------------------------------------------------

		_rd.setValue( io );
		_rd.recordWriteAccess( core, this );

		// --------------------------------------------------------------------

		core.updateClockCyclesCounter( 1l );
		incrementExecutionsCount();
	}
}
