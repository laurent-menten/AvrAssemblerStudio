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
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.logic.AND;
import be.lmenten.avr.core.register.Register;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor(
		opcode = "0010 00rd dddd rrrr",
		isAlias = true,
		alias = "AND Rd, Rd",
		statusRegister = "--******",
		syntax = "TST Rd",
		description = "Test for zero or minus"
	)
public class TST
	extends AND
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public TST( Register rd )
	{
		super( rd, rd );
	}

	public static TST newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.AND.extractOperand( OperandType.d, opcode );
		int r = InstructionSet.AND.extractOperand( OperandType.r, opcode );

		if( r != d )
		{
			throw new RuntimeException( "TST: Rd != Rr"  );
		}

		return new TST( Register.lookup(d) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.TST;
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public String getOperand2( Core core )
	{
		return null;
	}
}
