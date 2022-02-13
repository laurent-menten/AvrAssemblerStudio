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
import be.lmenten.avr.core.instruction.xbase.Instruction_Rr5;
import be.lmenten.avr.core.register.Register;
import be.lmenten.avr.core.register.RegisterIndexYZ;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "10q0 qq1r rrrr dqqq",
	statusRegister = "--------",
	syntax = "ST Y|Z, Rd // STD Y+q|Z+q, Rr",
	description = "Load indirect from data space using index"
)
public class STD
	extends Instruction_Rr5
{
	private RegisterIndexYZ rd;
	private int q;

	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public STD( RegisterIndexYZ rd, Register rr )
	{
		this( rd, 0, rr );
	}

	public STD( RegisterIndexYZ rd, int q, Register rr )
	{
		super( rr );

		this.rd = rd;
		this.q = q;

		int opc = InstructionSet.STD.getOpcodeMaskValue();
		opc = InstructionSet.STD.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		opc = InstructionSet.STD.insertOperand( OperandType.q, opc, q );
		opc = InstructionSet.STD.insertOperand( OperandType.r, opc, rr.getOperandIndex() );
		setOpcode( opc );
	}

	public static STD newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.STD.extractOperand( OperandType.d, opcode );
		int q = InstructionSet.STD.extractOperand( OperandType.q, opcode );
		int r = InstructionSet.STD.extractOperand( OperandType.r, opcode );

		return new STD( RegisterIndexYZ.lookup( d ), q, Register.lookup( r ) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.STD;
	}

	// ========================================================================
	// = toString() support ===================================================
	// ========================================================================

	@Override
	public String getMnemonic()
	{
		if( q != 0 )
		{
			return InstructionSet.ST.name();
		}

		return super.getMnemonic();
	}

	@Override
	public String getOperand1( Core core )
	{
		String text = rd.toString();
		if( q != 0 )
		{
			text = text + "+" + q;
		}

		return text;
	}

	@Override
	public String getOperand2( Core core )
	{
		return getRr().name();
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreRegister _rr = core.getRegister( this.getRr() );
		_rr.recordReadAccess( core, this );

		CoreRegister _rdl = core.getRegister( rd.getLowerRegister() );
		int rdl = _rdl.getData() & 0xFF;
		_rdl.recordReadAccess( core, this );
		CoreRegister _rdh = core.getRegister( rd.getUpperRegister() );
		int rdh = _rdh.getData() & 0xFF;
		_rdh.recordReadAccess( core, this );

		int address = ((rdh & 0xFF) << 8) | (rdl & 0xFF);

		CoreRegister _ramp = core.getRegister( rd.getExtendedRegister() );
		if( _ramp != null )
		{
			int ramp = _ramp.getData() & 0xFF;
			_ramp.recordReadAccess( core, this );

			address |= (ramp << 18);
		}

		address += q;

		// --------------------------------------------------------------------

		CoreData _z = core.getSramCell( address );
		_z.setData( _rr.getData() );
		_z.recordWriteAccess( core, this );

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
