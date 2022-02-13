// = ======================================================================== =
// = === AVR Simulator =============== Copyright (c) 2022+ Laurent Menten === =
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

package be.lmenten.avr.core.instruction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.lmenten.avr.core.descriptor.CoreVersion;
import be.lmenten.avr.core.instruction.arithmetic.ADC;
import be.lmenten.avr.core.instruction.arithmetic.ADD;
import be.lmenten.avr.core.instruction.arithmetic.ADIW;
import be.lmenten.avr.core.instruction.arithmetic.COM;
import be.lmenten.avr.core.instruction.arithmetic.DEC;
import be.lmenten.avr.core.instruction.arithmetic.DES;
import be.lmenten.avr.core.instruction.arithmetic.FMUL;
import be.lmenten.avr.core.instruction.arithmetic.FMULS;
import be.lmenten.avr.core.instruction.arithmetic.FMULSU;
import be.lmenten.avr.core.instruction.arithmetic.INC;
import be.lmenten.avr.core.instruction.arithmetic.MUL;
import be.lmenten.avr.core.instruction.arithmetic.MULS;
import be.lmenten.avr.core.instruction.arithmetic.MULSU;
import be.lmenten.avr.core.instruction.arithmetic.NEG;
import be.lmenten.avr.core.instruction.arithmetic.SBC;
import be.lmenten.avr.core.instruction.arithmetic.SBCI;
import be.lmenten.avr.core.instruction.arithmetic.SBIW;
import be.lmenten.avr.core.instruction.arithmetic.SUB;
import be.lmenten.avr.core.instruction.arithmetic.SUBI;
import be.lmenten.avr.core.instruction.arithmetic.test.CP;
import be.lmenten.avr.core.instruction.arithmetic.test.CPC;
import be.lmenten.avr.core.instruction.arithmetic.test.CPI;
import be.lmenten.avr.core.instruction.arithmetic.test.TST;
import be.lmenten.avr.core.instruction.bit.ASR;
import be.lmenten.avr.core.instruction.bit.BLD;
import be.lmenten.avr.core.instruction.bit.BST;
import be.lmenten.avr.core.instruction.bit.CBI;
import be.lmenten.avr.core.instruction.bit.CBR;
import be.lmenten.avr.core.instruction.bit.LSL;
import be.lmenten.avr.core.instruction.bit.LSR;
import be.lmenten.avr.core.instruction.bit.ROL;
import be.lmenten.avr.core.instruction.bit.ROR;
import be.lmenten.avr.core.instruction.bit.SBI;
import be.lmenten.avr.core.instruction.bit.SBR;
import be.lmenten.avr.core.instruction.bit.SWAP;
import be.lmenten.avr.core.instruction.bit.sreg.BCLR;
import be.lmenten.avr.core.instruction.bit.sreg.BSET;
import be.lmenten.avr.core.instruction.bit.sreg.CLC;
import be.lmenten.avr.core.instruction.bit.sreg.CLH;
import be.lmenten.avr.core.instruction.bit.sreg.CLI;
import be.lmenten.avr.core.instruction.bit.sreg.CLN;
import be.lmenten.avr.core.instruction.bit.sreg.CLS;
import be.lmenten.avr.core.instruction.bit.sreg.CLT;
import be.lmenten.avr.core.instruction.bit.sreg.CLV;
import be.lmenten.avr.core.instruction.bit.sreg.CLZ;
import be.lmenten.avr.core.instruction.bit.sreg.SEC;
import be.lmenten.avr.core.instruction.bit.sreg.SEH;
import be.lmenten.avr.core.instruction.bit.sreg.SEI;
import be.lmenten.avr.core.instruction.bit.sreg.SEN;
import be.lmenten.avr.core.instruction.bit.sreg.SES;
import be.lmenten.avr.core.instruction.bit.sreg.SET;
import be.lmenten.avr.core.instruction.bit.sreg.SEV;
import be.lmenten.avr.core.instruction.bit.sreg.SEZ;
import be.lmenten.avr.core.instruction.flow.CALL;
import be.lmenten.avr.core.instruction.flow.CPSE;
import be.lmenten.avr.core.instruction.flow.EICALL;
import be.lmenten.avr.core.instruction.flow.EIJMP;
import be.lmenten.avr.core.instruction.flow.ICALL;
import be.lmenten.avr.core.instruction.flow.IJMP;
import be.lmenten.avr.core.instruction.flow.JMP;
import be.lmenten.avr.core.instruction.flow.RCALL;
import be.lmenten.avr.core.instruction.flow.RET;
import be.lmenten.avr.core.instruction.flow.RETI;
import be.lmenten.avr.core.instruction.flow.RJMP;
import be.lmenten.avr.core.instruction.flow.SBIC;
import be.lmenten.avr.core.instruction.flow.SBIS;
import be.lmenten.avr.core.instruction.flow.SBRC;
import be.lmenten.avr.core.instruction.flow.SBRS;
import be.lmenten.avr.core.instruction.flow.sreg.BRBC;
import be.lmenten.avr.core.instruction.flow.sreg.BRBS;
import be.lmenten.avr.core.instruction.flow.sreg.BRCC;
import be.lmenten.avr.core.instruction.flow.sreg.BRCS;
import be.lmenten.avr.core.instruction.flow.sreg.BREQ;
import be.lmenten.avr.core.instruction.flow.sreg.BRGE;
import be.lmenten.avr.core.instruction.flow.sreg.BRHC;
import be.lmenten.avr.core.instruction.flow.sreg.BRHS;
import be.lmenten.avr.core.instruction.flow.sreg.BRID;
import be.lmenten.avr.core.instruction.flow.sreg.BRIE;
import be.lmenten.avr.core.instruction.flow.sreg.BRLO;
import be.lmenten.avr.core.instruction.flow.sreg.BRLT;
import be.lmenten.avr.core.instruction.flow.sreg.BRMI;
import be.lmenten.avr.core.instruction.flow.sreg.BRNE;
import be.lmenten.avr.core.instruction.flow.sreg.BRPL;
import be.lmenten.avr.core.instruction.flow.sreg.BRSH;
import be.lmenten.avr.core.instruction.flow.sreg.BRTC;
import be.lmenten.avr.core.instruction.flow.sreg.BRTS;
import be.lmenten.avr.core.instruction.flow.sreg.BRVC;
import be.lmenten.avr.core.instruction.flow.sreg.BRVS;
import be.lmenten.avr.core.instruction.logic.AND;
import be.lmenten.avr.core.instruction.logic.ANDI;
import be.lmenten.avr.core.instruction.logic.EOR;
import be.lmenten.avr.core.instruction.logic.OR;
import be.lmenten.avr.core.instruction.logic.ORI;
import be.lmenten.avr.core.instruction.mcu.BREAK;
import be.lmenten.avr.core.instruction.mcu.NOP;
import be.lmenten.avr.core.instruction.mcu.SLEEP;
import be.lmenten.avr.core.instruction.mcu.WDR;
import be.lmenten.avr.core.instruction.transfer.CLR;
import be.lmenten.avr.core.instruction.transfer.ELPM;
import be.lmenten.avr.core.instruction.transfer.ELPM_Rd;
import be.lmenten.avr.core.instruction.transfer.IN;
import be.lmenten.avr.core.instruction.transfer.LAC;
import be.lmenten.avr.core.instruction.transfer.LAS;
import be.lmenten.avr.core.instruction.transfer.LAT;
import be.lmenten.avr.core.instruction.transfer.LDI;
import be.lmenten.avr.core.instruction.transfer.LD;
import be.lmenten.avr.core.instruction.transfer.LD_mX;
import be.lmenten.avr.core.instruction.transfer.LD_X;
import be.lmenten.avr.core.instruction.transfer.LD_Xp;
import be.lmenten.avr.core.instruction.transfer.LD_mY;
import be.lmenten.avr.core.instruction.transfer.LD_Yp;
import be.lmenten.avr.core.instruction.transfer.LD_mZ;
import be.lmenten.avr.core.instruction.transfer.LD_Zp;
import be.lmenten.avr.core.instruction.transfer.LDD;
import be.lmenten.avr.core.instruction.transfer.LDS16;
import be.lmenten.avr.core.instruction.transfer.LDS;
import be.lmenten.avr.core.instruction.transfer.LPM;
import be.lmenten.avr.core.instruction.transfer.LPM_Rd;
import be.lmenten.avr.core.instruction.transfer.MOV;
import be.lmenten.avr.core.instruction.transfer.MOVW;
import be.lmenten.avr.core.instruction.transfer.OUT;
import be.lmenten.avr.core.instruction.transfer.POP;
import be.lmenten.avr.core.instruction.transfer.PUSH;
import be.lmenten.avr.core.instruction.transfer.SER;
import be.lmenten.avr.core.instruction.transfer.SPM;
import be.lmenten.avr.core.instruction.transfer.ST;
import be.lmenten.avr.core.instruction.transfer.ST_X;
import be.lmenten.avr.core.instruction.transfer.ST_Xp;
import be.lmenten.avr.core.instruction.transfer.ST_mX;
import be.lmenten.avr.core.instruction.transfer.ST_Yp;
import be.lmenten.avr.core.instruction.transfer.ST_mY;
import be.lmenten.avr.core.instruction.transfer.ST_Zp;
import be.lmenten.avr.core.instruction.transfer.ST_mZ;
import be.lmenten.avr.core.instruction.transfer.STD;
import be.lmenten.avr.core.instruction.transfer.STS16;
import be.lmenten.avr.core.instruction.transfer.STS;
import be.lmenten.avr.core.instruction.transfer.XCH;
import be.lmenten.avr.utils.StringUtils;

/**
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten<a>
 * @version 1.0
 * @since 1.0 - 2022 / 02 / 13
 */
public enum InstructionSet
{
	/** Add with carry */
	ADC		( ADC.class ),

	/** Add without carry */
	ADD		( ADD.class ),

	/** Add immediate to word */
	ADIW	( ADIW.class ),

	/** Logical AND */
	AND		( AND.class ),

	/** Logical AND with immediate */
	ANDI	( ANDI.class ),	

	/** Arithmetic shift right */
	ASR		( ASR.class ),

	/** Bit clear in SREG */
	BCLR	( BCLR.class ),

	/** Bit load from T flag in SREG to bit in register */
	BLD		( BLD.class ),

	/** Branch if bit in SREG is cleared */
	BRBC	( BRBC.class ),

	/** Branch if bit in SREG is set */
	BRBS	( BRBS.class ),

	/** Break */
	BREAK	( BREAK.class ),

	/** Branch if carry is cleared */
	BRCC	( BRCC.class ),

	/** Branch is carry is set */
	BRCS	( BRCS.class ),

	/** Branch if equal */
	BREQ	( BREQ.class ),		// BRBS 1, k

	/** Branch if greater or equal (signed) */
	BRGE	( BRGE.class ),		// BRBC 4, k

	/** Branch if half carry flag is cleared */
	BRHC	( BRHC.class ),		// BRBC 4, k

	/** Branch if half carry flag is set */
	BRHS	( BRHS.class ),		// BRBS 5, k

	/** Branch if global interrupt is disabled */
	BRID	( BRID.class ),		// BRBC 7, k

	/** Branch if global interrupt is enabled */
	BRIE	( BRIE.class ),		// BRBS 7, k

	/** Branch if lower (unsigned) */
	BRLO	( BRLO.class ),		// BRBS 0, k

	/** Branch if less than (signed) */
	BRLT	( BRLT.class ),		// BRBS 4, k

	/** Branch if minus */
	BRMI	( BRMI.class ),		// BRBS 2, k

	/** Branch if not equal */
	BRNE	( BRNE.class ),		// BRBC 1, k

	/** Branch if plus */
	BRPL	( BRPL.class ),		// BRBC 2, k

	/** Branch if same or higher (unsigned) */
	BRSH	( BRSH.class ),		// BRBC 0, k

	/** Branch if T flag is cleared */
	BRTC	( BRTC.class ),		// BRBC 6, k

	/** Branch if T flag is set */
	BRTS	( BRTS.class ),		// BRBS 6, k

	/** Branch if overflow flag is cleared */
	BRVC	( BRVC.class ),		// BRBC 3, k

	/** Branch if overflow flag is set */
	BRVS	( BRVS.class ),		// BRBS 3, k

	/** Bit set in SREG */
	BSET	( BSET.class ),

	/** Bit store from bit in register to T flag in SREG */
	BST		( BST.class ),	

	/** Long call to subroutine */
	CALL	( CALL.class ),

	/** Clear bit in I/O register */
	CBI		( CBI.class ),

	/** Clear bits in register	 */
	CBR		( CBR.class ),		// ANDI Rd, ($FF - K)	

	/** Clear carry flag */
	CLC		( CLC.class ),		// BCRL 0				

	/** Clear half carry flag */
	CLH		( CLH.class ),		// BCRL 5				

	/** Clear global interrupt flag */
	CLI		( CLI.class ),		// BCRL 7				

	/** Clear negative flag */
	CLN		( CLN.class ),		// BCRL 2				

	/** Clear register */
	CLR		( CLR.class ),		// EOR Rd, Rd			

	/** Clear sign flag */
	CLS		( CLS.class ),		// BCRL 4				

	/** Clear T flag */
	CLT		( CLT.class ),		// BCRL 6				

	/** Clear overflow flag */
	CLV		( CLV.class ),		// BCRL 3				

	/** Clear zero flag */
	CLZ		( CLZ.class ),		// BCRL 1				

	/** One's complement */
	COM		( COM.class ),

	/** Compare */
	CP		( CP.class ),

	/** Compare with carry */
	CPC		( CPC.class ),

	/** Compare with immediate */
	CPI		( CPI.class ),

	/** Compare and skip if equal */
	CPSE	( CPSE.class ),

	/** Decrement */
	DEC		( DEC.class ),

	/** DataInstruction Encryption Standard */
	DES		( DES.class ),

	/** Extended indirect call to subroutine */
	EICALL	( EICALL.class ),

	/** Extended indirect jump */
	EIJMP	( EIJMP.class ),

	/** Extended load program memory (to RO) */
	ELPM	( ELPM.class ),

	/** Extended load program memory (to Rd) */
	ELPM_Rd	( ELPM_Rd.class ),

	/** Logical Exclusive OR */
	EOR		( EOR.class ),

	/** Fractional multiply unsigned */
	FMUL	( FMUL.class ),

	/** Fractional multiply signed */
	FMULS	( FMULS.class ),

	/** Fractional multiply signed with unsigned */
	FMULSU	( FMULSU.class ),

	/** Indirect call to subroutine */
	ICALL	( ICALL.class ),

	/** Indirect jump */
	IJMP	( IJMP.class ),

	/** Load I/O location to register */
	IN		( IN.class ),

	/** Increment */
	INC		( INC.class ),

	/** Long jump */
	JMP		( JMP.class ),

	/** Load and clear */
	LAC		( LAC.class ),

	/** Load and set */
	LAS		( LAS.class ),

	/** Load and toggle */
	LAT		( LAT.class ),

	/** Load indirect from data space using index (SHADOWED) */
	LD		( LD.class ),

	/** Load indirect from data space using index X */
	LD_X	( LD_X.class ),

	/** Load indirect from data space using index -X */
	LD_mX	( LD_mX.class ),

	/** Load indirect from data space using index X+ */
	LD_Xp	( LD_Xp.class ),

	/** Load indirect from data space using index -Y */
	LD_mY	( LD_mY.class ),

	/** Load indirect from data space using index Y+ */
	LD_Yp	( LD_Yp.class ),

	/** Load indirect from data space using index -Z */
	LD_mZ	( LD_mZ.class ),

	/** Load indirect from data space using index Z+ */
	LD_Zp	( LD_Zp.class ),

	/** Load indirect from data space using index and displacement */
	LDD		( LDD.class ),

	/** Load immediate to register */
	LDI		( LDI.class ),

	/** Load direct (16 bits opcode) */
	LDS16	( LDS16.class ),

	/** Load direct*/
	LDS		( LDS.class ),

	/** Load program memory (to R0) */
	LPM		( LPM.class ),

	/** Load program memory (to Rd)	*/
	LPM_Rd	( LPM_Rd.class ),

	/** Logical shift left */
	LSL		( LSL.class ),		// ADD Rd, Rd			

	/** Logical shift right */
	LSR		( LSR.class ),

	/** Copy register */
	MOV		( MOV.class ),

	/** Copy register word */
	MOVW	( MOVW.class ),

	/** Multiply (unsigned) */
	MUL		( MUL.class ),

	/** Multiply (signed) */
	MULS	( MULS.class ),

	/** Multiply (signed with unsigned) */
	MULSU	( MULSU.class ),

	/** Two's complement (negate) */
	NEG		( NEG.class ),

	/** No operation */
	NOP		( NOP.class ),

	/** Logical OR */
	OR		( OR.class ),

	/** Logical OR with immediate */
	ORI		( ORI.class ),

	/** Store register to I/O location */
	OUT		( OUT.class ),

	/** Pop register from stack */
	POP		( POP.class ),

	/** Push register onto stack */
	PUSH	( PUSH.class ),

	/** Relative call to subroutine */
	RCALL	( RCALL.class ),

	/** Return from subroutine */
	RET		( RET.class ),

	/** Return from interrupt service routine */
	RETI	( RETI.class ),

	/** Relative jump */
	RJMP	( RJMP.class ),

	/** Rotate left through carry */
	ROL		( ROL.class ),		// ADC Rd, Rd			

	/** Rotate right through carry */
	ROR		( ROR.class ),

	/** Subtract with carry */
	SBC		( SBC.class ),

	/** Subtract immediate with carry */
	SBCI	( SBCI.class ),

	/** Set bit in I/O register */
	SBI		( SBI.class ),

	/** Skip if bit in I/O register cleared */
	SBIC	( SBIC.class ),

	/** Skip if bit in I/O register set */
	SBIS	( SBIS.class ),

	/** Subtract immediate from word */
	SBIW	( SBIW.class ),

	/** Set bits in register */
	SBR		( SBR.class ),		// ORI Rd, K			

	/** Skip if bit in register is cleared */
	SBRC	( SBRC.class ),

	/** Skip if bit in register is set */
	SBRS	( SBRS.class ),

	/** Set carry flag */
	SEC		( SEC.class ),		// BSET 0				

	/** Set half carry flag */
	SEH		( SEH.class ),		// BSET 5				

	/** Set global interrupt flag */
	SEI		( SEI.class ),		// BSET 7				

	/** Set negative flag */
	SEN		( SEN.class ),		// BSET 2				

	/** Set all bits in register */
	SER		( SER.class ),		// LDI Rd, $FF			

	/** Set sign flag */
	SES		( SES.class ),		// BSET 4				

	/** Set T flag */
	SET		( SET.class ),		// BSET 6				

	/** Set overflow flag */
	SEV		( SEV.class ),		// BSET 3				

	/** Set zero flag */
	SEZ		( SEZ.class ),		// BSET 1				

	/** Set circuit in sleep mode */
	SLEEP	( SLEEP.class ),

	/** Store program memory */
	SPM		( SPM.class ),

	/** Store indirect to data space using index (SHADOWED !)*/
	ST		( ST.class ),

	/** Store indirect to data space using index X */
	ST_X	( ST_X.class ),

	/** Store indirect to data space using index -X */
	ST_mX	( ST_mX.class ),

	/** Store indirect to data space using index X+ */
	ST_Xp	( ST_Xp.class ),

	/** Store indirect to data space using index -Y */
	ST_mY	( ST_mY.class ),

	/** Store indirect to data space using index Y+ */
	ST_Yp	( ST_Yp.class ),

	/** Store indirect to data space using index -Z */
	ST_mZ	( ST_mZ.class ),

	/** Store indirect to data space using index Z+ */
	ST_Zp	( ST_Zp.class ),

	/** Store indirect to data space using index and displacement */
	STD		( STD.class ),

	/** Store direct */
	STS16	( STS16.class ),

	/** Store direct (32 bits) */
	STS		( STS.class ),

	/** Subtract without carry */
	SUB		( SUB.class ),

	/** Subtract immediate */
	SUBI	( SUBI.class ),

	/** Swap nibbles */
	SWAP	( SWAP.class ),

	/** Test for zero or minus */
	TST		( TST.class ),		// AND Rd, Rd			

	/** Watch dog reset */
	WDR		( WDR.class ),

	/** Exchange */
	XCH		( XCH.class ),

	// ------------------------------------------------------------------------

	DATA	( DataInstruction.class ),
	;

	// ========================================================================
	// 
	// ========================================================================

	private static final List<InstructionSet> disassemblerList
		= new ArrayList<>();

	private static int maxMnemonicLength
		= 0;

	private static boolean isInitialized
		= false; 

	// ------------------------------------------------------------------------

	private final Class<? extends Instruction> clazz;
	private final Method newInstance;

	// ------------------------------------------------------------------------

	private String rawOpcode;
	private String rawStatusRegister;

	private int opcodeMask;
	private int opcodeMaskValue;
	private int opcodeMaskSize;
	private boolean is32bits;

	private boolean isAlias;
	private String alias;

	private String syntax;
	private String description;
	private String remark;

	private final Map<OperandType,Integer> operandMasks
		= new HashMap<>();

	private final Set<CoreVersion> versionSpecific
		= new HashSet<>();

	// ========================================================================
	// = CONSTRUCTOR ==========================================================
	// ========================================================================

	InstructionSet( Class<? extends Instruction> clazz )
	{
		this.clazz = clazz;

		try
		{
			newInstance = clazz.getDeclaredMethod( "newInstance", Integer.class, Integer.class );
		}
		catch( NoSuchMethodException | SecurityException ex )
		{
			throw new RuntimeException( name() + " has no valid newInstance(int,int) method", ex );
		}
	}

	/*package*/ static List<InstructionSet> getDisassemblerList()
	{
		return disassemblerList;
	}

	// ========================================================================
	// === LOOKUP =============================================================
	// ========================================================================

	/**
	 * Lookup an InstructionSet entry based on its opcode value.
	 * 
	 * This is done by checking the value against a reverse list of opcode
	 * values sorted by their mask weight.
	 * 
	 * @param opcode a binary opcode value (16 bits)
	 * @return the InstructionSet entry or null
	 */
	public static InstructionSet lookup( int opcode )
	{
		if( ! isInitialized )
		{
			throw new RuntimeException( "InstructionSet not initialised !!!" );
		}

		if( (opcode < 0) || (opcode > 0xFFFF) )
		{
			throw new IllegalArgumentException( "Opcode value out of bounds !!!" );
		}

		// --------------------------------------------------------------------

		for( InstructionSet instruction : disassemblerList )
		{
			if( ! instruction.isAlias
					&& ((opcode & instruction.opcodeMask) == instruction.opcodeMaskValue) )
			{
				return instruction;
			}
		}

		return null;
	}

	// ========================================================================
	// === FACTORY ============================================================
	// ========================================================================

	/**
	 * Create an instance of the handle associated with this instruction.
	 * 
	 * @param opcode instruction word
	 * @param opcode2 second instruction word for 32 bits instructions
	 * @return the newly created instance
	 */
	public Instruction newInstance( int opcode, Integer opcode2 )
	{
		if( ! isInitialized )
		{
			throw new RuntimeException( "InstructionSet not initialised !!!" );
		}

		// --------------------------------------------------------------------

		try
		{
			return (Instruction) newInstance.invoke( null, opcode, opcode2 );
		}
		catch( IllegalAccessException | IllegalArgumentException ex )
		{
			throw new RuntimeException( "Cannot instantiate " + name(), ex );
		}
		catch( InvocationTargetException ex )
		{
			throw new RuntimeException( "Cannot instantiate " + name(), ex );
		}
	}

	// ========================================================================
	// === OPERANDS MANIPULATION ==============================================
	// ========================================================================

	/**
	 * 
	 * @return the operands set
	 */
	public Set<Entry<OperandType, Integer>> getOperands()
	{
		return operandMasks.entrySet();
	}

	/**
	 * Extract the value of an operand from an opcode. If the operand bits
	 * are scattered through the opcode, they will be packed into a usable
	 * value.
	 * 
	 * @param type the type of the operand to extract
	 * @param opcode the opcode value
	 * @return the operand value
	 */
	public int extractOperand( OperandType type, int opcode )
	{
		if( operandMasks.containsKey( type ) )
		{
			int operandMask = operandMasks.get( type );
			int operandValue = 0b0000_0000_0000_0000;

			for( int i = 15 ; i >= 0 ; i-- )
			{
				if( (operandMask & (1<<i)) != 0 )
				{
					operandValue <<= 1;

					if( (opcode & (1<<i)) != 0 )
					{
						operandValue |= 1;
					}
				}
			}

			return operandValue;
		}

		throw new RuntimeException( name() + "  has no '" + type + "' of operand." );
	}

	/**
	 * Insert and operand value into an opcode. If the operand bits
	 * are scattered through the opcode, they will be unpacked accordingly.
	 * 
	 * @param type the type of the operand to extract
	 * @param opcode the original opcode value
	 * @param value the operand value
	 * @return the new opcode value with operand
	 */
	public int insertOperand( OperandType type, int opcode, int value )
	{
		if( operandMasks.containsKey( type ) )
		{
			int operandMask = operandMasks.get( type );
			int operandValue = 0b0000_0000_0000_0000;

			int operandValueMask = 0b0000_0000_0000_0001;

			for( int i = 0 ; i <= 15 ; i++ )
			{
				if( (operandMask & (1<<i)) != 0 )
				{
					if( (value & operandValueMask) == operandValueMask )
					{
						operandValue |= (1<<i);
					}

					operandValueMask <<= 1;
				}
			}

			return ((opcode & ~operandMask) | operandValue);
		}

		throw new RuntimeException( name() + "  has no '" + type + "' of operand." );
	}

	// ========================================================================
	// = Internal InstuctionSet entries initialisation ========================
	// ========================================================================

	/**
	 * For every InstructionSet entries, process the InstructionDescriptor
	 * annotation of the instruction handler implementations (using its class),
	 * then sort the entries based on its opcode mask for disassembler.
	 * <p>
	 * This method <b>SHOULD</b> be called before any use of InstructionSet.
	 */
	public static synchronized void init()
	{
		if( isInitialized )
		{
			LOG.warning( "InstructionSet is already initialised !" );

			return;
		}

		// --------------------------------------------------------------------

		LOG.info( "Initializing InstructionSet" );

		for( InstructionSet instruction : InstructionSet.values() )
		{
			LOG.finer( "Processing instruction " + instruction.name() );

			// --------------------------------------------------------------------
			// - Skip DATA --------------------------------------------------------
			// --------------------------------------------------------------------

			if( instruction == DATA )
			{
				continue;
			}

			// --------------------------------------------------------------------
			// - Set max mnemonic length ------------------------------------------
			// --------------------------------------------------------------------

			int sz = instruction.name().length();
			if( sz > maxMnemonicLength )
			{
				maxMnemonicLength = sz;
			}

			// --------------------------------------------------------------------
			// Get InstructionDescriptor annotation (mandatory).
			// --------------------------------------------------------------------

			InstructionDescriptor opcode = instruction.clazz.getAnnotation( InstructionDescriptor.class );
			if( opcode == null )
			{
				throw new RuntimeException( "Opcode " + instruction + " has no @Opcode annotation." );
			}

			// --------------------------------------------------------------------
			// Process informations.
			// --------------------------------------------------------------------

			instruction.rawOpcode = opcode.opcode();

			instruction.is32bits = opcode.is32bits();

			instruction.rawStatusRegister = opcode.statusRegister();

			instruction.versionSpecific.addAll( Arrays.asList( opcode.coreVersionSpecific() ) );

			// --------------------------------------------------------------------

			instruction.isAlias = opcode.isAlias();
			if( ! InstructionDescriptor.EMPTY.equals( opcode.alias() ) )
			{
				instruction.alias = opcode.alias();
			}

			// --------------------------------------------------------------------

			instruction.syntax = opcode.syntax();

			instruction.description = opcode.description();

			if( ! InstructionDescriptor.EMPTY.equals( opcode.remark() ) )
			{
				instruction.remark = opcode.remark();
			}

			// --------------------------------------------------------------------
			// Compute masks and add to the disassembler list.
			// --------------------------------------------------------------------

			instruction.processOpcode();

			disassemblerList.add( instruction );
		}

		// --------------------------------------------------------------------
		// Sort masks list by descending size of opcode mask (= opcode bits
		// count) and opcode mask value.
		// --------------------------------------------------------------------

		Comparator<InstructionSet> comparator = ( o1, o2 ) ->
		{
			if( o1.opcodeMaskSize == o2.opcodeMaskSize )
			{
				if( o1.opcodeMaskValue == o2.opcodeMaskValue )
				{
					return 0;
				}

				if( o1.opcodeMaskValue < o2.opcodeMaskValue )
				{
					return 1;
				}

				return -1;
			}

			if( o1.opcodeMaskSize < o2.opcodeMaskSize )
			{
				return 1;
			}

			return -1;
		};

		LOG.log( Level.FINE, "Sorting opcode list ({0} entries)", disassemblerList.size() );

		disassemblerList.sort( comparator );

		// --------------------------------------------------------------------
		// We are done and ready.
		// --------------------------------------------------------------------

		isInitialized = true;
	}

	/**
	 * Process the raw opcode string and extract instruction and opcode
	 * masks.
	 */
	private void processOpcode()
	{
		opcodeMaskValue = 0b0000_0000_0000_0000;
		opcodeMask = 0b0000_0000_0000_0000;
		opcodeMaskSize = 0;

		for( int i = 0 ; i < rawOpcode.length() ; i++ )
		{
			char c = rawOpcode.charAt( i );
			switch( c )
			{
				// ------------------------------------------------------------
				// Compute opcode mask and value.
				// Also update operand masks if any.
				// ------------------------------------------------------------

				case '1':
					opcodeMaskValue |= 1;

				case '0':
					for( Map.Entry<OperandType,Integer> entry: operandMasks.entrySet() )
					{
						entry.setValue( entry.getValue() << 1 );								
					}

					opcodeMaskValue <<= 1;
					
					opcodeMask |= 1;
					opcodeMask <<= 1;

					opcodeMaskSize++;
					break;

				// ------------------------------------------------------------
				// Compute operand mask
				// Also update other operand masks if any.
				// ------------------------------------------------------------

				case 'r':
				case 'd':
				case 's':
				case 'b':
				case 'k':
				case 'K':
				case 'A':
				case 'q':
				case 'x':
					OperandType type = OperandType.valueOf( "" + c );
					if( ! operandMasks.containsKey( type ) )
					{
						operandMasks.put( type, 0 );
					}

					for( Map.Entry<OperandType,Integer> entry: operandMasks.entrySet() )
					{
						Integer mask = entry.getValue();
						mask <<= 1;
						if( entry.getKey() == type )
						{
							mask |= 1;
						}
						entry.setValue( mask );								
					}

					opcodeMaskValue <<= 1;
					opcodeMask <<= 1;
					break;

				// ------------------------------------------------------------
				// Silently ignore space (used for readability).
				// ------------------------------------------------------------

				case ' ':
					break;

				// ------------------------------------------------------------
				// Internal error - should not arise beyond development !!!
				// ------------------------------------------------------------

				default:
					throw new RuntimeException( name() + " has invalid opcode character '" + c + "'" );
			}
		}

		// --------------------------------------------------------------------
		// Correct opcode mask and value.
		// --------------------------------------------------------------------

		opcodeMaskValue >>= 1;
		opcodeMask >>= 1;

		// --------------------------------------------------------------------
		// Log for FINEST debugging.
		// --------------------------------------------------------------------

		LOG.finest( "   "
				+ getRawOpcode() + (is32bits() ? " +" : "") + " --> "
				+ String.format( "0x%04X", opcodeMaskValue ) + " "
				+ String.format( "0x%04X", opcodeMask ) + " "
				+ String.format( "(%2d bits)", opcodeMaskSize )  + "   "
				+ (isAlias() ? "[alias for " + getAlias() + "]" : "" )
			);

		if( ! operandMasks.isEmpty() )
		{
			operandMasks.forEach(
				(key, value ) ->
					LOG.finest( "      "
							+ key + " : "
							+ String.format( "0x%04X", value )
						)
					);
		}

	}

	public static boolean isInitialized()
	{
		return isInitialized;
	}

	// ========================================================================
	// === GETTERS ============================================================
	// ========================================================================

	public static int getMaxMnemonicLength()
	{
		return maxMnemonicLength;
	}

	// ------------------------------------------------------------------------

	public String getRawOpcode()
	{
		return rawOpcode;
	}

	public String getRawStatusRegister()
	{
		return rawStatusRegister;
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the opcode with operands masked out.
	 * 
	 * @return the opcode
	 */
	public int getOpcodeMaskValue()
	{
		return opcodeMaskValue;
	}

	/**
	 * Get the opcode mask.
	 * 
	 * @return the opcode mask
	 */
	public int getOpcodeMask()
	{
		return opcodeMask;
	}

	/**
	 * Get the count of bits used for masking opcode
	 * 
	 * @return the mask size
	 */
	public int getOpcodeMaskSize()
	{
		return opcodeMaskSize;
	}

	/**
	 * Is this instruction 2 words wide.
	 * 
	 * @return true for 2 words instruction
	 */
	public boolean is32bits()
	{
		return is32bits;
	}

	/**
	 * Check if this instruction is supported by the given core version.
	 * 
	 * @param version the core version to check against
	 * 
	 * @return true if this instruction is supported by the core
	 */
	public boolean isSupportedBy( CoreVersion version )
	{
		if( versionSpecific.isEmpty() )
		{
			return true;
		}

		return versionSpecific.contains( version );
	}

	// ------------------------------------------------------------------------

	/**
	 * Check if this instruction is an alias (i.e. share the same opcode) for
	 * another instruction.
	 * 
	 * @return true if this instruction is an alias
	 */
	public boolean isAlias()
	{
		return isAlias;
	}

	/**
	 * 
	 * @return true if there is an alias
	 */
	public boolean hasAlias()
	{
		return (alias == null);
	}

	/**
	 * 
	 * @return the alias
	 */
	public String getAlias()
	{
		return alias;
	}

	// ------------------------------------------------------------------------

	/**
	 * 
	 * @return the syntax
	 */
	public String getSyntax()
	{
		return syntax;
	}

	/**
	 * 
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * 
	 * @return the remark
	 */
	public String getRemark()
	{
		return remark;
	}

	// ========================================================================
	// === DISPLAY HELPERS ====================================================
	// ========================================================================


	// ------------------------------------------------------------------------

	@Override
	public String toString()
	{
		StringBuffer s = new StringBuffer();

		s.append( StringUtils.leftPadding( this.name(), 7 ) );

		if( ! isAlias )
		{
			s.append( "   " );
		}
		else
		{
			s.append( " A " );
		}

		s.append( String.format( "0x%04X", opcodeMaskValue ) )
		 .append( ' ' )
		 .append( String.format( "0x%04X", opcodeMask ) )
		 .append( " (" )
		 .append( String.format( "%2d", opcodeMaskSize ) )
		 .append( ") : " )
		 .append( rawOpcode )
		 .append( ' ' )
		 .append( (is32bits ? '+' : ' ') )
		 .append( ' ' )
		 .append( rawStatusRegister )
		 .append( ' ' )
		 .append( description )
		 ;

		if( remark != null )
		{
			s.append( " (" )
			 .append( remark )
			 .append( ')' )
			 ;
		}

		if( ! operandMasks.isEmpty() )
		{
			operandMasks.forEach(
				(key, value ) ->
					s.append( StringUtils.rightPadding( "\n", (7 + 4) ) )
					 .append( key )
					 .append( " : " )
					 .append( String.format( "0x%04X", value ) )
			);
		}

		return s.toString();
	}

	// =========================================================================
	// === LOGGING =============================================================
	// =========================================================================

	private static final Logger LOG
		= Logger.getLogger( InstructionSet.class.getName() );	
}
