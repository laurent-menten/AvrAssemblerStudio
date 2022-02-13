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

package be.lmenten.avr.core.instruction.arithmetic.test;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.data.CoreStatusRegister;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd5Rr5;
import be.lmenten.avr.core.register.Register;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor(
		opcode = "0001 01rd dddd rrrr",
		statusRegister = "--******",
		syntax = "CP Rd, Rr",
		description = "Compare"
)
public class CP
	extends Instruction_Rd5Rr5
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public CP( Register rd, Register rr )
	{
		super( rd, rr );

		int opc = InstructionSet.CP.getOpcodeMaskValue();
		opc = InstructionSet.CP.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		opc = InstructionSet.CP.insertOperand( OperandType.r, opc, rr.getOperandIndex() );
		setOpcode( opc );
	}

	public static CP newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.CP.extractOperand( OperandType.d, opcode );
		int r = InstructionSet.CP.extractOperand( OperandType.r, opcode );

		return new CP( Register.lookup(d), Register.lookup(r) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.CP;
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

		CoreRegister _rr = core.getRegister( this.getRr() );
		Value rr = _rr.getValue();
		_rr.recordReadAccess( core, this );

		// --------------------------------------------------------------------
		
		Value r = rd.compute( (a,b) -> { return (byte)(a - b); }, rr );

		sreg.c( !rd.bit7() & rr.bit7() | rr.bit7() & r.bit7() | r.bit7() & !rd.bit7() );
		sreg.h( !rd.bit3() & rr.bit3() | rr.bit3() & r.bit3() | r.bit3() & !rd.bit3() );
		sreg.v( rd.bit7() & !rr.bit7() & !r.bit7() | !rd.bit7() & rr.bit7() & r.bit7() );
		sreg.n( r.bit7() );
		sreg.s( sreg.n() ^ sreg.v() );
		sreg.z( r.isZero() & sreg.z() );

		core.setStatusRegister( sreg );

		// --------------------------------------------------------------------

		core.updateClockCyclesCounter( 1l );
		incrementExecutionsCount();
	}
}
