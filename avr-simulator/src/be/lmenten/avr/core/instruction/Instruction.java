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

package be.lmenten.avr.core.instruction;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.CoreMemoryCell;
import be.lmenten.avr.utils.StringUtils;

public abstract class Instruction
	extends CoreMemoryCell
{
	private InstructionStatus status = InstructionStatus.NOT_CHECKED;
	private long executionsCount;

	private boolean breakpoint;

	// ========================================================================
	// =
	// ========================================================================

	public Instruction()
	{
		executionsCount = 0l;
	}

	// ========================================================================
	// = Instruction checker ==================================================
	// ========================================================================

	/**
	 * 
	 * @param status
	 */
	public void setStatus( InstructionStatus status )
	{
		this.status = status;
	}

	/**
	 * 
	 * @return
	 */
	public InstructionStatus getStatus()
	{
		return status;
	}

	// ========================================================================
	// = Breakpoint ===========================================================
	// ========================================================================

	/**
	 * 
	 * @param enabled
	 */
	public void setBreakpoint( boolean enabled )
	{
		breakpoint = enabled;
	}

	/**
	 * 
	 */
	public boolean breakpointEnabled()
	{
		return breakpoint;
	}

	// ========================================================================
	// = Instruction executions count =========================================
	// ========================================================================

	/**
	 * 
	 */
	public void incrementExecutionsCount()
	{
		executionsCount++;
	}

	/**
	 * 
	 */
	public void resetExecutionsCount()
	{
		executionsCount = 0l;
	}

	/**
	 * 
	 * @return
	 */
	public long getExecutionsCount()
	{
		return executionsCount;
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	/**
	 * Execute an instruction.
	 * When entering this method, program counter points next instruction
	 * 
	 * @param core
	 */
	public void execute( Core core )
	{
		throw new RuntimeException( "Opcode "
				+ this.getClass().getSimpleName() + " not implemented." );
	}

//	public abstract void execute( Core core );

	// ========================================================================
	// === BITS ===============================================================
	// ========================================================================

	public boolean bit8()	{ return bit( 8 ); }
	public boolean bit9()	{ return bit( 9 ); }
	public boolean bit10()	{ return bit( 10 ); }
	public boolean bit11()	{ return bit( 11 ); }
	public boolean bit12()	{ return bit( 12 ); }
	public boolean bit13()	{ return bit( 13 ); }
	public boolean bit14()	{ return bit( 14 ); }
	public boolean bit15()	{ return bit( 15 ); }

	public void bit8( boolean state )	{ bit( 8, state ); }
	public void bit9( boolean state )	{ bit( 9, state ); }
	public void bit10( boolean state )	{ bit( 10, state ); }
	public void bit11( boolean state )	{ bit( 11, state ); }
	public void bit12( boolean state )	{ bit( 12, state ); }
	public void bit13( boolean state )	{ bit( 13, state ); }
	public void bit14( boolean state )	{ bit( 14, state ); }
	public void bit15( boolean state )	{ bit( 15, state ); }

	// ========================================================================
	// ===
	// ========================================================================

	/**
	 *
	 */
	@Override
	public final int getDataWidth()
	{
		return 16;
	}

	// ------------------------------------------------------------------------

	/**
	 * 
	 * @return
	 */
	public abstract InstructionSet getInstructionSetEntry();

	/**
	 * 
	 * @return
	 */
	public boolean is32bits()
	{
		return (getInstructionSetEntry() != null)
				&& getInstructionSetEntry().is32bits();
	}

	/**
	 * Get opcode size in words
	 * 
	 * @return
	 */
	public int getOpcodeSize()
	{
		return is32bits() ? 2 : 1;
	}

	/**
	 * Get opcode size in bytes
	 * 
	 * @return
	 */
	public int getOpcodeBytesSize()
	{
		return is32bits() ? 4 : 2;
	}

	// ========================================================================
	// ===
	// ========================================================================

	public final void setOpcode( int opcode )
	{
		setData( opcode );
	}

	public final int getOpcode()
	{
		return getData();
	}

	// ------------------------------------------------------------------------

	public DataInstruction getSecondWord()
	{
		if( getInstructionSetEntry().is32bits() )
		{
			throw new RuntimeException( "Opcode "
					+ this.getClass().getSimpleName() + " SHOULD HAVE second word." );
		}

		throw new RuntimeException( "Opcode "
				+ this.getClass().getSimpleName() + " HAS NO second word." );
	}

	// ========================================================================
	// ===
	// ========================================================================

	public String getMnemonic()
	{
		return getInstructionSetEntry().name();
	}

	public String getDescription()
	{
		return getInstructionSetEntry().getDescription();
	}

	public String getSyntax()
	{
		return getInstructionSetEntry().getSyntax();
	}

	// ------------------------------------------------------------------------

	public String getOperand1( Core core )
	{
		return null;
	}

	public String getOperand2( Core core )
	{
		return null;
	}

	public String getComment( Core core )
	{
		return null;
	}

	protected String formatImmediate( int immediate, int size )
	{
		StringBuilder s = new StringBuilder();

		if( immediate >= 0x20 )
		{
			s.append( "'" )
			 .append( (char)immediate )
			 .append( "',  " );
		}

		s.append( immediate )
		 .append( ",  " )
		 .append( StringUtils.toBinaryString( immediate, size ) )
		 ;

		return s.toString();		
	}

	// ========================================================================
	// ===
	// ========================================================================

	public String toString( Core core )
	{
		StringBuilder s = new StringBuilder();

		s.append( getMnemonic() );

		String tmp = getOperand1( core );
		if( tmp != null )
		{
			s.append( " " )
			 .append( tmp );
		}

		tmp = getOperand1( core );
		if( tmp != null )
		{
			s.append( ", " )
			 .append( tmp );
		}

		return s.toString();
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString()
	{
		return toString( null );
	}
}
