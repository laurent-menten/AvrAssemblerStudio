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

import static be.lmenten.avr.core.descriptor.CoreVersion.AVRe;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRe_PLUS;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRxm;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRxt;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_RdP4RrP4;
import be.lmenten.avr.core.register.RegisterPair;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor(
	opcode = "0000 0001 dddd rrrr",
	coreVersionSpecific = { AVRe, AVRe_PLUS, AVRxm, AVRxt },
	statusRegister = "--------",
	syntax = "MOVW Rd+1:Rd[0,2..30], Rr+1:Rr[0,2..30]",
	description = "Copy register word"
)
public class MOVW
	extends Instruction_RdP4RrP4
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public MOVW( RegisterPair rd, RegisterPair rr )
	{
		super( rd, rr );

		int opc = InstructionSet.MOVW.getOpcodeMaskValue();
		opc = InstructionSet.MOVW.insertOperand( OperandType.d, opc,  rd.getOperandIndex() );
		opc = InstructionSet.MOVW.insertOperand( OperandType.r, opc,  rr.getOperandIndex() );
		setOpcode( opc );
	}

	public static MOVW newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.MOVW.extractOperand( OperandType.d, opcode );
		int r = InstructionSet.MOVW.extractOperand( OperandType.r, opcode );

		return new MOVW( RegisterPair.lookup(d), RegisterPair.lookup(r) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.MOVW;
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreRegister rd0 = core.getRegister( getRd().getLowerRegister() );
		CoreRegister rd1 = core.getRegister( getRd().getUpperRegister() );

		CoreRegister rr0 = core.getRegister( getRr().getLowerRegister() );
		rr0.recordReadAccess( core, this );
		CoreRegister rr1 = core.getRegister( getRr().getUpperRegister() );
		rr1.recordReadAccess( core, this );
		// --------------------------------------------------------------------

		rd1.setData( rr1.getData() );
		rd1.recordWriteAccess( core, this );

		rd0.setData( rr0.getData() );
		rd0.recordWriteAccess( core, this );

		// --------------------------------------------------------------------

		core.updateClockCyclesCounter( 1l );
		incrementExecutionsCount();
	}
}
