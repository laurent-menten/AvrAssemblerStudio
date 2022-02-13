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
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd3Rr3;
import be.lmenten.avr.core.register.LowUpperRegister;
import be.lmenten.avr.core.register.Register;
import be.lmenten.avr.utils.NumberUtils;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor(
	opcode = "0000 0011 0ddd 0rrr",
	coreVersionSpecific = { AVRe_PLUS, AVRxm, AVRxt },
	statusRegister = "--------",
	syntax = "MULSU Rd[16..31], Rr[16..31]",
	description = "Multiply (signed with unsigned)"
)
public class MULSU
	extends Instruction_Rd3Rr3
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public MULSU( LowUpperRegister rd, LowUpperRegister rr )
	{
		super( rd, rr );

		int opc = InstructionSet.MULSU.getOpcodeMaskValue();
		opc = InstructionSet.MULSU.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		opc = InstructionSet.MULSU.insertOperand( OperandType.r, opc, rr.getOperandIndex() );
		setOpcode( opc );
	}

	public static MULSU newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.MULSU.extractOperand( OperandType.d, opcode );
		int r = InstructionSet.MULSU.extractOperand( OperandType.r, opcode );

		return new MULSU( LowUpperRegister.lookup(d), LowUpperRegister.lookup(r) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.MULSU;
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
		multiplicand = NumberUtils.propagateMSB( multiplicand );
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
