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
import be.lmenten.avr.core.data.CoreControlRegister;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rr5;
import be.lmenten.avr.core.register.Register;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "1001 001r rrrr 1111",
	statusRegister = "--------",
	syntax = "PUSH Rd",
	description = "Push register onto stack"
)
public class PUSH
	extends Instruction_Rr5
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public PUSH( Register rr )
	{
		super( rr );

		int opc = InstructionSet.PUSH.getOpcodeMaskValue();
		opc = InstructionSet.PUSH.insertOperand( OperandType.r, opc, rr.getOperandIndex() );
		setOpcode( opc );
	}

	public static PUSH newInstance( Integer opcode, Integer opcode2 )
	{
		int r = InstructionSet.PUSH.extractOperand( OperandType.r, opcode );

		return new PUSH( Register.lookup( r ) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.PUSH;
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreRegister _rr = core.getRegister( getRr() );
		int rr = _rr.getData();
		_rr.recordReadAccess( core, this );

		// --------------------------------------------------------------------

		core.push( rr & 0xFF );

		CoreRegister spl = core.getRegister( CoreControlRegister.SPL );
		spl.recordWriteAccess( core, this );

		CoreRegister sph = core.getRegister( CoreControlRegister.SPH );
		if( sph != null )
		{
			sph.recordWriteAccess( core, this );
		}

		// --------------------------------------------------------------------

		core.updateClockCyclesCounter( 1l );
		incrementExecutionsCount();
	}
}
