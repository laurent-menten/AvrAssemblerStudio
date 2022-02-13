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
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd5;
import be.lmenten.avr.core.register.Register;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "1001 000d dddd 1111",
	statusRegister = "--------",
	syntax = "POP Rd",
	description = "Pop register from stack"
)
public class POP
	extends Instruction_Rd5
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public POP( Register rd )
	{
		super( rd );

		int opc = InstructionSet.POP.getOpcodeMaskValue();
		opc = InstructionSet.POP.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		setOpcode( opc );
	}

	public static POP newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.POP.extractOperand( OperandType.d, opcode );

		return new POP( Register.lookup( d ) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.POP;
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreRegister rd = core.getRegister( getRd() );

		// --------------------------------------------------------------------

		int value = core.pop() & 0xFF;

		rd.setData( value );
		rd.recordWriteAccess( core, this );

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
