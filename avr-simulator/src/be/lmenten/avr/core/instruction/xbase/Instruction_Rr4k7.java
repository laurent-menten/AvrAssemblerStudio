package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.data.CoreRegisterType;
import be.lmenten.avr.core.descriptor.CoreMemory;
import be.lmenten.avr.core.register.UpperRegister;

public abstract class Instruction_Rr4k7
	extends Instruction_Rr4
{
	private int k;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_Rr4k7( UpperRegister rr, int k )
	{
		super( rr );

		setAddress( k );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setAddress( int k )
	{
		if( (k<0) || (k>127) )
		{
			throw new IllegalArgumentException( "Invalid address " + k );
		}
		this.k = k;
	}

	public int getAddress()
	{
		return k;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public String getOperand1( Core core )
	{
		String symbol;

		if( core != null )
		{
			if( getAddress() < core.getDescriptor().getSramBase() )
			{
				CoreRegister reg = core.getRegisterByPhysicalAddress( getAddress() );
				if( reg != null && (reg.getType() != CoreRegisterType.RESERVED) )
				{
					return reg.getName();
				}
			}

			else if( (symbol = core.getSymbol( CoreMemory.SRAM, getAddress() ) ) != null )
			{
				return symbol;
			}
		}

		return String.format( "0x%04X", getAddress() );
	}

	@Override
	public String getOperand2( Core core )
	{
		return super.getOperand1( core );
	}

	@Override
	public String getComment( Core core )
	{
		if( core != null )
		{
			if( getAddress() < core.getDescriptor().getSramBase() )
			{
				CoreRegister reg = core.getRegisterByPhysicalAddress( getAddress() );
				if( reg != null && (reg.getType() != CoreRegisterType.RESERVED) )
				{
					return String.format( "@ 0x%02X, ", (getAddress() & getDataMask() ) )
						+ reg.getRegisterDescriptor().getDescription();
				}
				else
				{
					return "Reserved register !!!";
				}
			}
	
			return String.format( "@ 0x%04X",getAddress() );
		}

		return null;
	}

}
