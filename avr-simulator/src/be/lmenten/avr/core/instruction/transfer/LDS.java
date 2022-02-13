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
import be.lmenten.avr.core.data.CoreData;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd5k16;
import be.lmenten.avr.core.register.Register;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "1001 000d dddd 0000",
	is32bits = true,
	statusRegister = "--------",
	syntax = "LDS Rd, k[0..65535]",
	description = "Load direct"
)
public class LDS
	extends Instruction_Rd5k16
	implements TransferInstruction
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public LDS( Register rd, int k )
	{
		super( rd, k );

		int opc = InstructionSet.LDS.getOpcodeMaskValue();
		opc = InstructionSet.LDS.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		setOpcode( opc );
	}

	public static LDS newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.LDS.extractOperand( OperandType.d, opcode );

		return new LDS( Register.lookup( d ), opcode2 );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.LDS;
	}

	// ------------------------------------------------------------------------

	@Override
	public int getTargetAddress()
	{
		return getAddress();
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreData _v = core.getSramCell( getTargetAddress() );
		Value v = _v.getValue();
		_v.recordReadAccess( core,  this );

		CoreRegister _rd = core.getRegister( this.getRd() );

		// --------------------------------------------------------------------

		_rd.setValue( v );
		_rd.recordWriteAccess( core, this );

		// --------------------------------------------------------------------

		core.updateClockCyclesCounter( getCyclesCount( core, getTargetAddress() ) );
		incrementExecutionsCount();
	}

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
			case AVRxt:
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


			default:
		}

		return cycles;
	}
}
