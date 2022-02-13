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

package be.lmenten.avr.core;

import java.util.Properties;

import be.lmenten.avr.core.data.CoreControlRegister;
import be.lmenten.avr.core.data.CoreData;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.data.CoreStatusRegister;
import be.lmenten.avr.core.descriptor.CoreDescriptor;
import be.lmenten.avr.core.descriptor.CoreMemory;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.mcu.RunningMode;
import be.lmenten.avr.core.register.IRegister;
import be.lmenten.avr.core.register.IRegisterIndex;

/**
 * CoreModel is the viewer interface of the core. It exposes the getters
 * for the Core objects implementation and is focussed on UI components
 * usage.
 * 
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 08 / 23
 */
public interface CoreModel
{
	// ========================================================================
	// = Core descriptor ======================================================
	// ========================================================================

	/**
	 * Get the core descriptor of this device.
	 * 
	 * @return the core descriptor
	 */
	public CoreDescriptor getDescriptor();

	/**
	 * Get the default configuration for this device. If the parameter is null
	 * a new Properties object is created.
	 * 
	 * @param config a Properties object to receive the configuration or null
	 * @return a Properties object containing the definition
	 */
	public Properties getDefaultConfig( Properties config );

	// ------------------------------------------------------------------------

	/**
	 * Checks if the core supports an instruction. The check is made against
	 * the core version as well as the specificity of the specific device.
	 * 
	 * @param instruction the instruction
	 * @return true if instruction supported
	 */
	public boolean supportsInstruction( InstructionSet instruction );

	// ------------------------------------------------------------------------

	/**
	 * Checks if the core supports boot loader sections.
	 * 
	 * @return true if supported
	 */
	public boolean supportsBootLoaderSection();
	
	/**
	 * Get the base address of the application section.
	 * 
	 * @return the base address (in bytes) of the application section
	 */
	public int getApplicationSectionBase();

	/**
	 * Get the size of the application section. This is computed using the
	 * fuse's BOOTSZ bits value from the core configuration.
	 * 
	 * @return the size (in bytes) of the application section
	 */
	public int getApplicationSectionSize();

	/**
	 * Get the base address of the boot loader section.
	 * 
	 * @return the base address (in bytes) of the boot loader section
	 */
	public int getBootLoaderSectionBase();

	/**
	 * Get the size of the boot loader section. This is computed using the
	 * fuse's BOOTSZ bits value from the core configuration.
	 * 
	 * @return the size (in bytes) of the boot loader section
	 */
	public int getBootLoaderSectionSize();

	// ------------------------------------------------------------------------

	/**
	 * Get the base of the interrupt vectors table.
	 * 
	 * @return the base (in bytes) of the interrupt vectors table
	 */
	public int getInterrutVectorsBase();

	// ------------------------------------------------------------------------

	/**
	 * Checks if the core supports external memory.
	 * 
	 * @return true if supported
	 */
	public boolean supportsExternalMemoryFeature();

	/**
	 * Get the size of the installed external memory from the core
	 * configuration.
	 * 
	 * @return the size (in bytes) of the external memory
	 */
	public int getExternalSramSize();

	// ========================================================================
	// = Core execution =======================================================
	// ========================================================================

	/**
	 * Get the current program counter value.
	 * 
	 * @return the program counter value
	 */
	public int getProgramCounter();

	/**
	 * Get the current instruction (that is, the instruction pointed by the
	 * program counter).
	 * 
	 * @return the instruction
	 */
	public Instruction getCurrentInstruction();

	/**
	 * Get instruction at address.
	 *
	 * @param address the target address
	 * @return the instruction
	 */
	public Instruction getInstruction( int address );

	/**
	 * Get the instruction that was executed just before
	 * 
	 * @return the instruction
	 */
	public Instruction getPreviouslyExecutedInstruction();

	/**
	 * Get the instruction after current instruction.
	 * 
	 * @return the instruction
	 */
	public Instruction getFollowingInstruction();

	/**
	 * Get the current clock cycles counter value.
	 * 
	 * @return the clock cycles counter value
	 */
	public long getClockCyclesCounter();

	/**
	 * Get current device running mode.
	 * 
	 * @return the mode
	 */
	public RunningMode getCoreMode();

	// ------------------------------------------------------------------------

	/**
	 * Get the total count of interrupts that were triggered so far.
	 * 
	 * @return the count
	 */
	public long getInterruptsCount();

	/**
	 * Get the count of triggering that occurred for a specific interrupt
	 * vector so far.
	 * 
	 * @param vector the vector
	 * @return the count
	 */
	public long getInterruptCount( int vector );

	// ========================================================================
	// = Core registers =======================================================
	// ========================================================================

	/**
	 * On-chip Debug Register.
	 * 
	 * @return
	 */
	public boolean ioDebugRegisterDirty();


	// ------------------------------------------------------------------------

	/**
	 * <p>
	 * Get a copy of the StatusRegister. 
	 * 
	 * <p>
	 * A copy is given to allow full manipulation without any triggering of
	 * events.
	 * 
	 * @return a copy of the StatusRegister
	 */
	public CoreStatusRegister getStatusRegisterCopy();

	/**
	 * Get the current stack pointer value. This method abstracts differences
	 * among devices.
	 * 
	 * @return the stack pointer value
	 */
	public int getStackPointer();

	// ------------------------------------------------------------------------

	/**
	 * Get the value of the X, Y or Z index register
	 * 
	 * @param r the index register
	 * @param ext true for extended register
	 * @return the value
	 */
	public int getIndexRegisterValue( IRegisterIndex r, boolean ext );

	/**
	 * Get the value of the instruction index register value.
	 * 
	 * @param ext this for extended register
	 * @return the value
	 */
	public int getInstructionIndexRegisterValue( boolean ext );

	// ------------------------------------------------------------------------

	/**
	 * Get a General Purpose Register (R0-R31) by it register number
	 * 
	 * @param reg the register identifier
	 * @return the register
	 */
	public CoreRegister getRegister( IRegister reg );

	/**
	 * Get a register.
	 * 
	 * @param reg the register identifier
	 * @return the register
	 */
	public CoreRegister getRegister( CoreControlRegister reg );

	/**
	 * Get a register by its name.
	 * 
	 * @param name the register name
	 * @return the register
	 */
	public CoreRegister getRegister( String name );

	/**
	 * Get an I/O Register by its address as passed to IN/OUT (0..63) or
	 * CBI/SBI/SBIC/SBIS (0..31) instructions.
	 * 
	 * @param address
	 * @return the registe
	 */
	public CoreRegister getIORegisterByAddress( int address );

	/**
	 * Get an I/O Register by its physical address.
	 * 
	 * @param address
	 * @return the register
	 */
	public CoreRegister getRegisterByPhysicalAddress( int address );

	// ------------------------------------------------------------------------

	/**
	 * Get a lock bits register by its name.
	 * 
	 * @param name
	 * @return
	 */
	public CoreRegister getLockBits( String name );

	/**
	 * Get a fuses register by its name.
	 * 
	 * @param name
	 * @return
	 */
	public CoreRegister getFuseByte( String name );

	// ========================================================================
	// = Core memories ========================================================
	// ========================================================================

	/**
	 * Get a memory cell from the flash program memory.
	 * 
	 * @param address
	 * @return the memory cell
	 */
	public Instruction getFlashCell( int address );

	/**
	 * Get a memory cell from the sram data memory.
	 * 
	 * @param address
	 * @return
	 */
	public CoreData getSramCell( int address );

	// ========================================================================
	// = Symbols ==============================================================
	// ========================================================================

	/**
	 * Get the symbol for an address if any exists.
	 * 
	 * @param memory the memory zone 
	 * @param address the address
	 * @return the symbol or null
	 */
	public String getSymbol( CoreMemory memory, int address );

	// ========================================================================
	// = Config ===============================================================
	// ========================================================================

	public Boolean getBooleanConfig( String key );
	public Integer getIntegerConfig( String key );
	public Long getLongConfig( String key );
	public String getStringConfig( String key );
}
