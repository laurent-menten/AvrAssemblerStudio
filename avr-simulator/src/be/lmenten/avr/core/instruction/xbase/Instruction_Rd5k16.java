package be.lmenten.avr.core.instruction.xbase;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.data.CoreRegisterType;
import be.lmenten.avr.core.descriptor.CoreMemory;
import be.lmenten.avr.core.instruction.DataInstruction;
import be.lmenten.avr.core.register.Register;

public abstract class Instruction_Rd5k16
	extends Instruction_Rd5
{
	private int k;
	private DataInstruction secondWord;

	// ========================================================================
	// ===
	// ========================================================================

	public Instruction_Rd5k16( Register rd, int k )
	{
		super( rd );

		setAddress( k );
	}

	// ========================================================================
	// ===
	// ========================================================================

	protected void setAddress( int k )
	{
		if( (k<0) || (k>65535) )
		{
			throw new IllegalArgumentException( "Invalid address " + k );
		}
		this.k = k;

		this.secondWord = new DataInstruction( k );
	}

	public int getAddress()
	{
		return k;
	}

	@Override
	public DataInstruction getSecondWord()
	{
		return secondWord;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public String getOperand2( Core core )
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
