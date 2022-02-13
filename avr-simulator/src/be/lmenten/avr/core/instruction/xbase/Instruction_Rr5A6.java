package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.instruction.InstructionStatus;
import be.lmenten.avr.core.register.Register;

public abstract class Instruction_Rr5A6
	extends Instruction_Rr5
{
	private int A;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_Rr5A6( int A, Register rr )
	{
		super( rr );

		setAddress( A );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setAddress( int A )
	{
		if( (A<0) || (A>63) )
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
		if( (getStatus() != InstructionStatus.RESERVED_IO) && (core != null) )
		{
			CoreRegister rdesc = core.getIORegisterByAddress( getAddress() );
			return rdesc.getName();
			
		}

		return  String.format( "0x%02X", (getAddress() & getDataMask() ) );
	}

	@Override
	public String getOperand2( Core core )
	{
		return super.getOperand1( core );
	}

	@Override
	public String getComment( Core core )
	{
		String text = null;

		if( getStatus() == InstructionStatus.RESERVED_IO )
		{
			text = "*** unknown I/O location !!!";
		}
		
		else if( core != null )
		{
			CoreRegister rdesc = core.getIORegisterByAddress( getAddress() );
			text = String.format( "@ 0x%02X, ", (getAddress() & getDataMask() ) )
					+ rdesc.getRegisterDescriptor().getDescription();
		}

		return text;
	}
}
