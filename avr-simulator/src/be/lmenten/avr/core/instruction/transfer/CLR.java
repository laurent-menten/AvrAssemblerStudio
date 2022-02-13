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
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.logic.EOR;
import be.lmenten.avr.core.register.Register;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor(
		opcode = "0010 01rd dddd rrrr",
		isAlias = true,
		alias = "EOR Rd, Rd",
		statusRegister = "---0001*",
		syntax = "CLR Rd",
		description = "Clear register"
	)
public class CLR
	extends EOR
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public CLR( Register rd )
	{
		super( rd, rd );
	}

	public static CLR newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.EOR.extractOperand( OperandType.d, opcode );
		int r = InstructionSet.EOR.extractOperand( OperandType.r, opcode );

		if( r != d )
		{
			throw new RuntimeException( "CLR: Rd != Rr"  );
		}

		return new CLR( Register.lookup(d) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.CLR;
	}

	// ========================================================================
	// = toString() support ===================================================
	// ========================================================================

	@Override
	public String getOperand2( Core core )
	{
		return null;
	}
}
