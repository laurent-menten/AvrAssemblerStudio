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
import be.lmenten.avr.core.instruction.xbase.Instruction_Rr5A6;
import be.lmenten.avr.core.register.Register;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "1011 1AAr rrrr AAAA",
	statusRegister = "--------",
	syntax = "OUT A[0..63], Rd",
	description = "Store register to I/O location"
)
public class OUT
	extends Instruction_Rr5A6
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public OUT( int A, Register rr )
	{
		super( A, rr );

		int opc = InstructionSet.OUT.getOpcodeMaskValue();
		opc = InstructionSet.OUT.insertOperand( OperandType.r, opc, rr.getIndex() );
		opc = InstructionSet.OUT.insertOperand( OperandType.A, opc, getAddress() );

		setOpcode( opc );
	}

	public static OUT newInstance( Integer opcode, Integer opcode2 )
	{
		int r = InstructionSet.OUT.extractOperand( OperandType.r, opcode );
		int A = InstructionSet.OUT.extractOperand( OperandType.A, opcode );

		return new OUT( A, Register.lookup( r ) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.OUT;
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreRegister _io = core.getIORegisterByAddress( getAddress() );

		CoreRegister _rr = core.getRegister( this.getRr() );
		Value rr = _rr.getValue();
		_rr.recordReadAccess( core, this );

		// --------------------------------------------------------------------

		_io.setValue( rr );
		_io.recordWriteAccess( core, this );

		// --------------------------------------------------------------------

		core.updateClockCyclesCounter( 1l );
		incrementExecutionsCount();
	}
}
