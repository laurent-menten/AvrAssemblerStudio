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

import static be.lmenten.avr.core.descriptor.CoreVersion.AVRxm;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreData;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd5;
import be.lmenten.avr.core.register.Register;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "1001 001d dddd 0101",
	coreVersionSpecific = { AVRxm },
	statusRegister = "--------",
	syntax = "LAS Rd",
	description = "Load and set"
)
public class LAS
	extends Instruction_Rd5
{
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public LAS( Register rd )
	{
		super( rd );

		int opc = InstructionSet.LAS.getOpcodeMaskValue();
		opc = InstructionSet.LAS.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		setOpcode( opc );
	}

	public static LAS newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.LAS.extractOperand( OperandType.d, opcode );

		return new LAS( Register.lookup( d ) );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.LAS;
	}

	// ========================================================================
	// = toString() support ===================================================
	// ========================================================================

	@Override
	public String getOperand1( Core core )
	{
		return "Z";
	}

	@Override
	public String getOperand2( Core core )
	{
		return super.getOperand1( core );
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreRegister _rd = core.getRegister( this.getRd() );
		Value rd = _rd.getValue();
		_rd.recordReadAccess( core, this );

		CoreRegister _zl = core.getRegister( Register.R30 );
		int zl = _zl.getData();
		_zl.recordReadAccess( core, this );
		CoreRegister _zh = core.getRegister( Register.R31 );
		int zh = _zh.getData();
		_zh.recordReadAccess( core, this );
	
		int address = ((zh & 0xFF) << 8) | (zl & 0xFF);

		CoreData _z = core.getSramCell( address );
		Value z = _z.getValue();

		// --------------------------------------------------------------------

		Value r = rd.compute( (a,b) -> { return (byte)(a | b); }, z );

		_z.setValue( r );
		_z.recordWriteAccess( core, this );

		_rd.setValue( z );
		_rd.recordWriteAccess( core, this );

		// --------------------------------------------------------------------
		
		core.updateClockCyclesCounter( 1l );
		incrementExecutionsCount();
	}
}
