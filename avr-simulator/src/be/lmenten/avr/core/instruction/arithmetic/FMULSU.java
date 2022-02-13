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

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd3Rr3;
import be.lmenten.avr.core.register.LowUpperRegister;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor(
	opcode = "0000 0011 1ddd 1rrr",
	coreVersionSpecific = { AVRe_PLUS, AVRxm, AVRxt },
	statusRegister = "------**",
	syntax = "FMULSU Rd[16..23], Rr[16..23]",
	description = "Fractional multiply signed with unsigned"
)
public class FMULSU
	extends Instruction_Rd3Rr3
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public FMULSU( LowUpperRegister rd, LowUpperRegister rr )
	{
		super( rd, rr );

		int opc = InstructionSet.FMULSU.getOpcodeMaskValue();
		opc = InstructionSet.FMULSU.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		opc = InstructionSet.FMULSU.insertOperand( OperandType.r, opc, rr.getOperandIndex() );
		setOpcode( opc );
	}

	public static FMULSU newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.FMULSU.extractOperand( OperandType.d, opcode );
		int r = InstructionSet.FMULSU.extractOperand( OperandType.r, opcode );

		return new FMULSU( LowUpperRegister.lookup(d), LowUpperRegister.lookup(r) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.FMULSU;
	}
}
