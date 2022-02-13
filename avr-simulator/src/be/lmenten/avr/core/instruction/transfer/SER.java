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
import be.lmenten.avr.core.instruction.logic.ANDI;
import be.lmenten.avr.core.register.UpperRegister;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "1110 1111 dddd 1111",
	isAlias = true,
	alias = "LDI Rd, $FF",
	statusRegister = "--------",
	syntax = "SER Rd[16..31]",
	description = "Clear bits in register"
)
public class SER
	extends ANDI
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public SER( UpperRegister rd )
	{
		super( rd, 0xFF );
	}

	public static SER newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.LDI.extractOperand( OperandType.d, opcode );

		int K = InstructionSet.LDI.extractOperand( OperandType.K, opcode );
		if( K != 255 )
		{
			throw new RuntimeException( "bad value for SER" );
		}

		return new SER( UpperRegister.lookup( d ) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.SER;
	}
}
