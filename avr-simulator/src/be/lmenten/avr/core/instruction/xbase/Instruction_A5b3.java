package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;

public abstract class Instruction_A5b3
	extends Instruction_b3
{
	private int A;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_A5b3( int A, int b )
	{
		super( b );

		setAddress( A );
	}

	// ========================================================================
	// ===
	// ========================================================================

	public void setAddress( int A )
	{
		if( (A<0) || (A>31) )
		{
			throw new IllegalArgumentException( "Invalid address " + A );			
		}
		this.A = A;
	}

	public int getAddress()
	{
		return A;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public String getOperand1( Core core )
	{
		return String.format( "0x2X", getAddress() );
	}

	@Override
	public String getOperand2( Core core )
	{
		return Integer.toString( getBit() );
	}

	@Override
	public String getComment( Core core )
	{
		String text = null;

		if( core != null )
		{
			CoreRegister rdesc = core.getIORegisterByAddress( getAddress() );
			if( rdesc != null )
			{
				text = rdesc.getName() + " = " + rdesc.getRegisterDescriptor().getDescription();
			}
			else
			{
				text = "*** unknown I/O location !!!";
			}
		}

		return text;		
	}
}
