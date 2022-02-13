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

package be.lmenten.avr.core.instruction.arithmetic;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.data.CoreStatusRegister;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd5;
import be.lmenten.avr.core.register.Register;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "1001 010d dddd 0011",
	statusRegister = "---****-",
	syntax = "INC Rd",
	description = "Increment"
)
public class INC
	extends Instruction_Rd5
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public INC( Register rd )
	{
		super( rd );

		int opc = InstructionSet.INC.getOpcodeMaskValue();
		opc = InstructionSet.INC.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		setOpcode( opc );
	}

	public static INC newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.INC.extractOperand( OperandType.d, opcode );

		return new INC( Register.lookup( d ) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.INC;
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreStatusRegister sreg = core.getStatusRegisterCopy();

		CoreRegister _rd = core.getRegister( this.getRd() );
		Value rd = _rd.getValue();
		_rd.recordReadAccess( core, this );

		// --------------------------------------------------------------------
		
		Value r = rd.compute( (a) -> { return (byte)( a + 1 ); } );

		sreg.v( r.bit7() | !r.bit6() | !r.bit5() | !r.bit4() | !r.bit3() | !r.bit2() | !r.bit1() | !r.bit0() );
		sreg.n( r.bit7() );
		sreg.s( sreg.n() ^ sreg.v() );
		sreg.z( r.isZero() );

		_rd.setValue( r );
		_rd.recordWriteAccess( core, this );

		core.setStatusRegister( sreg );

		// --------------------------------------------------------------------
		
		core.updateClockCyclesCounter( 1l );
		incrementExecutionsCount();
	}
}
