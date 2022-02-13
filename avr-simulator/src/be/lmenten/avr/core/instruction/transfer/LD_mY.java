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
import be.lmenten.avr.core.register.Register;
import be.lmenten.avr.core.register.RegisterIndexXYZ;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "1001 000d dddd 1010",
	statusRegister = "--------",
	syntax = "LD Rd, -Y",
	description = "Load indirect from data space using index Y pre-decremented"
)
public class LD_mY
	extends LD
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public LD_mY( Register rd, RegisterIndexXYZ rr )
	{
		super( rd, rr );
	}

	public static LD_mY newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.LD.extractOperand( OperandType.d, opcode );
		int r = InstructionSet.LD.extractOperand( OperandType.r, opcode );

		return new LD_mY( Register.lookup( d ), RegisterIndexXYZ.lookup( r ) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.LD;
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	protected long getCyclesCount( Core core, int address )
	{
		long cycles = 2l;

		switch( core.getDescriptor().getCoreVersion() )
		{
			case AVRe:
			{
				cycles = 2l;
				break;
			}

			case AVRxm:
			{
				if( address < core.getDescriptor().getSramBase() )
				{
					cycles = 2l;
				}
				else
				{
					cycles = 3l;
				}
					
				break;
			}

			case AVRxt:
			{
				cycles = 2l;
				break;
			}

			case AVRrc:
			{
				cycles = 2l;	// FIXME doc says 2 / 3 ...
				break;
			}

			default:
		}

		return cycles;
	}
}
