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

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.register.Register;
import be.lmenten.avr.core.register.RegisterIndexXYZ;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "1001 001r rrrr 1100",
	statusRegister = "--------",
	syntax = "ST X, Rd",
	description = "Store indirect to data space using index X"
)
public class ST_X
	extends ST
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public ST_X( RegisterIndexXYZ rd, Register rr )
	{
		super( rd, rr );
	}

	public static ST_X newInstance( Integer opcode, Integer opcode2 )
	{
		int r = InstructionSet.ST.extractOperand( OperandType.r, opcode );
		int d = InstructionSet.ST.extractOperand( OperandType.d, opcode );

		return new ST_X( RegisterIndexXYZ.lookup( d ), Register.lookup( r ) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.ST;
	}
}
