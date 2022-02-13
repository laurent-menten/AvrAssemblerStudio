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
import be.lmenten.avr.core.register.RegisterIndexXYZ;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "1001 001r rrrr dddd",
	statusRegister = "--------",
	isAlias = true,
	alias = "ST X|X+|-X | Y+|-Y | Z+|-Z, Rd",
	syntax = "ST X|X+|-X | Y+|-Y | Z+|-Z, Rd",
	description = "Store indirect to data space using index"
)
public class ST
	extends Instruction_Rr5
{
	private RegisterIndexXYZ rd;

	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public ST( RegisterIndexXYZ rd, Register rr )
	{
		super( rr );

		this.rd = rd;

		int opc = InstructionSet.ST.getOpcodeMaskValue();
		opc = InstructionSet.ST.insertOperand( OperandType.r, opc, rr.getOperandIndex() );
		opc = InstructionSet.ST.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		setOpcode( opc );
	}

	public static ST newInstance( Integer opcode, Integer opcode2 )
	{
		int r = InstructionSet.ST.extractOperand( OperandType.r, opcode );
		int d = InstructionSet.ST.extractOperand( OperandType.d, opcode );

		return new ST( RegisterIndexXYZ.lookup( d ), Register.lookup( r ) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.ST;
	}

	// ========================================================================
	// = toString() support ===================================================
	// ========================================================================

	@Override
	public String getSyntax()
	{
		return getMnemonic() + " " + rd + ", Rr";
	}

	@Override
	public String getOperand1( Core core )
	{
		return rd.toString();
	}

	@Override
	public String getOperand2(Core core)
	{
		return getRr().name();
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreRegister rr = core.getRegister( this.getRr() );
		rr.recordReadAccess( core, this );

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

		// --------------------------------------------------------------------

		if( rd.isPreDecrement() )
		{
			address--;
		}

		CoreData _z = core.getSramCell( address );
		_z.setValue( rr.getValue() );
		_z.recordWriteAccess( core, this );

		if( rd.isPostIncrement() )
		{
			address++;
		}

		if( rd.isPreDecrement() || rd.isPostIncrement() )
		{
			if( core.getDescriptor().getOnChipSramSize() > 256 )
			{
				if( _ramp != null )
				{
					_ramp.setData( (address >> 16) & 0xFF );
					_ramp.recordWriteAccess( core, this );
				}

				_rdh.setData( (address >> 8) & 0xFF );
				_rdh.recordWriteAccess( core, this );
			}

			_rdl.setData( address & 0xFF );
			_rdl.recordWriteAccess( core, this );
		}

		// --------------------------------------------------------------------
		
		core.updateClockCyclesCounter( getCyclesCount( core, address ) );
		incrementExecutionsCount();
	}

	protected long getCyclesCount( Core core, int address )
	{
		return 0l;
	}
}
