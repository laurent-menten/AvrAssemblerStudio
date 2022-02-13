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
import be.lmenten.avr.core.data.CoreRegister;
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
	opcode = "1110 KKKK dddd KKKK",
	statusRegister = "--------",
	syntax = "LDI Rd[16..31], k[0.255]",
	description = "Load immediate"
)
public class LDI
	extends Instruction_Rd4K8
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public LDI( UpperRegister rd, int K )
	{
		super( rd, K );

		int opc = InstructionSet.LDI.getOpcodeMaskValue();
		opc = InstructionSet.LDI.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		opc = InstructionSet.LDI.insertOperand( OperandType.K, opc, getImmediate() );
		setOpcode( opc );
	}

	public static LDI newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.LDI.extractOperand( OperandType.d, opcode );
		int K = InstructionSet.LDI.extractOperand( OperandType.K, opcode );

		return new LDI( UpperRegister.lookup( d ), K );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.LDI;
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreRegister _rd = core.getRegister( this.getRd() );

		// --------------------------------------------------------------------

		_rd.setData( getImmediate() );
		_rd.recordWriteAccess( core, this );

		// --------------------------------------------------------------------

		core.updateClockCyclesCounter( 1l );
		incrementExecutionsCount();
	}
}
