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
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd5;
import be.lmenten.avr.core.register.Register;
import be.lmenten.avr.core.register.RegisterIndexYZ;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "10q0 qq0d dddd rqqq",
	statusRegister = "--------",
	syntax = "LD Rd, Y|Z // LDD Rd, Y+q|Z+q",
	description = "Load indirect from data space using index"
)
public class LDD
	extends Instruction_Rd5
{
	private RegisterIndexYZ rr;
	private int q;

	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public LDD( Register rd, RegisterIndexYZ rr )
	{
		this( rd, rr, 0 );
	}

	public LDD( Register rd, RegisterIndexYZ rr, int q )
	{
		super( rd );

		this.rr = rr;
		this.q = q;

		int opc = InstructionSet.LDD.getOpcodeMaskValue();
		opc = InstructionSet.LDD.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		opc = InstructionSet.LDD.insertOperand( OperandType.r, opc, rr.getOperandIndex() );
		opc = InstructionSet.LDD.insertOperand( OperandType.q, opc, q );
		setOpcode( opc );
	}

	public static LDD newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.LDD.extractOperand( OperandType.d, opcode );
		int r = InstructionSet.LDD.extractOperand( OperandType.r, opcode );
		int q = InstructionSet.LDD.extractOperand( OperandType.q, opcode );

		return new LDD( Register.lookup( d ), RegisterIndexYZ.lookup( r ), q );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.LDD;
	}

	// ========================================================================
	// = toString() support ===================================================
	// ========================================================================

	@Override
	public String getMnemonic()
	{
		if( q != 0 )
		{
			return InstructionSet.LD.name();
		}

		return super.getMnemonic();
	}

	@Override
	public String getOperand2( Core core )
	{
		String text = rr.toString();
		if( q != 0 )
		{
			text = text + "+" + q;
		}

		return text;
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreRegister _rd = core.getRegister( this.getRd() );

		CoreRegister _zl = core.getRegister( rr.getLowerRegister() );
		int zl = _zl.getData() & 0xFF;
		_zl.recordReadAccess( core, this );
		CoreRegister _zh = core.getRegister( rr.getUpperRegister() );
		int zh = _zh.getData() & 0xFF;
		_zh.recordReadAccess( core, this );

		int address = ((zh & 0xFF) << 8) | (zl & 0xFF);

		CoreRegister _ramp = core.getRegister( rr.getExtendedRegister() );
		if( _ramp != null )
		{
			int ramp = _ramp.getData() & 0xFF;
			_ramp.recordReadAccess( core, this );

			address |= (ramp << 18);
		}

		address += q;

		// --------------------------------------------------------------------

		CoreData _z = core.getSramCell( address );
		Value z = _z.getValue();
		_z.recordReadAccess( core, this );

		_rd.setValue( z );
		_rd.recordWriteAccess( core, this );

		// --------------------------------------------------------------------
		
		core.updateClockCyclesCounter( getCyclesCount( core, address ) );
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
			{
				if( address < core.getDescriptor().getSramBase() )
				{
					cycles = 2l;
				}
				else
				{
					cycles = 3l;
				}

				if( q == 0 )
				{
					cycles--;
				}
					
				break;
			}

			case AVRxt:
			{
				cycles = 2l;
				break;
			}

			case AVRrc:
			{
				cycles = 2l;	// FIXME doc says 1 / 2 ...
				break;
			}

			default:
		}

		return cycles;
	}
}
