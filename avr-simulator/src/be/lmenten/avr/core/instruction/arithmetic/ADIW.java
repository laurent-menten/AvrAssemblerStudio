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

import static be.lmenten.avr.core.descriptor.CoreVersion.AVR;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRe;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRe_PLUS;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRxm;
import static be.lmenten.avr.core.descriptor.CoreVersion.AVRxt;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.data.CoreStatusRegister;
import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.xbase.Instruction_Rd2K6;
import be.lmenten.avr.core.register.UpperRegisterPair;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 09 / 03
 */
@InstructionDescriptor
(
	opcode = "1001 0110 KKdd KKKK",
	coreVersionSpecific = { AVR, AVRe, AVRe_PLUS, AVRxm, AVRxt },
	statusRegister = "---*****",
	syntax = "ADIW Rd+1:Rd[24,26,28,30], K[0..63]",
	description = "Add immediate to word"
)
public class ADIW
	extends Instruction_Rd2K6
{	
	// ========================================================================
	// = Constructors and factories ===========================================
	// ========================================================================

	public ADIW( UpperRegisterPair rd, int K )
	{
		super( rd, K );

		int opc = InstructionSet.ADIW.getOpcodeMaskValue();
		opc = InstructionSet.ADIW.insertOperand( OperandType.d, opc, rd.getOperandIndex() );
		opc = InstructionSet.ADIW.insertOperand( OperandType.K, opc, getImmediate() );
		setOpcode( opc );
	}

	public static ADIW newInstance( Integer opcode, Integer opcode2 )
	{
		int d = InstructionSet.ADIW.extractOperand( OperandType.d, opcode );
		int K = InstructionSet.ADIW.extractOperand( OperandType.K, opcode );

		return new ADIW( UpperRegisterPair.lookup( d ), K );
	}

	// ------------------------------------------------------------------------

	@Override
	public InstructionSet getInstructionSetEntry()
	{
		return InstructionSet.ADIW;
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	@Override
	public void execute( Core core )
	{
		CoreStatusRegister sreg = core.getStatusRegisterCopy();

		CoreRegister _rdl = core.getRegister( this.getRd().getLowerRegister() );
		Value rdl = _rdl.getValue();
		_rdl.recordReadAccess( core, this );

		CoreRegister _rdh = core.getRegister( this.getRd().getUpperRegister() );
		Value rdh = _rdh.getValue();
		_rdh.recordReadAccess( core, this );

		Value rd = new Value( rdh, rdl );
		Value K = new Value( getImmediate(), 8 );

		// --------------------------------------------------------------------

		Value r = rd.compute( (a,b) -> { return (a + b); }, K );

		sreg.c( !r.bit15() & rdh.bit7() );
		sreg.v( !rdh.bit7() & r.bit15() );
		sreg.n( r.bit15() );
		sreg.s( sreg.n() ^ sreg.v() );
		sreg.z( r.isZero() );

		_rdl.setData( r.getData() & 0xFF);
		_rdl.recordWriteAccess( core, this );
		_rdh.setData( (r.getData() >> 8) & 0xFF );
		_rdh.recordWriteAccess( core, this );

		core.setStatusRegister( sreg );

		// --------------------------------------------------------------------

		core.updateClockCyclesCounter( 2l );
		incrementExecutionsCount();
	}
}
