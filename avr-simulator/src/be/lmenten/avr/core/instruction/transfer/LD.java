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
import be.lmenten.avr.core.register.RegisterIndexXYZ;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "1001 000d dddd rrrr",
	statusRegister = "--------",
	isAlias = true,
	alias = "ST X|X+|-X | Y+|-Y | Z+|-Z, Rd",
	syntax = "LD Rd, X|X+|-X | Y+|-Y | Z+|-Z",
	description = "Load indirect from data space using index"
)
public class LD
	extends Instruction_Rd5
{
	private RegisterIndexXYZ rr;

	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public LD( Register rd, RegisterIndexXYZ rr )
	{
		super( rd );

		this.rr = rr;

		int opc = InstructionSet.LD.getOpcodeMaskValue();
		opc = InstructionSet.LD.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		opc = InstructionSet.LD.insertOperand( OperandType.r, opc, rr.getOperandIndex() );
		setOpcode( opc );
	}

	public static LD newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.LD.extractOperand( OperandType.d, opcode );
		int r = InstructionSet.LD.extractOperand( OperandType.r, opcode );

		return new LD( Register.lookup( d ), RegisterIndexXYZ.lookup( r ) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.LD;
	}

	// ========================================================================
	// = toString() support ===================================================
	// ========================================================================

	@Override
	public String getSyntax()
	{
		return getMnemonic() + " Rd, " + rr;
	}

	@Override
	public String getOperand2( Core core )
	{
		return rr.toString();
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	protected long getCyclesCount( Core core, int address )
	{
		return 0l;
	}

	@Override
	public void execute( Core core )
	{
		CoreRegister _rd = core.getRegister( this.getRd() );

		CoreRegister _rrl = core.getRegister( rr.getLowerRegister() );
		int rrl = _rrl.getData() & 0xFF;
		_rrl.recordReadAccess( core, this );
		CoreRegister _rrh = core.getRegister( rr.getUpperRegister() );
		int rrh = _rrh.getData() & 0xFF;
		_rrh.recordReadAccess( core, this );

		int address = ((rrh & 0xFF) << 8) | (rrl & 0xFF);

		CoreRegister _ramp = core.getRegister( rr.getExtendedRegister() );
		if( _ramp != null )
		{
			int ramp = _ramp.getData() & 0xFF;
			_ramp.recordReadAccess( core, this );

			address |= (ramp << 16);
		}

		// --------------------------------------------------------------------

		if( rr.isPreDecrement() )
		{
			address--;
		}

		CoreData _z = core.getSramCell( address );
		Value z = _z.getValue();
		_z.recordReadAccess( core, this );

		_rd.setValue( z );
		_rd.recordWriteAccess( core, this );

		if( rr.isPostIncrement() )
		{
			address++;
		}

		if( rr.isPreDecrement() || rr.isPostIncrement() )
		{
			if( core.getDescriptor().getOnChipSramSize() > 256 )
			{
				if( _ramp != null )
				{
					_ramp.setData( (address >> 16) & 0xFF );
					_ramp.recordWriteAccess( core, this );
				}

				_rrh.setData( (address >> 8) & 0xFF );
				_rrh.recordWriteAccess( core, this );
			}

			_rrl.setData( address & 0xFF );
			_rrl.recordWriteAccess( core, this );
		}

		// --------------------------------------------------------------------
		
		core.updateClockCyclesCounter( getCyclesCount( core, address ) );
		incrementExecutionsCount();
	}
}
