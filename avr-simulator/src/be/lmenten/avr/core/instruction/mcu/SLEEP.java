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

package be.lmenten.avr.core.instruction.mcu;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreControlRegister;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.event.CoreEvent;
import be.lmenten.avr.core.event.CoreEventType;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.mcu.RunningMode;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "1001 0101 1000 1000",
	statusRegister = "--------",
	syntax = "SLEEP",
	description = "Set circuit in sleep mode"
)
public class SLEEP
	extends Instruction
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public SLEEP()
	{
		setOpcode( InstructionSet.SLEEP.getOpcodeMaskValue() );
	}

	public static SLEEP newInstance( Integer opcode, Integer opcode2 )
	{
		return new SLEEP();
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.SLEEP;
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreRegister smcr = core.getRegister( CoreControlRegister.SMCR );

		if( smcr.bit( "SE" ) )
		{
			int sm = smcr.bits( "SM2", "SM1", "SM0" );
			switch( sm )
			{
				case 0b000:
					core.setCoreMode( RunningMode.IDLE );
					break;

				case 0b001:
					core.setCoreMode( RunningMode.ADC_NOISE_REDUCTION );
					break;

				case 0b010:
					core.setCoreMode( RunningMode.POWER_DOWN );
					break;

				case 0b011:
					core.setCoreMode( RunningMode.POWDER_SAVE );
					break;

				case 0b110:
					core.setCoreMode( RunningMode.STANDBY );
					break;

				case 0b111:
					core.setCoreMode( RunningMode.EXTENDED_STANDBY );
					break;

				default:
					CoreEvent ev = new CoreEvent( CoreEventType.INVALID_SLEEP_MODE, core );
					ev.setValue( sm );

					core.fireCoreEvent( ev );
					break;
			}
		}

		core.updateClockCyclesCounter( 1l );
		incrementExecutionsCount();
	}
}
