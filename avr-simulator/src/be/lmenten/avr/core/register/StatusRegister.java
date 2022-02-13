package be.lmenten.avr.core.register;

import static be.lmenten.avr.core.instruction.InstructionSet.*;

import be.lmenten.avr.core.instruction.InstructionSet;

public enum StatusRegister
{
	C	( CLC, SEC,		BRSH, BRLO ),
	Z	( CLZ, SEZ, 	BRNE, BREQ ),
	N	( CLN, SEN, 	BRPL, BRMI ),
	V	( CLV, SEV, 	BRVC, BRVS ),
	S	( CLS, SES, 	BRGE, BRLT ),
	H	( CLH, SEH, 	BRHC, BRHS ),
	T	( CLT, SET, 	BRTC, BRTS ),
	I	( CLI, SEI, 	BRID, BRIE ),
	;

	// ------------------------------------------------------------------------

	private final InstructionSet bclrAlias;
	private final InstructionSet bsetAlias;
	private final InstructionSet brbcAlias;
	private final InstructionSet brbsAlias;

	private final int mask;
	private final int notMask;

	// ========================================================================
	// ===
	// ========================================================================

	private StatusRegister( InstructionSet bclrAlias,
			InstructionSet bsetAlias,
			InstructionSet brbcAlias,
			InstructionSet brbsAlias
		)
	{
		this.bclrAlias = bclrAlias;
		this.bsetAlias = bsetAlias;
		this.brbcAlias = brbcAlias;
		this.brbsAlias = brbsAlias;

		this.mask = 1 << ordinal();
		this.notMask = ~this.mask;
	}

	// ========================================================================
	// ===
	// ========================================================================

	public static StatusRegister lookup( int s )
	{
		if( (s<0) || (s>7) )
		{
			throw new IllegalArgumentException( "Invalid bit index " + s );
		}

		return values()[ s ];
	}

	// ========================================================================
	// ===
	// ========================================================================

	public int getOperandIndex()
	{
		return ordinal();
	}

	public int getBitMask()
	{
		return mask;
	}

	// ========================================================================
	// ===
	// ========================================================================

	public boolean isSet( int value )
	{
		return (value & mask) == mask;
	}

	public boolean isCleared( int value )
	{
		return (value & mask) != mask;
	}

	// ========================================================================
	// ===
	// ========================================================================

	public int set( int value, boolean state )
	{
		return state ? set( value ) : clear( value );
	}

	public int set( int value )
	{
		return value | mask;
	}

	public int clear( int value )
	{
		return value & notMask;
	}

	public int toggle( int value )
	{
		return value ^ mask;
	}

	// ========================================================================
	// ===
	// ========================================================================

	public InstructionSet getBCLRAlias()
	{
		return bclrAlias;
	}

	public InstructionSet getBSETAlias()
	{
		return bsetAlias;
	}

	public InstructionSet getBRBCAlias()
	{
		return brbcAlias;
	}

	public InstructionSet getBRBSAlias()
	{
		return brbsAlias;
	}
}
