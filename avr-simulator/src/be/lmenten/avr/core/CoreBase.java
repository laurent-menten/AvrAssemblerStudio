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
import java.util.StringJoiner;
import java.util.logging.Logger;

import be.lmenten.avr.core.data.CoreControlRegister;
import be.lmenten.avr.core.data.CoreData;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.data.CoreRegisterType;
import be.lmenten.avr.core.data.CoreStatusRegister;
import be.lmenten.avr.core.descriptor.CoreDescriptor;
import be.lmenten.avr.core.descriptor.CoreFeatures;
import be.lmenten.avr.core.descriptor.CoreMemory;
import be.lmenten.avr.core.descriptor.CoreRegisterDescriptor;
import be.lmenten.avr.core.driver.Driver;
import be.lmenten.avr.core.driver.MemoryDriver;
import be.lmenten.avr.core.driver.eeprom.EepromDriver;
import be.lmenten.avr.core.driver.mcu.McuSurveyor;
import be.lmenten.avr.core.driver.mcu.WatchdogDriver;
import be.lmenten.avr.core.driver.xmemory.ExternalMemoryDriver;
import be.lmenten.avr.core.event.CoreEvent;
import be.lmenten.avr.core.event.CoreEventType;
import be.lmenten.avr.core.exception.ConfigurationException;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.InstructionStatus;
import be.lmenten.avr.core.instruction.flow.FlowInstruction;
import be.lmenten.avr.core.instruction.flow.RET;
import be.lmenten.avr.core.instruction.transfer.IN;
import be.lmenten.avr.core.instruction.transfer.OUT;
import be.lmenten.avr.core.instruction.transfer.TransferInstruction;
import be.lmenten.avr.core.mcu.ResetSources;
import be.lmenten.avr.core.mcu.RunningMode;
import be.lmenten.avr.core.register.IRegister;
import be.lmenten.avr.core.register.IRegisterIndex;
import be.lmenten.avr.core.register.RegisterXYZ;
import be.lmenten.avr.utils.StringUtils;

/**
 * CoreBase is the most basic implentation of the core mostly dealing with
 * execution of instruction and the peripherals common to every devices.
 * 
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 08 / 23
 */
/*package*/ abstract class CoreBase
	extends Core
{
	// -------------------------------------------------------------------------
	// - Memories --------------------------------------------------------------
	// -------------------------------------------------------------------------

	private final CoreRegister [] fuses;

	private final CoreRegister [] lockBits;

	// -------------------------------------------------------------------------
	// - Registers shortcuts ---------------------------------------------------
	// -------------------------------------------------------------------------

	public final CoreStatusRegister SREG;	// Status Register
	public final CoreRegister MCUSR;		// MCU Status Register
	public final CoreRegister MCUCR;		// MCU Control Register
	public final CoreRegister WDTCSR;		// Watchdog Timer Control Register

	public final CoreRegister SPH;			// StackPointer
	public final CoreRegister SPL;

	// -------------------------------------------------------------------------

	public final CoreRegister SMCR;			// 
	public final CoreRegister PRR0;			// 
	public final CoreRegister PRR1;			// 
	
	public final CoreRegister OSCCAL;		// 
	public final CoreRegister CLKPR;		// 
	
	// -------------------------------------------------------------------------

	public final CoreRegister RAMPD;		// 
	public final CoreRegister RAMPX;		// 
	public final CoreRegister RAMPY;		// 
	public final CoreRegister RAMPZ;		// 
	public final CoreRegister EIND;			// 

	// -------------------------------------------------------------------------
	// - Configuration ---------------------------------------------------------
	// -------------------------------------------------------------------------

	private final Properties userConfig;

	protected final MemoryDriver eepromDriver;
	protected final MemoryDriver externalRamDriver;

	protected final Driver surveyorDriver;
	protected final Driver watchdogDriver;

	// -------------------------------------------------------------------------

	/**
	 * Check for missing instruction
	 */
	protected boolean instructionCheckEnabled = true;

	// -------------------------------------------------------------------------

	/**
	 * 
	 */
//	private int externalSramSize = 0;

	// -------------------------------------------------------------------------
	// - Runtime ---------------------------------------------------------------
	// -------------------------------------------------------------------------

	private boolean ioDebugRegisterDirty = false;

	private int programCounter;

	private long clockCyclesCounter;
	
	private int [] pendingInterrupts;

	private long interruptsCount;
	private long [] interruptCount;

	private RunningMode coreMode = RunningMode.STOPPED;
	private Instruction previouslyExecutedInstruction;

	// =========================================================================
	// === CONSTRUCTOR(S) ======================================================
	// =========================================================================

	/**
	 * Create a default basic code for this device.
	 * 
	 * @param cdesc the descriptor for this device
	 */
	CoreBase( CoreDescriptor cdesc )
	{
		this( cdesc, null );
	}

	/**
	 * Create and configure a basic code for this device.
	 * 
	 * @param cdesc the descriptor for this device
	 * @param config the configuration for this device
	 */
	CoreBase( CoreDescriptor cdesc, Properties config )
	{
		super( cdesc );

		this.userConfig = (config != null) ? config : getDefaultConfig( null );

		// ----------------------------------------------------------------------
		// - CORE ---------------------------------------------------------------
		// ----------------------------------------------------------------------

		LOG.info( "Core: " + cdesc.getPartName() );

		StringJoiner sj = new StringJoiner( ", " );
		for( CoreFeatures feature : cdesc.getFeatures() )
		{
			sj.add( feature.toString() );
		}
		LOG.fine( " > features : " + sj );

		// ----------------------------------------------------------------------
		// - CORE RUNTIME CONFIGURATION -----------------------------------------
		// ----------------------------------------------------------------------

		privParseConfig();

		parseConfig( userConfig );

		// ----------------------------------------------------------------------
		// - FUSES / LOCKBITS ---------------------------------------------------
		// ----------------------------------------------------------------------

		LOG.fine( " > fuse bytes : " + cdesc.getFusesCount() );

		fuses = new CoreRegister [ cdesc.getFusesCount() ];
		cdesc.exportFuses( (addr, rdesc) -> 
		{
			CoreRegister register = new CoreRegister( addr, rdesc );

			configureRegister( register );
				
			fuses[ addr ] = register;

			LOG.finer( "    > " + register );
		} );

		// ----------------------------------------------------------------------

		LOG.fine( " > lockbits bytes : " + cdesc.getLockBitsCount() );

		lockBits = new CoreRegister [ cdesc.getLockBitsCount() ];
		cdesc.exportLockBits( (addr, rdesc) ->
		{
			CoreRegister register = new CoreRegister( addr, rdesc );

			configureRegister( register );

			lockBits[ addr ] = register;

			LOG.finer( "    > " + register );
		} );

		// ----------------------------------------------------------------------
		// - FLASH --------------------------------------------------------------
		// ----------------------------------------------------------------------

		LOG.info( "Flash size = " + cdesc.getFlashSize() + " bytes" );

		flash = new Instruction [ cdesc.getFlashSize() / 2 ];
		for( int addr = 0 ; addr < flash.length ; addr++ )
		{
			flash[ addr ] = null;
		}

		if( supportsBootLoaderSection() )
		{
			LOG.fine( " > Application section: "
				+ String.format( "0x%06X", getApplicationSectionBase() )
				+ " ("
				+ getApplicationSectionSize() + " bytes)" );

			LOG.fine( " > Boot loader section: "
				+ String.format( "0x%06X", getBootLoaderSectionBase() )
				+ " ("
				+ getBootLoaderSectionSize() + " bytes)" );
		}

		pendingInterrupts = new int [cdesc.getInterruptsCount()];

		interruptsCount = 0l;
		interruptCount = new long [cdesc.getInterruptsCount()];

		for( int i = 0 ; i < cdesc.getInterruptsCount() ; i++ )
		{
			pendingInterrupts[i] = 0;
			interruptCount[i] = 0l;
		}

		// ----------------------------------------------------------------------
		// - SRAM ---------------------------------------------------------------
		// ----------------------------------------------------------------------

		StringBuilder txtRamSize = new StringBuilder();

		txtRamSize.append( "Internal sram size = "  )
			.append( cdesc.getRegistersCount() ).append( "+" )
			.append( cdesc.getIoRegistersCount() ).append( "+" )
			.append( cdesc.getExtendedIoRegistersCount() ).append( "+" )
			.append( cdesc.getSramSize() );

		txtRamSize.append(  " = " )
			.append( cdesc.getOnChipSramSize() )
			.append(  " bytes" )
			;

		LOG.info( txtRamSize.toString() );

		sram = new CoreData [ cdesc.getOnChipSramSize() ];
		for( int addr = 0 ; addr < sram.length ; addr++ )
		{
			sram[ addr ] = null;
		}

		// - R0 .. Rxx ----------------------------------------------------------

		cdesc.exportRegisters( ( addr, rdesc ) ->
		{
			CoreRegister reg = new CoreRegister( addr, rdesc ); 
			reg.setCellAddress( addr );

			sram[ addr ] = reg;			
		} );
		
		LOG.fine( " > " + cdesc.getRegistersCount() + " general registers");
		
		// - I/O registers ------------------------------------------------------

		for( int i = 0; i < cdesc.getIoRegistersCount() ; i++ )
		{
			CoreRegister reg;
			int address = cdesc.getIoRegistersBase() + i;

			CoreRegisterDescriptor rdesc = cdesc.getRegisterDescriptor( address );
			if( rdesc != null )
			{				
				if( rdesc.getName().equals( "SREG" ) )
				{
					reg = new CoreStatusRegister( address, rdesc );
				}
				else
				{
					reg = new CoreRegister( address, rdesc );
				}
			}
			else
			{
				rdesc = new CoreRegisterDescriptor( cdesc, CoreRegisterType.RESERVED, address, "-" );
				reg = new CoreRegister( address, rdesc );
			}

			reg.setCellAddress( address );
			sram[ address ] = reg;
		}

		LOG.fine( " > " + cdesc.getIoRegistersCount() + " I/O registers");

		// - Extended I/O registers ---------------------------------------------

		for( int i = 0; i < cdesc.getExtendedIoRegistersCount() ; i++ )
		{
			CoreRegister reg;
			int address = cdesc.getExtendedIoRegistersBase() + i;

			CoreRegisterDescriptor rdesc = cdesc.getRegisterDescriptor( address );
			if( rdesc != null )
			{				
				reg = new CoreRegister( address, rdesc );
			}
			else
			{
				rdesc = new CoreRegisterDescriptor( cdesc, CoreRegisterType.RESERVED, address, "-" );
				reg = new  CoreRegister( address, rdesc );
			}

			reg.setCellAddress( address );
			sram[ address ] = reg;
		}

		LOG.fine( " > " + cdesc.getExtendedIoRegistersCount() + " extended I/O registers");

		// - Internal memory clear off ----------------------------------------
		// FIXME is this needed ?

		for( int addr = 0 ; addr < cdesc.getSramSize(); addr++ )
		{
			int address = cdesc.getSramBase() + addr;
			CoreData data = new CoreData( address ); 
			sram[ address ] = data;
		}

		// ----------------------------------------------------------------------
		// - External memorry ---------------------------------------------------
		// ----------------------------------------------------------------------

		if( supportsExternalMemoryFeature() )
		{
			LOG.info( "++ Loading external memory driver" );

			externalRamDriver = new ExternalMemoryDriver( "externalRam-control", this );

			registerDriver( externalRamDriver );
		}
		else
		{
			LOG.info( "-- No external memory support" );

			externalRamDriver = null;
		}

		// ----------------------------------------------------------------------
		// - EEPROM -------------------------------------------------------------
		// ----------------------------------------------------------------------

		if( cdesc.hasEeprom() )
		{
			LOG.info( "++ Loading eeprom driver" );

			eepromDriver = new EepromDriver( "eeprom-control", this );

			registerDriver( eepromDriver );
		}
		else
		{
			LOG.info( "-- No eeprom support" );

			eepromDriver = null;
		}

		// ----------------------------------------------------------------------
		// - MCU Registers shortcuts --------------------------------------------
		// ----------------------------------------------------------------------

		CoreRegisterDescriptor rdesc;

		rdesc = cdesc.getRegisterDescriptor( "SREG" );
		SREG = (CoreStatusRegister) sram[ rdesc.getAddress() ];

		rdesc = cdesc.getRegisterDescriptor( "MCUCR" );
		MCUCR = (CoreRegister) sram[ rdesc.getAddress() ];

		rdesc = cdesc.getRegisterDescriptor( "MCUSR" );
		MCUSR = (CoreRegister) sram[ rdesc.getAddress() ];

		rdesc = cdesc.getRegisterDescriptor( "WDTCSR" );
		WDTCSR = (CoreRegister) sram[ rdesc.getAddress() ];

		// ----------------------------------------------------------------------

		rdesc = cdesc.getRegisterDescriptor( "SPL" );
		SPL = (CoreRegister) sram[ rdesc.getAddress() ];

		rdesc = cdesc.getRegisterDescriptor( "SPH" );
		if( rdesc != null )
		{
			SPH = (CoreRegister) sram[ rdesc.getAddress() ];
		}
		else
		{
			SPH = null;
		}

		// TODO add CCP from Xmega architecture

		// ----------------------------------------------------------------------

		rdesc = cdesc.getRegisterDescriptor( "OSCCAL" );
		OSCCAL = (CoreRegister) sram[ rdesc.getAddress() ];

		rdesc = cdesc.getRegisterDescriptor( "CLKPR" );
		CLKPR = (CoreRegister) sram[ rdesc.getAddress() ];
		
		rdesc = cdesc.getRegisterDescriptor( "SMCR" );
		SMCR = (CoreRegister) sram[ rdesc.getAddress() ];

		rdesc = cdesc.getRegisterDescriptor( "PRR0" );
		PRR0 = (rdesc != null) ? (CoreRegister) sram[ rdesc.getAddress() ] : null;

		rdesc = cdesc.getRegisterDescriptor( "PRR1" );
		PRR1 = (rdesc != null) ? (CoreRegister) sram[ rdesc.getAddress() ] : null;

		// ----------------------------------------------------------------------
		// - Extended indexes registers -----------------------------------------
		// ----------------------------------------------------------------------

		rdesc = cdesc.getRegisterDescriptor( "RAMPD" );
		if( rdesc != null )
		{
			RAMPD = (CoreRegister) sram[ rdesc.getAddress() ];
		}
		else
		{
			RAMPD = null;
		}

		// --------------------------------------------------------------------

		rdesc = cdesc.getRegisterDescriptor( "RAMPX" );
		if( rdesc != null )
		{
			RAMPX = (CoreRegister) sram[ rdesc.getAddress() ];
		}
		else
		{
			RAMPX = null;
		}

		rdesc = cdesc.getRegisterDescriptor( "RAMPY" );
		if( rdesc != null )
		{
			RAMPY = (CoreRegister) sram[ rdesc.getAddress() ];
		}
		else
		{
			RAMPY = null;
		}

		rdesc = cdesc.getRegisterDescriptor( "RAMPZ" );
		if( rdesc != null )
		{
			RAMPZ = (CoreRegister) sram[ rdesc.getAddress() ];
		}
		else
		{
			RAMPZ = null;
		}
	
		// --------------------------------------------------------------------

		rdesc = cdesc.getRegisterDescriptor( "EIND" );
		if( rdesc != null )
		{
			EIND = (CoreRegister) sram[ rdesc.getAddress() ];
		}
		else
		{
			EIND = null;
		}

		// ----------------------------------------------------------------------
		// - Default symbols ----------------------------------------------------
		// ----------------------------------------------------------------------

		addSymbol( CoreMemory.FLASH, 0x00000, "_RESET" );

		for( int i = 0 ; i < cdesc.getInterruptsCount() ; i++ )
		{
			int address = getInterruptVectorAddress( i );

			String name = cdesc.getInterruptName( i );
			if( name != null )
			{
				addSymbol( CoreMemory.FLASH, address, "_iv_" + name );				
			}
		}

		if( supportsBootLoaderSection() )
		{
			addSymbol( CoreMemory.FLASH, getBootLoaderSectionBase(), "_BOOTLOADER_entry" );
		}

		// ----------------------------------------------------------------------
		// - Install peripherals ------------------------------------------------
		// ----------------------------------------------------------------------

		surveyorDriver = new McuSurveyor( "surveyor" , this );
		registerDriver( surveyorDriver );

		watchdogDriver = new WatchdogDriver( "watchdog-control", this );
		registerDriver( watchdogDriver );

		// ----------------------------------------------------------------------

		installPeripherals();

		// ----------------------------------------------------------------------
		// - Finalisation -------------------------------------------------------
		// ----------------------------------------------------------------------

		reset( ResetSources.POWER_ON );
	}

	// ----------------------------------------------------------------------------

	/**
	 * 
	 */
	protected /*abstract*/ void installPeripherals()
	{		
	}

	// ============================================================================
	// = Configuration ============================================================
	// ============================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Properties getDefaultConfig( Properties config )
	{
		if( config == null )
		{
			config = new Properties();
		}

		if( supportsExternalMemoryFeature() )
		{
			config.put( CoreConfiguration.CONFIG_EXTERNAL_SRAM, 0 );
		}

		return config;
	}
	
	/**
	 * 
	 */
	private void privParseConfig()
	{
		if( userConfig == null )
		{
			return;
		}

		// ----------------------------------------------------------------------

		Boolean bValue = getBooleanConfig( CoreConfiguration.CONFIG_INSTRUCTION_CHECK );
		if( bValue != null )
		{
			instructionCheckEnabled = bValue;
		}		
	}

	/**
	 * 
	 * @param config
	 */
	protected void parseConfig( Properties config )
	{
	}

	// ========================================================================
	// = Configuration helpers ================================================
	// ========================================================================

	/**
	 * 
	 * @param register
	 */
	protected void configureRegister( CoreRegister register )
	{
		String name = register.getName();
		Integer regValue = getIntegerConfig( name );
		if( regValue != null )
		{
			register.setData( regValue & 0xFF );
		}

		for( int i = 0 ; i < 8 ; i++ )
		{
			String bitName = register.getBitName( i );
			if( bitName == null )
			{
				continue;
			}

			Boolean bitValue = getBooleanConfig( name + "." + bitName );
			if( bitValue != null )
			{
				register.bit( bitName, bitValue );
			}
		}		
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public Boolean getBooleanConfig( String key )
	{
		String value = userConfig.getProperty( key );
		if( value == null )
		{
			return null;
		}

		try
		{
			return StringUtils.parseBoolean( value );
		}
		catch( Exception ex )
		{
			throw new ConfigurationException(
				key,
				value,
				"Format error in coniguration",
				ex
			);
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public Integer getIntegerConfig( String key )
	{
		String value = userConfig.getProperty( key );
		if( value == null )
		{
			return null;
		}

		try
		{
			return (int) StringUtils.parseNumber( value);
		}
		catch( Exception ex )
		{
			throw new ConfigurationException(
				key,
				value, "Format error in configuration",
				ex
			);
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public Long getLongConfig( String key )
	{
		String value = userConfig.getProperty( key );
		if( value == null )
		{
			return null;
		}

		try
		{
			return StringUtils.parseNumber( value);
		}
		catch( Exception ex )
		{
			throw new ConfigurationException(
				key,
				value, "Format error in configuration",
				ex
			);
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public String getStringConfig( String key )
	{
		return userConfig.getProperty( key );
	}

	// =========================================================================
	// === DEBUG HELPERS =======================================================
	// =========================================================================

	/**
	 * {@inheritDoc}
	 */
	public void ioDebugRegisterDirty( boolean ioDebugRegisterDirty )
	{
		this.ioDebugRegisterDirty = ioDebugRegisterDirty;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean ioDebugRegisterDirty()
	{
		return ioDebugRegisterDirty;
	}

	// =========================================================================
	// === Descriptor ==========================================================
	// =========================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoreDescriptor getDescriptor()
	{
		return cdesc;
	}

	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportsBootLoaderSection()
	{
		return cdesc.hasFeature( CoreFeatures.BOOT_LOADER );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getApplicationSectionBase()
	{
		if( supportsBootLoaderSection() )
		{
			return 0x00000;
		}

		throw new UnsupportedOperationException( "MCU has no bootloader support" );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getApplicationSectionSize()
	{
		if( supportsBootLoaderSection() )
		{
			return cdesc.getFlashSize() - getBootLoaderSectionSize();
		}

		throw new UnsupportedOperationException( "MCU has no bootloader support" );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getBootLoaderSectionBase()
	{
		if( supportsBootLoaderSection() )
		{
			CoreRegister fuse = getFuseByte( "HIGH_BYTE" ) ;

			int bootsz = fuse.bits( "BOOTSZ1", "BOOTSZ0" );

			return cdesc.getBootLoaderSection( bootsz ).getRangeBase();
		}

		throw new UnsupportedOperationException( "MCU has no bootloader support" );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getBootLoaderSectionSize()
	{
		if( supportsBootLoaderSection() )
		{
			CoreRegister fuse = getFuseByte( "HIGH_BYTE" ) ;

			int bootsz = fuse.bits( "BOOTSZ1", "BOOTSZ0" );

			return cdesc.getBootLoaderSection( bootsz ).getRangeSize();
		}

		throw new UnsupportedOperationException( "MCU has no bootloader support" );
	}

	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportsExternalMemoryFeature()
	{
		return cdesc.hasFeature( CoreFeatures.EXTERNAL_SRAM );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getExternalSramSize()
	{
		if( supportsExternalMemoryFeature() )
		{
			return externalRamDriver.getManagedMemorySize();
		}

		throw new UnsupportedOperationException( "MCU has no external memory support" );
	}

	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportsInstruction( InstructionSet instruction )
	{
		return instruction.isSupportedBy( cdesc.getCoreVersion() )
				&& ! cdesc.isInstructionMissing( instruction );
	}

	// =========================================================================
	// === PROGRAM =============================================================
	// =========================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getProgramCounter()
	{
		return programCounter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProgramCounter( int address )
	{
		this.programCounter = address;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateProgramCounter( int offset )
	{
		this.programCounter += offset ;
	}

	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Instruction getCurrentInstruction()
	{
		return flash[ (programCounter / 2) ];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Instruction getInstruction( int address )
	{
		return flash[ (address / 2) ];
	}

	@Override
	public Instruction getPreviouslyExecutedInstruction()
	{
		return previouslyExecutedInstruction;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Instruction getFollowingInstruction()
	{
		Instruction current = getCurrentInstruction();
		if( current != null )
		{
			return flash[ (programCounter / 2) + current.getOpcodeSize() ];
		}

		return null;
	}

	// -------------------------------------------------------------------------

	@Override
	public int getInterrutVectorsBase()
	{
		int address = 0x00004;
		if( MCUCR.bit( "IVSEL" ) )
		{
			CoreRegister fuse = getFuseByte( "HIGH_BYTE" ) ;

			int bootsz = fuse.mask( "BOOTSZ1", "BOOTSZ0" ) >> 1;
			address += cdesc.getBootLoaderSection( bootsz ).getRangeBase();
		}

		return address;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInterruptVectorAddress( int vector )
	{
		if( vector == 0 ) // RESET
		{
			if( supportsBootLoaderSection() )
			{
				CoreRegister fuse = getFuseByte( "HIGH_BYTE" ) ;

				if( fuse.bit( "BOOTRST" ) )	// BOOTRST not programmed
				{
					return 0x0000;
				}

				else // BOOTRST programmed
				{
					return getBootLoaderSectionBase();
				}
			}
			else
			{
				return 0x0000;
			}
		}

		return getInterrutVectorsBase()
				+ ((vector-1) * cdesc.getInterruptVectorSize());
	}

	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pushProgramCounter()
	{
		int value = programCounter;

		for( int i = cdesc.getProgramCounterWidth() ; i > 0 ; i -= 8 )
		{
			byte b = (byte) (value & 0xFF);
			push( b );
			
			value >>= 8;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void popProgramCounter()
	{
		int value = 0x0000000;

		for( int i = cdesc.getProgramCounterWidth() ; i > 0 ; i -= 8 )
		{
			int b = pop();
			
			value = (value << 8) | (b & 0xFF);
		}

		programCounter = value;
	}

	// ========================================================================
	// === SREG ===============================================================
	// ========================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoreStatusRegister getStatusRegisterCopy()
	{
		return SREG.clone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStatusRegister( CoreStatusRegister sreg )
	{
		SREG.silentSetData( sreg.silentGetData() );
	}

	// ========================================================================
	// === STACK ==============================================================
	// ========================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getStackPointer()
	{
		int address = 0x0000;		
		if( SPH != null )
		{
			address = (SPH.silentGetData() & 0xFF) << 8;
		}
		address |= (SPL.silentGetData() & 0xFF);

		return address;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStackPointer( int address )
	{
		SPL.silentSetData( (byte) (address & 0xFF) ); 
		if( SPH != null )
		{
			SPH.silentSetData( (byte) ((address >> 8) & 0xFF) );
		}		
	}

	// ------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void push( int value )
	{
		int address = getStackPointer();

		sram[ address-- ].silentSetData( value );

		setStackPointer( address );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int pop()
	{
		int address = getStackPointer();

		byte value = (byte) sram[ ++address ].silentGetData();

		setStackPointer( address );

		return value;
	}

	// ========================================================================
	// === CLOCK ==============================================================
	// ========================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getClockCyclesCounter()
	{
		return clockCyclesCounter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateClockCyclesCounter( long clockCyclesCount )
	{
		this.clockCyclesCounter += clockCyclesCount;
	}

	// ------------------------------------------------------------------------

	// FIXME don't assume running 16Mhz

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long nanosToTicks( double time )
	{
		return (long)( time / (1d/16000d) );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double ticksToNanos( long ticks )
	{
		return (double)( ticks * (1d/16000d) );
	}

	// ========================================================================
	// === RUNNING MODE =======================================================
	// ========================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RunningMode getCoreMode()
	{
		return coreMode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void setCoreMode( RunningMode coreMode )
	{
		CoreEvent ev = new CoreEvent( CoreEventType.CORE_MODE_CHANGED, this );
		ev.setOldValue( this.coreMode );
		ev.setNewValue( coreMode );

		if( ! fireCoreEvent( ev ) )
		{
			this.coreMode = coreMode;
		}
	}

	// ========================================================================
	// === EXECUTION ==========================================================
	// ========================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void step()
	{
		int savedProgramCounter = getProgramCounter();

		Instruction current = getCurrentInstruction();
		CoreEventType eventType = CoreEventType.NO_INSTRUCTION;

		if( SREG.i() )
		{
			for( int i = 0 ; i < pendingInterrupts.length ; i++ )
			{
				if( pendingInterrupts[i] > 0 )
				{
					pendingInterrupts[i]--;

					pushProgramCounter();
					setProgramCounter( getInterruptVectorAddress( i ) );

					SREG.i( false );

					current = getCurrentInstruction();
					eventType = CoreEventType.NO_INSTRUCTION_FOR_INTERRUPT;
					break;
				}
			}
		}

		if( current == null )
		{
			CoreEvent ev = new CoreEvent( eventType, this );
			ev.setInstruction( current );
			fireCoreEvent( ev );

			setCoreMode( RunningMode.STOPPED );
			return;
		}

		updateProgramCounter( current.getOpcodeSize() * 2 );

		// --------------------------------------------------------------------
		// - Simulate ---------------------------------------------------------
		// --------------------------------------------------------------------

		long tmpCycles = getClockCyclesCounter();

		try
		{
			current.execute( this );
		}
		catch( Throwable ex)
		{
			setCoreMode( RunningMode.STOPPED );
			setProgramCounter( savedProgramCounter );

			throw ex;
		}

		current.recordExecuteAccess( this, current );
		previouslyExecutedInstruction = current;

		assert tmpCycles != getClockCyclesCounter()
			: current.getInstructionSetEntry().name() + " did not update the cycles counter"
			;

		tickDrivers();

		// --------------------------------------------------------------------
		// - Breakpoint -------------------------------------------------------
		// --------------------------------------------------------------------

		Instruction next  = getCurrentInstruction();
		if( next != null )
		{
			if( current == next )
			{
				CoreEvent ev = new CoreEvent( CoreEventType.INFINITE_LOOP, this );
				ev.setInstruction( current );
				fireCoreEvent( ev );
			}

			if( next.breakpointEnabled() )
			{
				setCoreMode( RunningMode.STOPPED );
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stepOver()
	{
		Instruction current = getCurrentInstruction();
		if( current == null )
		{
			// FIXME handle execution of nothing
			return;
		}

		int targetProgramCounter
			= getProgramCounter() + (current.getOpcodeSize() * 2);

		while( coreMode == RunningMode.RUNNING )
		{
			if( getProgramCounter() == targetProgramCounter )
			{
				setCoreMode( RunningMode.STOPPED );
				continue;
			}

			step();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stepOut()
	{
		Instruction current;

		setCoreMode( RunningMode.RUNNING );

		do
		{
			current = getCurrentInstruction();
			if( current == null )
			{
				// FIXME handle execution of nothing
				return;
			}

			step();
			
			if( (current instanceof RET) /*|| (current instanceof RETI)*/ )
			{
				setCoreMode( RunningMode.STOPPED );
			}
		}
		while( coreMode == RunningMode.RUNNING );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run()
	{
		setCoreMode( RunningMode.RUNNING );

		do
		{
			step();
		}
		while( coreMode == RunningMode.RUNNING );
	}

	// ========================================================================
	// = 
	// ========================================================================

	@Override
	public void interrupt( String name )
	{
		int vector = cdesc.getInterruptVector( name );
		// FIXME check for error

		interrupt( vector );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void interrupt( int vector )
	{
		pendingInterrupts[vector] ++;

		interruptsCount++;
		interruptCount[vector]++;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getInterruptsCount()
	{
		return interruptsCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getInterruptCount( int vector )
	{
		return interruptCount[vector];
	}

	// ------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset( ResetSources resetSource )
	{
		clockCyclesCounter = 0l;

		// --------------------------------------------------------------------

		interruptsCount = 0l;

		for( int i = 0 ; i < interruptCount.length ; i++ )
		{
			interruptCount[i] = 0l;
		}

		// --------------------------------------------------------------------
		// --- RESET ISR address ----------------------------------------------	
		// --------------------------------------------------------------------

		if( supportsBootLoaderSection() )
		{
			CoreRegister fuse = getFuseByte( "HIGH_BYTE" ) ;

			if( fuse.bit( "BOOTRST" ) )	// BOOTRST not programmed
			{
				programCounter = 0x0000;
			}

			else // BOOTRST programmed
			{
				programCounter = getBootLoaderSectionBase();
			}
		}
		else
		{
			programCounter = 0x0000;
		}

		// --------------------------------------------------------------------
		// --- SRAM default values --------------------------------------------
		// --------------------------------------------------------------------

		for( int i = 0 ; i < sram.length ; i++ )
		{
			if( sram[i] != null )
			{
				sram[i].reset();
			}
		}

		// --------------------------------------------------------------------
		// - 
		// --------------------------------------------------------------------
	
		resetDrivers();

		// --------------------------------------------------------------------
		// --- Set reset source flag in MCUSR --------------------------------- 
		// --------------------------------------------------------------------

		switch( resetSource )
		{
			case POWER_ON:
				MCUSR.bit( "PORF", true );
				break;
				
			case EXTERNAL:
				MCUSR.bit( "EXTRF", true );
				break;
				
			case BROWN_OUT:
				MCUSR.bit( "BORF", true );
				break;
				
			case WATCHDOG:
				MCUSR.bit( "WDRF", true );
				break;
				
			case JTAG:
				MCUSR.bit( "JTRF", true );
				break;				
		}

		// --------------------------------------------------------------------
		// ---
		// --------------------------------------------------------------------

		SREG.bit( "I", false );

		// --------------------------------------------------------------------

		// TODO reset components

		// --------------------------------------------------------------------
		// ---
		// --------------------------------------------------------------------

		coreMode = RunningMode.RUNNING;
	}

	// ========================================================================
	// === REGISTERS ==========================================================
	// ========================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoreRegister getRegister( IRegister r )
	{
		int rIndex = r.getIndex();
		if( rIndex >= cdesc.getRegistersCount() )
		{
			throw new IllegalArgumentException();
		}

		return (CoreRegister) sram[ cdesc.getRegistersBase() + rIndex ];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoreRegister getRegister( CoreControlRegister reg )
	{
		switch( reg )
		{
			case SREG:		return SREG;
			case SPH:		return SPH;
			case SPL:		return SPL;

			case MCUCR:		return MCUCR;
			case MCUSR:		return MCUSR;
			case WDTCSR:	return WDTCSR;

			case SMCR:		return SMCR;
			case PRR0:		return PRR0;
			case PRR1:		return PRR1;

			case CLKPR:		return CLKPR;
			case OSCCAL:	return OSCCAL;

			case RAMD:		return RAMPD;
			case RAMX:		return RAMPX;
			case RAMY:		return RAMPY;
			case RAMZ:		return RAMPZ;
			case EIND:		return EIND;
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoreRegister getRegister( String name )
	{
		CoreRegisterDescriptor rdesc = cdesc.getRegisterDescriptor( name );
		if( rdesc == null )
		{
			return null;
		}
		
		return (CoreRegister) sram[ rdesc.getAddress() ];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoreRegister getIORegisterByAddress( int address )
	{
		if( (address < 0x00)
				|| (address >= cdesc.getIoRegistersCount()) )
		{
			throw new IllegalArgumentException( "address = " + address + " >=" + cdesc.getIoRegistersCount() );
		}

		return (CoreRegister) sram[ cdesc.getIoRegistersBase() + address ];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoreRegister getRegisterByPhysicalAddress( int address )
	{
		if( (address < 0x000000)
				|| (address >= cdesc.getSramBase()) )
		{
			throw new IllegalArgumentException( "address = " + address );
		}

		return (CoreRegister) sram[ address ];
	}

	// ------------------------------------------------------------------------

	/**
	 * Get Fuse byte by its name.
	 * 
	 * @param name
	 * @return
	 */
	public CoreRegister getFuseByte( String name )
	{
		CoreRegisterDescriptor rdesc = cdesc.getFuseDescriptor( name );
		if( rdesc == null )
		{
			return null;
		}

		return fuses[ rdesc.getAddress() ];
	}

	/**
	 * Get Lock Bits byte by its name.
	 * 
	 * @param name
	 * @return
	 */
	public CoreRegister getLockBits( String name )
	{
		CoreRegisterDescriptor rdesc = cdesc.getLockBitsDescriptor( name );
		if( rdesc == null )
		{
			return null;
		}

		return fuses[ rdesc.getAddress() ];
	}

	// ========================================================================
	// === MEMORY ACCESS ======================================================
	// ========================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Instruction getFlashCell( int address )
	{
		if( ! checkFlashAddress(address) )
		{
			throw new IndexOutOfBoundsException(address);
		}
		
//		if( flash[address / 2] == null )
//		{
//			flash[address / 2] = new DataInstruction( 0x0000 );
//		}

		return flash[address / 2];
	}

	/**
	 * 
	 * @param address
	 * @return
	 */
	public boolean checkFlashAddress( int address )
	{
		return (address >= 0) && (address < cdesc.getFlashSize());		
	}

	// ------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoreData getSramCell( int address )
	{
		if( address > cdesc.getSramLimit() )
		{
			if( supportsExternalMemoryFeature() )
			{
				return externalRamDriver.getMemoryCell( address );
			}

			throw new IndexOutOfBoundsException(address);
		}

		// lazy allocation
		
		if( sram[ address ] == null )
		{
			sram[ address ] = new CoreData( address );
		}

		return sram[ address ];
	}

	/**
	 * 
	 * @param address
	 * @return
	 */
	public boolean checkSramAddress( int address )
	{
//		if( supportsExternalMemoryFeature() )
//		{	
//			if( XMCRA.bit( "SRE" ) )
//			{
//				return (address >= 0) && (address < sram.length);
//			}
//		}

		return (address >= 0) && (address < cdesc.getOnChipSramSize());
	}

	/**
	 * 
	 * @param address
	 * @return
	 */
	public int maskExternalMemoryAddress( int address )
	{
//		if( supportsExternalMemoryFeature() )
//		{	
//			if( XMCRA.bit( "SRE" ) )
//			{
//				@SuppressWarnings("unused")
//				byte mask = XMCRB.mask( "XMM2", "XMM1", "XMM0" );
//
//				// TODO Handle external memory masking.
//
//				return address;
//			}
//		}

		return address;
	}

	// ========================================================================
	// === INDEX REGISTERS ====================================================
	// ========================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInstructionIndexRegisterValue( boolean ext )
	{
		int zAddress = 0;

		if( ext )
		{
			CoreRegister eind = getRegister( CoreControlRegister.EIND );
			if( eind != null )
			{
				zAddress = (eind.silentGetData() & 0xFF) << 16;
			}
		}

		CoreRegister h = getRegister( RegisterXYZ.Z.getUpperRegister() );
		CoreRegister l = getRegister( RegisterXYZ.Z.getLowerRegister() );
		zAddress += ((h.silentGetData() & 0xFF) << 8) + (l.silentGetData() & 0xFF);

		return zAddress;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInstructionIndexRegisterValue( int value, boolean ext )
	{
		if( ext )
		{
			CoreRegister eind = getRegister( CoreControlRegister.EIND );
			if( eind != null )
			{
				eind.silentSetData( (value >> 16 ) & 0xFF );
			}
		}

		CoreRegister h = getRegister( RegisterXYZ.Z.getUpperRegister() );
		CoreRegister l = getRegister( RegisterXYZ.Z.getLowerRegister() );

		h.silentSetData( value >> 8 );
		l.silentSetData( value & 0xFF);
	}

	// ------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIndexRegisterValue( IRegisterIndex r, boolean ext )
	{
		int zAddress = 0;

		if( ext )
		{
			CoreRegister ramp = getRegister( r.getExtendedRegister() );
			if( ramp != null )
			{
				zAddress = (ramp.silentGetData() & 0xFF) << 16;
			}
		}

		CoreRegister h = getRegister( r.getUpperRegister() );
		CoreRegister l = getRegister( r.getLowerRegister() );
		zAddress |= ((h.silentGetData() & 0xFF) << 8) + (l.silentGetData() & 0xFF);

		return zAddress;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setIndexRegisterValue( IRegisterIndex r, int value, boolean ext )
	{
		if( ext )
		{
			CoreRegister ramp = getRegister( r.getExtendedRegister() );
			if( ramp != null )
			{
				ramp.silentSetData( (byte) (value >> 16) );
			}
		}

		CoreRegister h = getRegister( r.getUpperRegister() );
		CoreRegister l = getRegister( r.getLowerRegister() );

		h.silentSetData( value >> 8 );
		l.silentSetData( value & 0xFF );
	}

	// ========================================================================
	// = Instruction checker ==================================================
	// ========================================================================

	protected InstructionStatus checkInstruction( Instruction instruction )
	{

		// --------------------------------------------------------------------
		// - CALLs / JUMPs and co. --------------------------------------------
		// --------------------------------------------------------------------

		if( instruction instanceof FlowInstruction )
		{
			FlowInstruction instr = (FlowInstruction)instruction;

			int targetAddress = instr.getTargetAddress();
			if( (targetAddress < 0x00000) || targetAddress > cdesc.getFlashSize() )
			{
				return InstructionStatus.FLASH_ADDRESS_OUT_OF_RANGE;
			}

			if( supportsBootLoaderSection() )
			{
				int address = instruction.getCellAddress();
				if( (address < getBootLoaderSectionBase())
						&& targetAddress >= getBootLoaderSectionBase() )
				{
					return InstructionStatus.EXECUTE_BOOTLOADER_FROM_APPLICATION;
				}
			}
		}

		// --------------------------------------------------------------------
		// - LDS / STS --------------------------------------------------------
		// --------------------------------------------------------------------

		else if( instruction instanceof TransferInstruction )
		{
			TransferInstruction instr = (TransferInstruction)instruction;

			int targetAddress = instr.getTargetAddress();
			if( targetAddress < cdesc.getSramBase() )
			{
				CoreRegister reg = getRegisterByPhysicalAddress( targetAddress );
				if( reg.getType() == CoreRegisterType.RESERVED )
				{
					return InstructionStatus.RESERVED_IO;
				}	
			}

			if( targetAddress > cdesc.getSramSize() )
			{
				return InstructionStatus.SRAM_ADDRESS_OUT_OF_RANGE;
			}
		}

		else switch( instruction.getInstructionSetEntry() )
		{
			// ----------------------------------------------------------------
			// - IN / OUT -----------------------------------------------------
			// ----------------------------------------------------------------

			case IN:
			{
				IN in = (IN)instruction;
				int address = in.getAddress();

				CoreRegister reg = getIORegisterByAddress( address );
				if( reg.getType() == CoreRegisterType.RESERVED )
				{
					return InstructionStatus.RESERVED_IO;
				}
				
				break;
			}

			case OUT:
			{
				OUT out = (OUT)instruction;
				int address = out.getAddress();

				CoreRegister reg = getIORegisterByAddress( address );
				if( reg.getType() == CoreRegisterType.RESERVED )
				{
					return InstructionStatus.RESERVED_IO;
				}
				
				break;
			}

			// ----------------------------------------------------------------
			// - Others -------------------------------------------------------
			// ----------------------------------------------------------------

			default:
		}

		return InstructionStatus.OK;
	}

	// ========================================================================
	// = Utilities ============================================================
	// ========================================================================

	private static final Logger LOG
		= Logger.getLogger( Core.class.getName() );
}
