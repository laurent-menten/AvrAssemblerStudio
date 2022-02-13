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

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.logging.Logger;

import be.lmenten.avr.binfmt.hex.IntelHexReader;
import be.lmenten.avr.core.descriptor.CoreDescriptor;
import be.lmenten.avr.core.instruction.DataInstruction;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.InstructionStatus;
import be.lmenten.avr.core.instruction.flow.JMP;
import be.lmenten.avr.core.instruction.transfer.CLR;
import be.lmenten.avr.core.instruction.transfer.LDI;
import be.lmenten.avr.core.instruction.transfer.OUT;
import be.lmenten.avr.core.register.Register;
import be.lmenten.avr.core.register.UpperRegister;

/**
 * CoreBaseImpl is a default implentation of the core. It deals with
 * loading program and data to the core.
 * 
 * 
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 08 / 23
 */
/*package*/ class CoreBaseImpl
	extends CoreBase
{
	protected CoreBaseImpl( CoreDescriptor cdesc )
	{
		super( cdesc );
	}

	public CoreBaseImpl( CoreDescriptor cdesc, Properties config )
	{
		super( cdesc, config );
	}

	// =========================================================================
	// === Loaders =============================================================
	// =========================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void installFakeBootLoader()
	{
		LOG.info( "Installing fake bootloader" );

		int resetAddress = 0x00000;
		LOG.fine( " > Reset vector address = " + String.format( "0x%06X", resetAddress ) );

		int blsAddress = getBootLoaderSectionBase();
		LOG.fine( " > Base address = " + String.format( "0x%06X", blsAddress ) );

		// --------------------------------------------------------------------
		// - Bootloader code --------------------------------------------------
		// --------------------------------------------------------------------

		int sp = cdesc.getOnChipSramSize() - 1;

		Instruction [] bootloader =
		{
/*
			new LDI( UpperRegister.R16, 0x10 ),
			new LDI( UpperRegister.R17, 0xFE ),
			new MUL( Register.R16, Register.R17 ),
			new MULS( UpperRegister.R16, UpperRegister.R17 ),
			new MULSU( LowUpperRegister.R16, LowUpperRegister.R17 ),

			new LDI( UpperRegister.R16, 0xFE ),
			new LDI( UpperRegister.R17, 0x10 ),
			new MUL( Register.R16, Register.R17 ),
			new MULS( UpperRegister.R16, UpperRegister.R17 ),
			new MULSU( LowUpperRegister.R16, LowUpperRegister.R17 ),
*/
			// Clear status register
			new CLR( Register.R16 ),
			new OUT( 0x3F, Register.R16 ),

			// Set stack pointer to end of on-chip ram
			new LDI( UpperRegister.R16, sp & 0xFF ),
			new OUT( 0x3D, Register.R16 ),
			new LDI( UpperRegister.R16, (sp >> 8) & 0xFF ),
			new OUT( 0x3E, Register.R16 ),

			// Clear used register
			new CLR( Register.R16 ),

			// Jump to application reset vector
			new JMP( resetAddress )
		};

		// --------------------------------------------------------------------
		// - Install ----------------------------------------------------------
		// --------------------------------------------------------------------

		int address = blsAddress;
		for( Instruction instr : bootloader )
		{
			instr.setCellAddress( address );
			checkInstruction( instr );

			if( flash[(address / 2)] != null )
			{
				flashMemoryOverwritting( blsAddress );
			}

			flash[(address / 2) + 0] = instr;
			address += 2;

			if( instr.getInstructionSetEntry().is32bits() )
			{
				Instruction instr2 = instr.getSecondWord();
				instr2.setCellAddress( address );

				flash[(address / 2) + 1] = instr2;
				address += 2;
			}	
		}

		LOG.info( "Loaded " + (address-blsAddress) + " bytes" );
	}

	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadFlash( File file )
		throws IOException
	{
		LOG.info( "Loading " + file.getPath() );

		try( IntelHexReader r = new IntelHexReader( file ) )
		{
			int loadedSize = 0;
			int minAddress = 0;
			int maxAddress = 0;
			
			while( ! r.eof() )
			{
				int address = r.getAddress();
				if( address < minAddress )
				{
					minAddress = address;
				}
				else if( address > maxAddress )
				{
					maxAddress = address;
				}

				int opcode = (r.readWord() & 0xFFFF);
				int opcode2 = 0x0000;

				InstructionSet entry = InstructionSet.lookup( opcode );
				if( entry != null )
				{
					if(  instructionCheckEnabled && !supportsInstruction( entry ) )
					{
						unsupportedInstruction( entry );
					}
					
					// --------------------------------------------------------

					if( entry.is32bits() )
					{
						opcode2 = (r.readWord() & 0xFFFF);
					}

					Instruction instruction = entry.newInstance( opcode, opcode2 );
					instruction.setCellAddress( address );

					// --------------------------------------------------------

					if( instruction.getOpcode() != opcode )
					{
						invalidOpcode( instruction, opcode );
					}

					if( flash[(address/2)] != null )
					{
						flashMemoryOverwritting( address );
					}

					flash[(address/2)] = instruction;
					loadedSize += 2;

					// --------------------------------------------------------

					if( entry.is32bits() )
					{
						Instruction instruction2 = instruction.getSecondWord();
						instruction2.setCellAddress( address + 2 );

						if( instruction2.getOpcode() != opcode2 )
						{
							invalidOpcode2( instruction, opcode2 );
						}
						
						flash[(address/2) + 1] = instruction2;
						loadedSize += 2;
					}

					InstructionStatus status = checkInstruction( instruction );
					instruction.setStatus( status );

					automaticSymbol( instruction );
				}

				else
				{
					DataInstruction data = new DataInstruction( opcode );
					data.setCellAddress( address );

					if( data.getOpcode() != opcode )
					{
						invalidData( data, opcode );
					}
					
					flash[(address/2)] = data;
					loadedSize += 2;
				}
			}

			LOG.fine( " > Lowest load address = " + String.format( "0x%06X", minAddress ) );
			LOG.fine( " > Highest load address = " + String.format( "0x%06X", maxAddress ) );
			LOG.info( "Loaded " + loadedSize + " bytes" );
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Here is an example of usage:
	 * 
	 * <pre>
	 * Consumer &lt;Instruction[]&gt; programLoader = new Consumer&lt;Instruction[]&gt;()
	 * {
	 *    public void accept( Instruction[] sram )
	 *    {
	 *       int pc = 0;
	 *       
	 *       sram[pc++] = new NOP();
	 *       sram[pc++] = new LDI( UpperRegister.R16, 1 );
	 *       sram[pc++] = new LDI( UpperRegister.R17, 1 );
	 *       sram[pc++] = new ADD( Register.R16, Register.R17 );
	 *    }
	 * };
	 * 
	 * core.loadProgram( programLoader );
	 * </pre>
	 */
	@Override
	public void loadProgram( Consumer<Instruction []> loader )
	{
		loader.accept( flash );
	}

	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadSram( File file )
		throws IOException
	{
		try( IntelHexReader r = new IntelHexReader( file ) )
		{
			while( ! r.eof() )
			{
				int address = (int) r.getAddress();
				int value = r.readByte();

				getSramCell( address ).silentSetData( value );
			}
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadEeprom( File file )
		throws IOException
	{
		try( IntelHexReader r = new IntelHexReader( file ) )
		{
			while( ! r.eof() )
			{
				int address = (int) r.getAddress();
				int value = r.readByte();

				eepromDriver.getMemoryCell( address ).silentSetData( value );
			}
		}
	}

	// ========================================================================
	// = Utilities ============================================================
	// ========================================================================

	private static final Logger LOG
		= Logger.getLogger( CoreBaseImpl.class.getName() );
}
