// = ======================================================================== =
// = === AVR Simulator =============== Copyright (c) 2022+ Laurent Menten === =
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

package be.lmenten.avr.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.lmenten.avr.core.data.CoreData;
import be.lmenten.avr.core.data.CoreStatusRegister;
import be.lmenten.avr.core.descriptor.CoreDescriptor;
import be.lmenten.avr.core.descriptor.CoreMemory;
import be.lmenten.avr.core.driver.Driver;
import be.lmenten.avr.core.event.CoreEvent;
import be.lmenten.avr.core.event.CoreEventListener;
import be.lmenten.avr.core.event.CoreEventType;
import be.lmenten.avr.core.exception.CoreAbortException;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.flow.FlowInstruction;
import be.lmenten.avr.core.instruction.transfer.TransferInstruction;
import be.lmenten.avr.core.mcu.ResetSources;
import be.lmenten.avr.core.mcu.RunningMode;
import be.lmenten.avr.core.register.IRegisterIndex;

/**
 * Core is the user interface of the core. It exposes the setters and
 * control methods of the core implementation as well as other methods not
 * directly related to simulation.
 * 
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 08 / 23
 */
public abstract class Core
	implements CoreModel
{
	// -------------------------------------------------------------------------
	// - Initialise InstuctionSet if needed ------------------------------------
	// -------------------------------------------------------------------------

	static
	{
		if( ! InstructionSet.isInitialized() )
		{
			InstructionSet.init();
		}		
	}
	
	// -------------------------------------------------------------------------
	// - Who we are ------------------------------------------------------------
	// -------------------------------------------------------------------------

	protected final CoreDescriptor cdesc;

	// -------------------------------------------------------------------------
	// - Memories --------------------------------------------------------------
	// -------------------------------------------------------------------------

	protected Instruction [] flash;

	private final Map<Integer,String> flashSymbolsByAddresse
		= new HashMap<>();

	// -------------------------------------------------------------------------

	protected CoreData sram [];

	private final Map<Integer,String> sramSymbolsByAddresse
		= new HashMap<>();

	// -------------------------------------------------------------------------

	private final Map<Integer,String> eepromSymbolsByAddresse
		= new HashMap<>();

	// -------------------------------------------------------------------------
	// - Drivers ---------------------------------------------------------------
	// -------------------------------------------------------------------------

	// Keep it sorted in insertion order

	private final Map<String,Driver> coreDrivers
		= new LinkedHashMap<>();

	// -------------------------------------------------------------------------
	// - Events ----------------------------------------------------------------
	// -------------------------------------------------------------------------

	private final List<CoreEventListener> coreEventListener
		= new ArrayList<>();

	// =========================================================================
	// = Constructors ==========================================================
	// =========================================================================

	/**
	 * Construct a Core generic object.
	 * 
	 * @param cdesc the descriptor for this device
	 */
	protected Core( CoreDescriptor cdesc )
	{
		this.cdesc = cdesc;
	}

	// =========================================================================
	// = Drivers ===============================================================
	// =========================================================================

	/**
	 * Register a driver to this core.
	 * 
	 * @param driver the driver
	 */
	protected void registerDriver( Driver driver )
	{
		LOG.log( Level.INFO, "Registering driver '" + driver.getId() +"'." );

		if( coreDrivers.containsKey( driver.getId() ) )
		{
			throw new RuntimeException( "Driver already registered" );
		}

		coreDrivers.put( driver.getId(), driver );
	}

	/**
	 * Propagate a reset of the core to the registered drivers.
	 */
	protected void resetDrivers()
	{
		coreDrivers.forEach( (id,driver) ->
		{
			driver.onReset();			
		} );
	}
	
	/**
	 * <p>
	 * Propagate the clock signal to the registered drivers. 
	 * 
	 * <p>
	 * Note: this is called after every simulation of an instruction.
	 */
	protected void tickDrivers()
	{
		long currentTicksCount = getClockCyclesCounter();

		coreDrivers.forEach( (id,driver) ->
		{
			driver.onTick( currentTicksCount );
		} );
	}

	// =========================================================================
	// = Core simulation =======================================================
	// =========================================================================

	/**
	 * Set the StatusRegister (SREG) content. This method does not trigger an
	 * access event.
	 * 
	 * @param sreg the StatusRegister content
	 */
	public abstract void setStatusRegister( CoreStatusRegister sreg );

	// -------------------------------------------------------------------------

	/**
	 * Execute a single instruction.
	 */
	public abstract void step();

	/**
	 * <p>
	 * Execute until program counter is equal to next instruction address.
	 * 
	 * <p>
	 * This is typically used to step over a CALL/ICALL/EICALL/RCALL instruction.
	 */
	public abstract void stepOver();

	/**
	 * Execute until execution of a RET/RETI instruction.
	 */
	public abstract void stepOut();

	/**
	 * Run the program until a breakpoint or an interrupting event.
	 */
	public abstract void run();

	// -------------------------------------------------------------------------

	/**
	 * Set the value of the program counter. The value is expressed in bytes.
	 * 
	 * @param address the value of the program counter
	 */
	public abstract void setProgramCounter( int address );

	/**
	 * Update the value of the program counter. The value is expressed in bytes.
	 * 
	 * @param offset the offset to apply to the program counter
	 */
	public abstract void updateProgramCounter( int offset );

	/**
	 * Push the value of the program counter onto the stack.
	 */
	public abstract void pushProgramCounter();

	/**
	 * Pop the program counter value from the stack
	 */
	public abstract void popProgramCounter();

	// -------------------------------------------------------------------------

	/**
	 * Set the running mode of this core.
	 * 
	 * @param coreMode the running mode
	 */
	public abstract void setCoreMode( RunningMode coreMode );
	
	/**
	 * Reset this core.
	 * 
	 * @param resetSource the reset source
	 */
	public abstract void reset( ResetSources resetSource );

	// ------------------------------------------------------------------------

	/**
	 * Get the address in memory of an interrupt vector.
	 * 
	 * @param vector the vector
	 * @return the address
	 */
	public abstract int getInterruptVectorAddress( int vector );

	/**
	 * Generate and interrupt for the given vector.
	 * 
	 * @param vector the vector
	 */
	public abstract void interrupt( int vector );

	/**
	 * Generate and interrupt for the given vector.
	 * 
	 * @param name the name of the vector
	 */
	public abstract void interrupt( String name );

	// -------------------------------------------------------------------------

	/**
	 * Update the clock cycles count. The passed value is added to the current
	 * one.
	 * 
	 * @param clockCyclesCount the increment to apply to the clock cycle counter
	 */
	public abstract void updateClockCyclesCounter( long clockCyclesCount );

	/**
	 * Convert a delay in nanoiseconds to a number of ticks.
	 * 
	 * @param time a delay in nanoseconds
	 * @return the number of ticks
	 */
	public abstract long nanosToTicks( double time );

	/**
	 * Convert a number of ticks to a delay in nanoseconds.
	 * 
	 * @param ticks a number of ticks
	 * @return the delay in nanoiseconds
	 */
	public abstract double ticksToNanos( long ticks );

	// -------------------------------------------------------------------------

	/**
	 * The status of the IDRD bit in OCDR register.
	 * 
	 * @param ioDebugRegisterDirty
	 */
	public abstract void ioDebugRegisterDirty( boolean ioDebugRegisterDirty );

	// =========================================================================
	// = Index registers =======================================================
	// =========================================================================

	/**
	 * Set the value of the X, Y or Z index register
	 * 
	 * @param r the index register
	 * @param value the value
	 * @param ext true for extended register
	 */
	public abstract void setIndexRegisterValue( IRegisterIndex r, int value, boolean ext);

	/**
	 * Set the value of the instruction index register value.
	 * 
	 * @param value the value
	 * @param ext this for extended register
	 */
	public abstract void setInstructionIndexRegisterValue( int value, boolean ext );

	// =========================================================================
	// = Stack =================================================================
	// =========================================================================

	/**
	 * Set the value of the stack register. This method abstracts differences
	 * among devices.
	 * 
	 * @param address the stack pointer value
	 */
	public abstract void setStackPointer( int address );

	/**
	 * Push a value onto stack.
	 * 
	 * @param value the value
	 */
	public abstract void push( int value );

	/**
	 * Pop a value from stack.
	 * 
	 * @return the value
	 */
	public abstract int pop();

	// =========================================================================
	// = Core loaders ==========================================================
	// =========================================================================

	/**
	 * For devices that supports the boot loader feature, install a fake
	 * version that does nothing but jump to the reset vector.
	 */
	public abstract void installFakeBootLoader();

	/**
	 * Load an iHex file into the flash memory.
	 * 
	 * @param ihexFilename the File object for the file.
	 * @throws IOException
	 */
	public abstract void loadFlash( File ihexFilename )
		throws IOException;

	/**
	 * Load an iHex file into the sram memory.
	 * 
	 * @param ihexFilename the File object for the file.
	 * @throws IOException
	 */
	public abstract void loadSram( File ihexFilename )
		throws IOException;

	/**
	 * Load an iHex file into the eeprom memory.
	 * 
	 * @param ihexFilename the File object for the file.
	 * @throws IOException
	 */
	public abstract void loadEeprom( File ihexFilename )
		throws IOException;

	// -------------------------------------------------------------------------

	/**
	 * Load a program using a user supplied method.
	 * Use this for debugging purpose only.
	 * <p>
	 * <b>!!! NO SUPPORT !!!</b>
	 * 
	 * @param loader the loader
	 */
	@Deprecated
	public abstract void loadProgram( Consumer<Instruction []> loader );

//	@Deprecated
//	public void programDump( BiConsumer<Integer,Instruction> consumer )
//	{
//		for( int i = 0 ; i < flash.length ; i++ )
//		{
//			if( flash[i] != null )
//			{
//				consumer.accept( i*2, flash[i] );
//				if( flash[i].getOpcodeSize() == 2 )
//				{
//					i++;
//				}
//			}
//		}
//	}

	// ========================================================================
	// === SYMBOLS ============================================================
	// ========================================================================

	/**
	 * Automatically add a symbol based on the instruction, like target address
	 * of call and jumps.
	 * 
	 * @param instruction an instruction
	 */
	protected void automaticSymbol( Instruction instruction )
	{
		if( instruction instanceof FlowInstruction )
		{
			int address = ((FlowInstruction)instruction).getTargetAddress();

			StringBuilder symbol = new StringBuilder();

			if( instruction.getCellAddress() == 0x00000 )
			{
				symbol.append( "APPLICATION_entry" );
			}
			else
			{
				switch( instruction.getInstructionSetEntry() )
				{
					case CALL:
					case RCALL:
						symbol.append( "__func_" );
						break;

					case JMP:
					case RJMP:
						symbol.append( "__label_" );
						break;

					default:
						symbol.append( "__" );
						break;
				}

				symbol.append( String.format( "%06X", address ) );
			}

			if( getSymbol( CoreMemory.FLASH, address ) == null )
			{
				addSymbol( CoreMemory.FLASH, address, symbol.toString() );
			}
		}

		else if( instruction instanceof TransferInstruction )
		{
			int address = ((TransferInstruction)instruction).getTargetAddress();
			if( address >= this.getDescriptor().getSramBase() )
			{
				StringBuilder symbol = new StringBuilder();
			
				symbol.append( "__data_" );
				symbol.append( String.format( "%06X", address ) );

				if( getSymbol( CoreMemory.SRAM, address ) == null )
				{
					addSymbol( CoreMemory.SRAM, address, symbol.toString() );
				}
			}
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSymbol( CoreMemory memory, int address )
	{
		switch( memory )
		{
			case FLASH:
				return flashSymbolsByAddresse.get( address );

			case SRAM:
				return sramSymbolsByAddresse.get( address );

			case EEPROM:
				return eepromSymbolsByAddresse.get( address );
		}

		return null;
	}

	/**
	 * Add a symbol.
	 * 
	 * @param memory the memory zone where the symbol is defined
	 * @param address the address of the symbol
	 * @param symbol the name of the symbol
	 */
	public void addSymbol( CoreMemory memory, int address, String symbol )
	{	
		switch( memory )
		{
			case FLASH:
				flashSymbolsByAddresse.put( address, symbol );
				break;

			case SRAM:
				sramSymbolsByAddresse.put( address, symbol );
				break;

			case EEPROM:
				eepromSymbolsByAddresse.put( address, symbol );
				break;
		}
	}

	// FIXME symbols should exist by name !!!

	/**
	 * Remove a symbol
	 * 
	 * @param memory
	 * @param address
	 */
	public void removeSymbol( CoreMemory memory, int address )
	{
		switch( memory )
		{
			case FLASH:
				flashSymbolsByAddresse.remove( address );
				break;

			case SRAM:
				sramSymbolsByAddresse.remove( address );
				break;

			case EEPROM:
				eepromSymbolsByAddresse.remove( address );
				break;
		}
	}

	/**
	 * Remove all symbols a a memory zone.
	 * 
	 * @param memory the memory zone
	 */
	public void flushSymbols(  CoreMemory memory )
	{
		switch( memory )
		{
			case FLASH:
				flashSymbolsByAddresse.clear();
				break;

			case SRAM:
				sramSymbolsByAddresse.clear();
				break;

			case EEPROM:
				eepromSymbolsByAddresse.clear();
				break;
		}
	}

	/**
	 * Remove all symbols
	 */
	public void flushSymbols()
	{
		flashSymbolsByAddresse.clear();
		sramSymbolsByAddresse.clear();
		eepromSymbolsByAddresse.clear();
	}

	// =========================================================================
	// === Events ==============================================================
	// =========================================================================

	/**
	 * Add a CoreEventListener to this core.
	 * 
	 * @param listener the listener
	 */
	public void addCoreEventListener( CoreEventListener listener )
	{
		if( coreEventListener.contains( listener ) )
		{
			return;
		}

		coreEventListener.add( listener );
	}

	/**
	 * Remove a CoreEventListener from this core.
	 * 
	 * @param listener the listener
	 */
	public void removeCoreEventListener( CoreEventListener listener )
	{
		if( ! coreEventListener.contains( listener ) )
		{
			return;
		}

		coreEventListener.remove( listener );
	}

	/**
	 * Fire a CoreEvent for every registererd listeners.
	 * 
	 * @param ev the CoreEvent
	 * @return veto
	 */
	public boolean fireCoreEvent( CoreEvent ev )
	{
		coreEventListener.forEach( listener -> listener.onEvent( ev ) );

		if( ev.shouldAbort() )
		{
			throw new CoreAbortException( ev );
		}

		return ev.hasVeto();
	}

	// -------------------------------------------------------------------------

	/**
	 * 
	 * @param entry the faulty instruction type
	 * @return veto
	 */
	protected boolean unsupportedInstruction( InstructionSet entry )
	{		
		LOG.warning( "Instruction "
				+ entry.name() + " not supported by core "
				+ getDescriptor().getCoreVersion().getCode()
				+ " of " + getDescriptor().getPartName()
			);

		CoreEvent event
			= new CoreEvent( CoreEventType.UNSUPPORTED_INSTRUCTION, this );
		event.setInstructionSetEntry( entry );

		return fireCoreEvent( event );
	}

	/**
	 * 
	 * @param entry the faulty instruction type
	 * @param instruction the faulty instruction
	 * @return veto
	 */
	protected boolean unsupportedInstruction( InstructionSet entry, Instruction instruction )
	{		
		LOG.warning( "Instruction "
				+ entry.name() + " not supported by core "
				+ getDescriptor().getCoreVersion().getCode()
				+ " of " + getDescriptor().getPartName()
			);

		CoreEvent event
			= new CoreEvent( CoreEventType.UNSUPPORTED_INSTRUCTION, this );
		event.setInstruction( instruction );
		event.setInstructionSetEntry( entry );

		return fireCoreEvent( event );
	}

	/**
	 * 
	 * @param instruction the faulty instruction
	 * @param opcode the expected opcode value
	 * @return veto
	 */
	protected boolean invalidOpcode( Instruction instruction, int opcode )
	{
		LOG.severe( "Opcode not preserved for "
				+ instruction.getInstructionSetEntry().name() + " : "
				+ String.format( "0x%04X --> 0x%04X", opcode, instruction.getOpcode() )
			);
	
		CoreEvent event
			= new CoreEvent( CoreEventType.INVALID_OPCODE, this );
		event.setInstruction( instruction );

		return fireCoreEvent( event );
	}

	/**
	 * 
	 * @param instruction the faulty instruction
	 * @param opcode2 the expected opcode value
	 * @return veto
	 */
	protected boolean invalidOpcode2( Instruction instruction, int opcode2 )
	{		
		LOG.severe( "Opcode not preserved for second word of "
				+ instruction.getInstructionSetEntry().name() + " : "
				+ String.format( "0x%04X --> 0x%04X", opcode2, instruction.getSecondWord().getOpcode() )
			);

		CoreEvent event
			= new CoreEvent( CoreEventType.INVALID_OPCODE_2, this );
		event.setInstruction( instruction );

		return fireCoreEvent( event );
	}
	
	/**
	 * 
	 * @param data the faulty data
	 * @param opcode the expected opcode value
	 * @return veto
	 */
	protected boolean invalidData( Instruction data, int opcode )
	{
		LOG.severe( "DataInstruction not preserved "
				+ String.format( "0x%04X --> 0x%04X", opcode, data.getOpcode() )
			);

		CoreEvent event
			= new CoreEvent( CoreEventType.INVALID_DATA, this );
		event.setInstruction( data );

		return fireCoreEvent( event );
	}

	/**
	 * 
	 * @param address the faulty address
	 * @return veto
	 */
	protected boolean flashMemoryOverwritting( int address )
	{
		LOG.warning( "Overwritting flash at address "
				+ String.format( "%06X", address ) );

		CoreEvent event
			= new CoreEvent( CoreEventType.FLASH_MEMORY_OVERWRITING, this );

		return fireCoreEvent( event );
	}

	// ========================================================================
	// = 
	// ========================================================================



	// ========================================================================
	// = Utilities ============================================================
	// ========================================================================

	private static final Logger LOG
		= Logger.getLogger( Core.class.getName() );
}
