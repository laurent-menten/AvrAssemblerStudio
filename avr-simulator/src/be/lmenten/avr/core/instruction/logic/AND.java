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

package be.lmenten.avr.core.instruction.logic;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.data.CoreStatusRegister;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.arithmetic.test.TST;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd5Rr5;
import be.lmenten.avr.core.register.Register;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor(
	opcode = "0010 00rd dddd rrrr",
	statusRegister = "---*0**-",
	syntax = "AND Rd, Rr",
	description = "Logical AND",
	alias = "TST Rd"
)
public class AND
	extends Instruction_Rd5Rr5
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public AND( Register rd, Register rr )
	{
		super( rd, rr );

		int opc = InstructionSet.AND.getOpcodeMaskValue();
		opc = InstructionSet.AND.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		opc = InstructionSet.AND.insertOperand( OperandType.r, opc, rr.getOperandIndex() );
		setOpcode( opc );
	}

	public static AND newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.AND.extractOperand( OperandType.d, opcode );
		int r = InstructionSet.AND.extractOperand( OperandType.r, opcode );

		if( d == r )
		{
			return new TST( Register.lookup(d) );
		}
		else
		{
			return new AND( Register.lookup(d), Register.lookup(r) );
		}
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.AND;
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
		
		Value r = rd.compute( (a,b) -> { return (byte)(a & b); }, rr );

		sreg.v( false );
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
