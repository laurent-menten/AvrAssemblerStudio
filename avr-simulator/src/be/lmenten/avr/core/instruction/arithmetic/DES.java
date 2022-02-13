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

package be.lmenten.avr.core.instruction.arithmetic;

import static be.lmenten.avr.core.descriptor.CoreVersion.AVRxm;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.data.CoreStatusRegister;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_K4;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "1001 0100 KKKK 1011",
	coreVersionSpecific = { AVRxm },
	statusRegister = "--------",
	syntax = "DES K[0..15]",
	description = "DataInstruction Encryption Standard"
)
public class DES
	extends Instruction_K4
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public DES( int K )
	{
		super( K );

		int opc = InstructionSet.DES.getOpcodeMaskValue();
		opc = InstructionSet.DES.insertOperand( OperandType.K, opc, getRound() );
		setOpcode( opc );
	}

	public static DES newInstance( Integer opcode, Integer opcode2 )
	{
		int K = InstructionSet.DES.extractOperand( OperandType.K, opcode );

		return new DES( K );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.DES;
	}
	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreStatusRegister sreg = core.getStatusRegisterCopy();

		// --------------------------------------------------------------------

		CoreRegister [] register = new CoreRegister [16];
		byte [] data = new byte [8];
		byte [] key = new byte [8];

		for( int i = 0 ; i < 8 ; i++ )
		{
			register[i] = core.getRegisterByPhysicalAddress( i );
			data[i] = (byte) register[i].getData();
			register[i].recordReadAccess( core, this );
		}

		for( int i = 8 ; i < 16 ; i++ )
		{
			register[i] = core.getRegisterByPhysicalAddress( i );
			key[i - 8] = (byte) register[i].getData();
			register[i].recordReadAccess( core, this );			
		}

		// --------------------------------------------------------------------

		byte [] result;

		if( sreg.h() )
		{
			result = decrypt( getRound(), data, key );
		}
		else
		{
			result = encrypt( getRound(), data, key );
		}
		
		// --------------------------------------------------------------------

		for( int i = 0 ; i < 16 ; i++ )
		{
			register[i].setData( result[i] );
			register[i].recordWriteAccess( core, this );
		}

		// --------------------------------------------------------------------

		Instruction instr = core.getPreviouslyExecutedInstruction();
		if( (instr != null) && (instr instanceof DES) )
		{
			core.updateClockCyclesCounter( 1l );				
		}
		else
		{
			core.updateClockCyclesCounter( 2l );				
		}

		incrementExecutionsCount();
	}

	// ========================================================================
	// = ENCRYPTION ===========================================================
	// ========================================================================

	//FIXME implement proper DES encryption
	private byte [] encrypt( int round, byte [] data, byte [] key )
	{
		byte [] result = new byte [16];

		for( int i = 0 ; i < 8 ; i++ )
		{
			result[i] = (byte)(data[i] ^ key[i]);
			result[8+i] = (byte)(key[i] ^ data[i]);
		}

		return result;
	}

	// ========================================================================
	// = DECRYPTION ===========================================================
	// ========================================================================
	
	//FIXME implement proper DES decryption
	private byte [] decrypt( int round, byte [] data, byte [] key )
	{
		byte [] result = new byte [16];

		for( int i = 0 ; i < 8 ; i++ )
		{
			result[i] = (byte)(data[i] ^ key[i]);
			result[8+i] = (byte)(key[i] ^ data[i]);
		}

		return result;
	}
}
