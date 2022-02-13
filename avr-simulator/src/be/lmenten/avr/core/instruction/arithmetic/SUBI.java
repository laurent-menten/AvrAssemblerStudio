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
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd4K8;
import be.lmenten.avr.core.register.UpperRegister;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "0101 KKKK dddd KKKK",
	statusRegister = "--******",
	syntax = "SUBI Rd[16..31], K[0..255]",
	description = "Subtract immediate"
)
public class SUBI
	extends Instruction_Rd4K8
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public SUBI( UpperRegister rd, int K )
	{
		super( rd, K );

		int opc = InstructionSet.SUBI.getOpcodeMaskValue();
		opc = InstructionSet.SUBI.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		opc = InstructionSet.SUBI.insertOperand( OperandType.K, opc, K );
		setOpcode( opc );
	}

	public static SUBI newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.SUBI.extractOperand( OperandType.d, opcode );
		int K = InstructionSet.SUBI.extractOperand( OperandType.K, opcode );

		return new SUBI( UpperRegister.lookup( d ), K );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.SUBI;
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

		Value K = new Value( getImmediate(), 8 );

		// --------------------------------------------------------------------

		Value r = rd.compute( (a,b) -> { return (byte)(a - b); }, K );

		sreg.c( !rd.bit7() & K.bit7() | K.bit7() & r.bit7() | r.bit7() & !rd.bit7() );
		sreg.h( !rd.bit3() & K.bit3() | K.bit3() & r.bit3() | r.bit3() & !rd.bit3() );
		sreg.v( rd.bit7() & !K.bit7() & !r.bit7() | !rd.bit7() & K.bit7() & r.bit7() );
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
