// = ======================================================================== =
// = === AVR Programmer Studio ======= Copyright (c) 2020+ Laurent Menten === =
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

package be.lmenten.avr.core.analysis;

import be.lmenten.avr.core.instruction.Instruction;

/**
 * This class describe an access description to a memory zone of the core.
 *
 * @author Laurent Menten
 * @version 1.0, (12 Jul 2020)
 * @since 1.0
 */
public class Access
{
	private final long tick;
	private final Instruction instruction;
	private final AccessType accessType;

	// =========================================================================
	// = Constructors ==========================================================
	// =========================================================================

	public Access( long tick, Instruction instruction, AccessType accessType )
	{
		this.tick = tick;
		this.instruction = instruction;
		this.accessType = accessType;
	}

	// =========================================================================
	// = Getters ===============================================================
	// =========================================================================

	/**
	 * Get cycle clock counter value at the time of the access.
	 * 
	 * @return the cycle clock counter
	 */
	public long getTick()
	{
		return tick;
	}

	/**
	 * Get the accessing instruction.
	 * 
	 * @return the accessing instruction
	 */
	public Instruction getInstruction()
	{
		return instruction;
	}

	/**
	 * Get the type of access.
	 * 
	 * @return the type of access
	 */
	public AccessType getAccessType()
	{
		return accessType;
	}
}
