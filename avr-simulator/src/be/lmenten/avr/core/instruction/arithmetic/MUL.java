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

import static be.lmenten.avr.core.descriptor.CoreVersion.AVRe_PLUS;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRxm;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRxt;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.data.CoreStatusRegister;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd5Rr5;
import be.lmenten.avr.core.register.Register;
import be.lmenten.avr.utils.NumberUtils;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor(
	opcode = "1001 11rd dddd rrrr",
	coreVersionSpecific = { AVRe_PLUS, AVRxm, AVRxt },
	statusRegister = "--------",
	syntax = "MUL Rd, Rr",
	description = "Multiply (unsigned)"
)
public class MUL
	extends Instruction_Rd5Rr5
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public MUL( Register rd, Register rr )
	{
		super( rd, rr );

		int opc = InstructionSet.MUL.getOpcodeMaskValue();
		opc = InstructionSet.MUL.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		opc = InstructionSet.MUL.insertOperand( OperandType.r, opc, rr.getOperandIndex() );
		setOpcode( opc );
	}

	public static MUL newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.MUL.extractOperand( OperandType.d, opcode );
		int r = InstructionSet.MUL.extractOperand( OperandType.r, opcode );

		return new MUL( Register.lookup(d), Register.lookup(r) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.MUL;
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreStatusRegister sreg = core.getStatusRegisterCopy();

		CoreRegister _rd = core.getRegister( this.getRd() );
		int multiplicand = _rd.getData();
		_rd.recordReadAccess( core, this );

		CoreRegister _rr = core.getRegister( this.getRr() );
		int multiplier = _rr.getData();
		_rr.recordReadAccess( core, this );

		CoreRegister _r0 = core.getRegister( Register.R0 );

		CoreRegister _r1 = core.getRegister( Register.R1 );

		// --------------------------------------------------------------------

		int r = multiplicand * multiplier;

		sreg.c( NumberUtils.bit15(r) );
		sreg.z( (r & 0xFFFF) == 0 );

		_r1.setData( NumberUtils.highByte(r) );
		_r1.recordWriteAccess( core, this );

		_r0.setData( NumberUtils.lowByte(r) );
		_r0.recordWriteAccess( core, this );

		core.setStatusRegister( sreg );

		// --------------------------------------------------------------------
		
		core.updateClockCyclesCounter( 2l );
		incrementExecutionsCount();
	}
}
