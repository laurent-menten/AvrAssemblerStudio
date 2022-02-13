package be.lmenten.avr.core.descriptor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Runtime.Version;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import be.lmenten.avr.core.data.CoreRegisterType;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.utils.StringUtils;

//FIXME check Atmel documentation for coreFeatures of various MCUs 

/**
 * <p>
 * A descriptor for a specific MCU from the AVR families.
 * 
 * <p>
 * It serves the same purpose as the part specific include file used by AVRASM
 * and extends it to provide a full description of the part.
 * 
 * <ul>
 *  <li>A description of the core</li>
 * 	<li>The various coreFeatures supported.</li>
 * 	<li>The definition of all memories supported.</li>
 * 	<li>The definitions of every I/O registers and their bits</li>
 *		<li>The fuses bytes and lockbits definitions.</li>
 * 	<li>The interrupts names and vectors.</li>
 *  <li>Some internal informations</li>
 * </ul>
 * 
 *
 * @author Laurent Menten
 * @version 1.0, (4 Jun 2020)
 * @since 1.0
 */
public class CoreDescriptor
{
	// =========================================================================
	// === JSON KEYS ===========================================================
	// =========================================================================
	//
	// These keys are used internally for parsing the .json description file.
	//
	
	private static final String KEY_FILE = "file";

	private static final String KEY_VERSION = "version";
	private static final String KEY_DATE = "date";
	private static final String KEY_AUTHOR = "author";
	private static final String KEY_NOTES = "notes";

	private static final String KEY_CORE = "core";

	private static final String KEY_NAME = "name";
	private static final String KEY_CORE_VERSION = "core_version";
	private static final String KEY_MISSING_INSTRUCTIONS = "missing_instructions";
	private static final String KEY_SIGNATURE = "signature";

	private static final String KEY_FLASH = "flash";
	private static final String KEY_PC_WIDTH = "pc_width";
	private static final String KEY_VECTOR_SIZE = "vector_size";
	
	private static final String KEY_RWW = "rww";
	private static final String KEY_NRWW = "nrww";
	private static final String KEY_PAGE_SIZE = "page_size";
	private static final String KEY_BOOTSZ = "bootsz";

	private static final String KEY_SYMBOLS = "symbols";

	private static final String KEY_REGISTERS = "registers";
	private static final String KEY_IO = "io";
	private static final String KEY_EXTENDED_IO = "extended_io";
	private static final String KEY_SRAM = "sram";
	private static final String KEY_EXTERNAL_SRAM = "external_sram";

	private static final String KEY_EEPROM = "eeprom";
	private static final String KEY_EEADDR_WIDTH = "eeaddr_width";

	private static final String KEY_FEATURES = "features";

	private static final String KEY_FUSES = "fuses";
	private static final String KEY_LOCK_BITS = "lock_bits";

	private static final String KEY_INTERRUPTS = "interrupts";
	private static final String KEY_IO_MAP = "io_map";
	private static final String KEY_EXTENDED_IO_MAP = "extended_io_map";

	// =========================================================================
	// === DATA ================================================================
	// =========================================================================

	private final ResourceBundle res;

	// -------------------------------------------------------------------------

	private Version fileVersion;
	private String fileDate;
	private String fileAuthor;
	private String fileNotes;

	// -------------------------------------------------------------------------

	private String partName;
	private CoreVersionASM coreVersionASM;
	private CoreVersion coreVersion;
	private byte [] partSignature;

	private Set<InstructionSet> missingInstructions
		= EnumSet.noneOf( InstructionSet.class );
	
	private Set<CoreFeatures> coreFeatures
		= EnumSet.noneOf( CoreFeatures.class );

	// -------------------------------------------------------------------------
	// - FLASH memory ----------------------------------------------------------
	// -------------------------------------------------------------------------

	private HashMap<String,CoreMemoryRange>  flashMap
		= new HashMap<>();

	private CoreMemoryRange flash;

	private CoreMemoryRange rww;					// feature: BLS
	private CoreMemoryRange nrww;					// feature: BLS
	private CoreMemoryRange bootsz [];				// feature: BLS

	private int pc_width;
	private int vector_size;
	private int page_size;

	// -------------------------------------------------------------------------

	private HashMap<Integer,CoreInterruptDescriptor> interruptsByVector
		= new HashMap<>();

	private HashMap<String,CoreInterruptDescriptor> interruptsByName
		= new HashMap<>();

	// -------------------------------------------------------------------------
	// - STATIC-RAM memory -----------------------------------------------------
	// -------------------------------------------------------------------------

	public boolean hasSram;

	private HashMap<String,CoreMemoryRange> sramMap
		= new HashMap<>();
	
	private CoreMemoryRange registers;
	private CoreMemoryRange io;	
	private CoreMemoryRange extended_io;	
	private CoreMemoryRange sram;	
	private CoreMemoryRange external_sram;		// feature: external memory

	// -------------------------------------------------------------------------

	private HashMap<Integer,CoreRegisterDescriptor> registersByAddress
		= new HashMap<>();

	private HashMap<String,CoreRegisterDescriptor> registersByName
		= new HashMap<>();

	// -------------------------------------------------------------------------

	private HashMap<Integer,CoreRegisterDescriptor> ioRegistersByAddress
		= new HashMap<>();

	private HashMap<String,CoreRegisterDescriptor> ioRegistersByName
		= new HashMap<>();

	// -------------------------------------------------------------------------

	private HashMap<Integer,CoreRegisterDescriptor> extIoRegistersByAddress
		= new HashMap<>();

	private HashMap<String,CoreRegisterDescriptor> extIoRegistersByName
		= new HashMap<>();

	// -------------------------------------------------------------------------
	// - EEPROM memory ---------------------------------------------------------
	// -------------------------------------------------------------------------

	private boolean hasEeprom;

	private HashMap<String,CoreMemoryRange> eepromMap
		= new HashMap<>();

	private CoreMemoryRange eeprom;	
	private int eeaddr_width;

	// -------------------------------------------------------------------------
	// - FUSES & LOCKBTS -------------------------------------------------------
	// -------------------------------------------------------------------------

	private HashMap<String,CoreRegisterDescriptor> lockBits
		= new HashMap<>();

	private HashMap<String,CoreRegisterDescriptor> fuses
		= new HashMap<>();

	// -------------------------------------------------------------------------
	// - SYMBOLS ---------------------------------------------------------------
	// -------------------------------------------------------------------------

	private LinkedHashMap<String,CoreSymbolDefinition> symbols
		= new LinkedHashMap<>();

	// -------------------------------------------------------------------------
	// -INTERNAL & UTILITIES ---------------------------------------------------
	// -------------------------------------------------------------------------

	private int maxRegisterNameLength = 0;
	private int maxBitNameLength = 0;

	// =========================================================================
	// === CONSTRUCTOR(s) ======================================================
	// =========================================================================

	/**
	 * 
	 * @param coreId
	 */
	/*package*/ CoreDescriptor( AvrDevice coreId )
	{
		res = coreId.getDescriptorResourceBundle();
	
		// ----------------------------------------------------------------------

		JSONParser parser = new JSONParser();

		InputStream is = coreId.getDescriptorDataAsStream() ;
		InputStreamReader isr = new InputStreamReader( is );
		try( BufferedReader reader = new BufferedReader( isr ) )
		{
			JSONObject jsonRoot = (JSONObject) parser.parse( reader );

			// -------------------------------------------------------------------

			JSONObject jsonFile = (JSONObject) jsonRoot.get( KEY_FILE );
			parseFileObject( jsonFile );

			// -------------------------------------------------------------------

			JSONObject jsonCore = (JSONObject) jsonRoot.get( KEY_CORE );
			parseCoreObject( jsonCore );

			// -------------------------------------------------------------------

			JSONArray jsonSymbols = (JSONArray) jsonRoot.get( KEY_SYMBOLS );
			parseSymbolsArray( jsonSymbols );

			// -------------------------------------------------------------------

			JSONArray jsonFuses = (JSONArray) jsonRoot.get( KEY_FUSES );
			parseNamedBytesArray( CoreRegisterType.FUSE, jsonFuses, fuses );

			JSONArray jsonLockBits = (JSONArray) jsonRoot.get( KEY_LOCK_BITS );
			parseNamedBytesArray( CoreRegisterType.LOCK_BIT, jsonLockBits, lockBits );

			// -------------------------------------------------------------------

			JSONArray jsonInterrupts = (JSONArray) jsonRoot.get( KEY_INTERRUPTS );
			parseInterruptsArray( jsonInterrupts, interruptsByName );
			interruptsByName.forEach( (key, value)
				-> interruptsByVector.put( value.getVector(), value )
			);

			// -------------------------------------------------------------------

			// R0..R31
			for( int i = 0 ; i < registers.getRangeSize() ; i++ )
			{
				int address = 0x0000 + i;
				String name = "R" + i;

				CoreRegisterDescriptor desc
					= new CoreRegisterDescriptor( this, CoreRegisterType.GENERAL_PURPOSE, address, name );
				registersByAddress.put( address, desc );
				registersByName.put( name, desc );
			}

			// I/O
			JSONArray jsonIo = (JSONArray) jsonRoot.get( KEY_IO_MAP );
			parseIORegistersArray( CoreRegisterType.IO, jsonIo, ioRegistersByAddress );
			ioRegistersByAddress.forEach( (key, value) ->
				{
					if( registersByName.containsKey( value.getName() ) )
					{
						// FIXME name confict R0..R31
					}

					ioRegistersByName.put( value.getName(), value );
				}
			);

			// Extended I/O
			JSONArray jsonExtIo = (JSONArray) jsonRoot.get( KEY_EXTENDED_IO_MAP );
			parseIORegistersArray( CoreRegisterType.EXTENDED_IO, jsonExtIo, extIoRegistersByAddress );
			extIoRegistersByAddress.forEach( (key, value) ->
				{
					if( registersByName.containsKey( value.getName() ) )
					{
						// FIXME name confict R0..R31
					}

					if( ioRegistersByName.containsKey( value.getName() ) )
					{
						// FIXME duplicate name
					}

					extIoRegistersByName.put( value.getName(), value );
				}
			);
		}

		catch( ParseException e )
		{
			e.printStackTrace();
		}

		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	// ========================================================================
	// ===
	// ========================================================================

	private void parseFileObject( JSONObject o )
	{
		String tmp;

		tmp = (String) o.get( KEY_VERSION );
		fileVersion = Version.parse( tmp );

		fileDate = (String) o.get( KEY_DATE );

		fileAuthor = (String) o.get( KEY_AUTHOR );

		fileNotes = (String) o.get( KEY_NOTES );
	}


	// ========================================================================
	// ===
	// ========================================================================

	private void parseCoreObject( JSONObject o )
	{
		String tmp;

		// ----------------------------------------------------------------------
		// - Core identity ------------------------------------------------------
		// ----------------------------------------------------------------------

		partName = (String) o.get( KEY_NAME );

		tmp = (String) o.get( KEY_VERSION );
		coreVersionASM = CoreVersionASM.valueOf( tmp );

		tmp = (String) o.get( KEY_CORE_VERSION );
		coreVersion = CoreVersion.lookup( tmp );

		JSONArray jsonMissingInstructions = (JSONArray) o.get( KEY_MISSING_INSTRUCTIONS );
		for( int i = 0 ; i < jsonMissingInstructions.size() ; i ++ )
		{
			tmp = (String) jsonMissingInstructions.get( i );
			missingInstructions.add( InstructionSet.valueOf( tmp ) );
		}

		JSONArray jsonSignature = (JSONArray) o.get( KEY_SIGNATURE );
		partSignature = new byte [ jsonSignature.size() ];
		for( int i = 0 ; i < jsonSignature.size() ; i ++ )
		{
			tmp = (String) jsonSignature.get( i );
			partSignature[i] = (byte) StringUtils.parseNumber( tmp );
		}

		// ----------------------------------------------------------------------
		// - Features -----------------------------------------------------------
		// ----------------------------------------------------------------------

		JSONArray jsonFeatures = (JSONArray) o.get( KEY_FEATURES );
		for( int i = 0 ; i < jsonFeatures.size() ; i ++ )
		{
			tmp = (String) jsonFeatures.get( i );
			coreFeatures.add( CoreFeatures.valueOf( tmp ) );
		}

		// ----------------------------------------------------------------------
		// - Flash memory -------------------------------------------------------
		// ----------------------------------------------------------------------

		JSONObject o1 = (JSONObject) o.get( KEY_FLASH ); 
		flash = new CoreMemoryRange( CoreMemory.FLASH, KEY_FLASH, o1 );
		flashMap.put( flash.getRangeName(), flash );
		
		tmp = (String) o.get( KEY_PC_WIDTH );
		pc_width = (int) StringUtils.parseNumber( tmp );
		
		tmp = (String) o.get( KEY_VECTOR_SIZE );
		vector_size = (int) StringUtils.parseNumber( tmp );

		if( hasFeature( CoreFeatures.BOOT_LOADER ) )
		{
			o1 = (JSONObject) o.get( KEY_RWW );
			rww = new CoreMemoryRange( CoreMemory.FLASH, KEY_RWW, o1 );
			flashMap.put( rww.getRangeName(), rww );

			o1 = (JSONObject) o.get( KEY_NRWW );
			nrww = new CoreMemoryRange( CoreMemory.FLASH, KEY_NRWW, o1 );
			flashMap.put( nrww.getRangeName(), nrww );

			tmp = (String) o.get( KEY_PAGE_SIZE );
			page_size = (int) StringUtils.parseNumber( tmp );

			JSONArray jsonBootsz =(JSONArray) o.get( KEY_BOOTSZ );
			bootsz = new CoreMemoryRange [ jsonBootsz.size() ];
			for( int i = 0 ; i < jsonBootsz.size() ; i ++ )
			{
				o1 = (JSONObject) jsonBootsz.get( i );
				bootsz[i] = new CoreMemoryRange( CoreMemory.FLASH, "BLS_"+i, o1 );
				flashMap.put( bootsz[i].getRangeName(), bootsz[i] );

				//FIXME add application ranges
			}
		}

		// ----------------------------------------------------------------------
		// - Static RAM ---------------------------------------------------------
		// ----------------------------------------------------------------------

		o1 = (JSONObject) o.get( KEY_REGISTERS );
		registers = new CoreMemoryRange( CoreMemory.SRAM, CoreMemoryRange.REGISTERS_RANGE_NAME, o1 );
		sramMap.put( registers.getRangeName(), registers );

		o1 = (JSONObject) o.get( KEY_IO );
		io = new CoreMemoryRange( CoreMemory.SRAM, CoreMemoryRange.IO_RANGE_NAME, o1 );
		sramMap.put( io.getRangeName(), io );

		o1 = (JSONObject) o.get( KEY_EXTENDED_IO );
		extended_io = new CoreMemoryRange( CoreMemory.SRAM, CoreMemoryRange.EXTENDED_IO_RANGE_NAME, o1 );
		sramMap.put( extended_io.getRangeName(), extended_io );

		o1 = (JSONObject) o.get( KEY_SRAM );
		sram = new CoreMemoryRange( CoreMemory.SRAM, CoreMemoryRange.SRAM_RANGE_NAME, o1 );
		sramMap.put( sram.getRangeName(), sram );

		if( hasFeature( CoreFeatures.EXTERNAL_SRAM ) )
		{
			o1 = (JSONObject) o.get( KEY_EXTERNAL_SRAM );
			external_sram = new CoreMemoryRange( CoreMemory.SRAM, CoreMemoryRange.EXTENDED_SRAM_RANGE_NAME, o1 );
			sramMap.put( external_sram.getRangeName(),  external_sram );
		}

		// ----------------------------------------------------------------------
		// - Eeprom -------------------------------------------------------------
		// ----------------------------------------------------------------------

		o1 = (JSONObject) o.get( KEY_EEPROM );
		hasEeprom = o1 != null;

		if( hasEeprom )
		{
			eeprom = new CoreMemoryRange( CoreMemory.EEPROM, KEY_EEPROM, o1 );
			eepromMap.put( eeprom.getRangeName(), eeprom );

			tmp = (String) o.get( KEY_EEADDR_WIDTH );
			eeaddr_width = (int) StringUtils.parseNumber( tmp );
		}
	}

	// ========================================================================
	// ===
	// ========================================================================

	private void parseSymbolsArray( JSONArray o )
	{
		for( int i = 0 ; i < o.size() ; i++ )
		{
        	JSONObject o1 = (JSONObject) o.get( i );
        	CoreSymbolDefinition d = new CoreSymbolDefinition( o1 );

        	if( d.getSymbol() == null )
        	{
        		throw new RuntimeException( "symbol " + o1 + " has no name" );
        	}

        	if( symbols.containsKey( d.getSymbol() ) )
        	{
        		LOG.warning( "Redifinition of symbol " + d.getSymbol() );
        	}

        	symbols.put( d.getSymbol(), d );
		}
	}

	// ========================================================================
	// ===
	// ========================================================================

	private void parseNamedBytesArray( CoreRegisterType type, JSONArray o, Map<String,CoreRegisterDescriptor> map )
	{
        for( int i = 0 ; i < o.size() ; i++ )
        {
        	JSONObject o1 = (JSONObject) o.get( i );
        	CoreRegisterDescriptor d = new CoreRegisterDescriptor( this, type, o1, res );

        	if( ! d.hasName() )
        	{
        		throw new RuntimeException( "register " + o1 + " has no name" );
        	}

        	map.put( d.getName(), d );
        }
	}

	// ========================================================================
	// ===
	// ========================================================================

	private void parseInterruptsArray( JSONArray o, Map<String,CoreInterruptDescriptor> map )
	{
        for( int i = 0 ; i < o.size() ; i++ )
        {
        	JSONObject o1 = (JSONObject) o.get( i );
        	CoreInterruptDescriptor d = new CoreInterruptDescriptor( o1 );

        	if( ! d.hasName() )
        	{
        		throw new RuntimeException( "interrupt " + o1 + " has no partName" );
        	}

        	map.put( d.getName(), d );
        }
	}

	// ========================================================================
	// ===
	// ========================================================================

	private void parseIORegistersArray( CoreRegisterType type, JSONArray o, Map<Integer,CoreRegisterDescriptor> map )
	{
        for( int i = 0 ; i < o.size() ; i++ )
        {
        	JSONObject o1 = (JSONObject) o.get( i );
        	CoreRegisterDescriptor d = new CoreRegisterDescriptor( this, type, o1, res );

        	if( ! d.hasAddress() )
        	{
        		throw new RuntimeException( "register " + o1 + " has no address" );
        	}

        	map.put( d.getAddress(), d );

        	// ----------------------------------------------------------------

        	if( d.getName().length() > maxRegisterNameLength )
        	{
        		maxRegisterNameLength = d.getName().length();
        	}

        	if( d.hasBitNames() )
        	{
        		for( int bit = 0 ; bit < 8 ; bit++ )
        		{
        			if( d.getBitName(bit) != null )
        			{
        				if( d.getBitName(bit).length() > maxBitNameLength )
        				{
        					maxBitNameLength = d.getBitName(bit).length();
        				}
        			}
        		}
        	}
      }
	}

	// ========================================================================
	// === GETTERS = DESCRIPTION FILE =========================================
	// ========================================================================

	/**
	 * Get version of the device description file.
	 * 
	 * @return the description file version
	 */
	public Version getFileVersion()
	{
		return fileVersion;
	}

	/**
	 * Get date of the device description file.
	 * 
	 * @return
	 */
	public String getFileDate()
	{
		return fileDate;
	}

	/**
	 * Get author of the device description file.
	 * 
	 * @return
	 */
	public String getFileAuthor()
	{
		return fileAuthor;
	}

	/**
	 * Get notes of the device description file.
	 * 
	 * @return
	 */
	public String getFileNotes()
	{
		return fileNotes;
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the length of the longest register name.
	 * 
	 * @return
	 */
	public int getMaxRegisterNameLength()
	{
		return maxRegisterNameLength;
	}

	/**
	 * Get the length of the longest register's bit name.
	 * 
	 * @return
	 */
	public int getMaxBitNameLength()
	{
		return maxBitNameLength;
	}

	// =========================================================================
	// === GETTERS = DEVICE ====================================================
	// =========================================================================

	/**
	 * 
	 * @return
	 */
	public String getPartName()
	{
		return partName;
	}

	/**
	 * 
	 * @return
	 */
	public CoreVersionASM getCoreVersionASM()
	{
		return coreVersionASM;
	}

	/**
	 * 
	 * @return
	 */
	public CoreVersion getCoreVersion()
	{
		return coreVersion;
	}

	/**
	 * 
	 * @return
	 */
	public InstructionSet [] getMissingInstructions()
	{
		return missingInstructions.toArray(  new InstructionSet [0] );
	}

	/**
	 * 
	 * @param instruction
	 * @return
	 */
	public boolean isInstructionMissing( InstructionSet instruction )
	{
		return missingInstructions.contains( instruction );
	}

	/**
	 * 
	 * @param instruction
	 * @return
	 */
	public boolean hasInstruction( InstructionSet instruction )
	{
		return ! missingInstructions.contains( instruction );
	}

	/**
	 * 
	 * @return
	 */
	public byte [] getPartSignature()
	{
		return partSignature;
	}

	// -------------------------------------------------------------------------

	/**
	 * 
	 * @return
	 */
	public CoreFeatures [] getFeatures()
	{
		return coreFeatures.toArray( new CoreFeatures [0] );
	}

	/**
	 * 
	 * @param feature
	 * @return
	 */
	public boolean hasFeature( CoreFeatures feature )
	{
		return coreFeatures.contains( feature );
	}

	// =========================================================================
	// === SYMBOLS =============================================================
	// =========================================================================

	/**
	 * 
	 * @return
	 */
	public int getSymbolsCount()
	{
		return symbols.size();
	}

	/**
	 * 
	 * @param c
	 */
	public void exportSymbols( BiConsumer<String,CoreSymbolDefinition> c )
	{
		for( Map.Entry<String,CoreSymbolDefinition> s : symbols.entrySet() )
		{
			c.accept( s.getKey(), s.getValue() );
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 
	 * @param symbol
	 * @return
	 */
	public CoreSymbolDefinition getSymbolDefinition( String symbol )
	{
		return symbols.get( symbol );
	}

	// =========================================================================
	// === FUSES / LOCKBITS ====================================================
	// =========================================================================

	/**
	 * Get the number of fuses bytes of this MCU.
	 * 
	 * @return
	 */
	public int getFusesCount()
	{
		return fuses.size();
	}

	/**
	 * 
	 * @param c
	 */
	public void exportFuses( BiConsumer<Integer,CoreRegisterDescriptor> c )
	{
		int i = 0;
		for( Map.Entry<String,CoreRegisterDescriptor> f : fuses.entrySet() )
		{
			c.accept( i++, f.getValue() );
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the number of lockbits bytes of this MCU.
	 * 
	 * @return
	 */
	public int getLockBitsCount()
	{
		return lockBits.size();
	}

	/**
	 * 
	 * @param c
	 */
	public void exportLockBits( BiConsumer<Integer,CoreRegisterDescriptor> c )
	{
		int i = 0;
		for( Map.Entry<String,CoreRegisterDescriptor> f : lockBits.entrySet() )
		{
			c.accept( i++, f.getValue() );
		}
	}

	// ========================================================================
	// === 
	// ========================================================================

	/**
	 * 
	 * @param memory
	 * @return
	 */
	public Collection<CoreMemoryRange> getMemoryRanges( CoreMemory memory )
	{
		switch( memory )
		{
			case FLASH:
				return flashMap.values();

			case SRAM:
				return sramMap.values();

			case EEPROM:
				return eepromMap.values();
		}

		return null;
	}

	/**
	 * 
	 * @param memory
	 * @param name
	 * @return
	 */
	public CoreMemoryRange getMemoryRange( CoreMemory memory, String name )
	{
		switch( memory )
		{
			case FLASH:
				return flashMap.get( name );

			case SRAM:
				return sramMap.get( name );

			case EEPROM:
				return eepromMap.get( name );
		}

		return null;
	}

	// ========================================================================
	// === FLASH MEMORY =======================================================0
	// ========================================================================

	/**
	 * Get the size of the flash memory in bytes.
	 * 
	 * @return Size of flash memory in bytes.
	 */
	public int getFlashSize()
	{
		return flash.getRangeSize();
	}

	/**
	 * Get the number of bits of this MCU's program counter.
	 * 
	 * @return
	 */
	public int getProgramCounterWidth()
	{
		return pc_width;
	}

	/**
	 * Get the size of a single interrupt vector in bytes.
	 * 
	 * @return
	 */
	public int getInterruptVectorSize()
	{
		return vector_size * 2;
	}


	public String getInterruptName( int vector )
	{
		CoreInterruptDescriptor d = interruptsByVector.get( vector );
		if( d != null )
		{
			return d.getName();
		}

		return null;
	}

	public int getInterruptVector( String name )
	{
		// FIXME do ignore case
		CoreInterruptDescriptor d = interruptsByName.get( name );
		if( d != null )
		{
			return d.getVector();
		}

		return -1;
	}

	/**
	 * Get the number of interrupt vectors supported by this MCU.
	 * 
	 * @return
	 */
	public int getInterruptsCount()
	{
		return interruptsByName.size();
	}

	public void exportInterrupts( BiConsumer<String,CoreInterruptDescriptor> c )
	{
		for( Map.Entry<String,CoreInterruptDescriptor> i : interruptsByName.entrySet() )
		{
			c.accept( i.getKey(), i.getValue() );
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * 
	 * @return
	 */
	public CoreMemoryRange getRwwRange()
	{
		return rww;
	}

	/**
	 * 
	 * @return
	 */
	public CoreMemoryRange getNRwwRane()
	{
		return nrww;
	}

	/**
	 * 
	 * @return
	 */
	public int getPageSize()
	{
		return page_size;
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the size in bytes of the boot loader section according to the BOOTSZ
	 * fuse value.
	 * 
	 * @param bootsz
	 * @return
	 */
	public CoreMemoryRange getBootLoaderSection( int bootsz )
	{
		return this.bootsz[ bootsz ];
	}

	// ========================================================================
	// === STATIC RAM =========================================================
	// ========================================================================

	/**
	 * 
	 * @return
	 */
	public int getOnChipSramSize()
	{
		return getRegistersCount()
				+ getIoRegistersCount()
				+ getExtendedIoRegistersCount()
				+ getSramSize()
				;
	}

	// ------------------------------------------------------------------------

	public int getRegistersBase()
	{
		return 0x0000;
	}

	public int getRegistersCount()
	{
		return registers.getRangeSize();
	}

	public void exportRegisters( BiConsumer<Integer,CoreRegisterDescriptor> c )
	{
		for( Map.Entry<Integer,CoreRegisterDescriptor> r : registersByAddress.entrySet() )
		{
			c.accept( r.getKey(), r.getValue() );
		}
	}

	// ------------------------------------------------------------------------

	public int getIoRegistersBase()
	{
		return 0x0000
				+ getRegistersCount()
			;
	}

	public int getIoRegistersCount()
	{
		return io.getRangeSize();
	}

	// ------------------------------------------------------------------------

	public int getExtendedIoRegistersBase()
	{
		return 0x0000
				+ getRegistersCount()
				+ getIoRegistersCount()
			;
	}

	public int getExtendedIoRegistersCount()
	{
		return extended_io.getRangeSize();
	}

	// ------------------------------------------------------------------------

	/**
	 * 
	 * @return
	 */
	public int getSramBase()
	{
		return sram.getRangeBase();
	}

	/**
	 * 
	 * @return
	 */
	public int getSramSize()
	{
		return sram.getRangeSize();
	}

	/**
	 * 
	 * @return
	 */
	public int getSramLimit()
	{
		return sram.getRangeLimit();
	}

	// ------------------------------------------------------------------------

	/**
	 * 
	 * @return
	 */
	public int getExternalSramBase()
	{
		return external_sram.getRangeBase();
	}

	/**
	 * 
	 * @return
	 */
	public int getMaxExternalSramSize()
	{
		return external_sram.getRangeSize();
	}

	/**
	 * 
	 * @return
	 */
	public int getMaxExternalSramLimit()
	{
		return external_sram.getRangeLimit();
	}

	// ========================================================================
	// === EEPROM =============================================================
	// ========================================================================

	/**
	 * Check if this core supports internal Eeprom
	 * 
	 * @return
	 */
	public boolean hasEeprom()
	{
		return hasEeprom;
	}

	/**
	 * Get the width of the EEPROM addresses.
	 * 
	 * @return
	 */
	public int getEepromAddressWidth()
	{
		return eeaddr_width;
	}

	// ------------------------------------------------------------------------

	/**
	 * 
	 * @return
	 */
	public int getEepromBase()
	{
		return eeprom.getRangeBase();
	}

	/**
	 * Get the size of the EEPROM in bytes.
	 * 
	 * @return
	 */
	public int getEepromSize()
	{
		return eeprom.getRangeSize();
	}

	/**
	 * 
	 * @return
	 */
	public int getEepromLimit()
	{
		return eeprom.getRangeLimit();
	}

	// ========================================================================
	// ===
	// ========================================================================

	public CoreRegisterDescriptor getRegisterDescriptor( String name )
	{
		if( registersByName.containsKey( name ) )
		{
			return registersByName.get( name );
		}

		if( ioRegistersByName.containsKey( name ) )
		{
			return ioRegistersByName.get( name );
		}

		if( extIoRegistersByName.containsKey( name ) )
		{
			return extIoRegistersByName.get( name );
		}
		
		return null;
	}
	
	public CoreRegisterDescriptor getRegisterDescriptor( int address )
	{
		if( registersByAddress.containsKey( address ) )
		{
			return registersByAddress.get( address );
		}

		if( ioRegistersByAddress.containsKey( address ) )
		{
			return ioRegistersByAddress.get( address );
		}

		if( extIoRegistersByAddress.containsKey( address ) )
		{
			return extIoRegistersByAddress.get( address );
		}
		
		return null;
	}

	// ------------------------------------------------------------------------

	public CoreRegisterDescriptor getFuseDescriptor( String name )
	{
		return fuses.get( name );
	}

	public CoreRegisterDescriptor getLockBitsDescriptor( String name )
	{
		return lockBits.get( name );
	}

	// ========================================================================
	// === LOGGING ============================================================
	// ========================================================================

	private static final Logger LOG
		= Logger.getLogger( CoreDescriptor.class.getName() );
}
