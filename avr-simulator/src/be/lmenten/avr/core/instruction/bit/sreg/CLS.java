package be.lmenten.avr.core.instruction.bit.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1001 0100 1100 1000",
	isAlias = true,
	alias = "BCLR 4",
	statusRegister = "---0----",
	syntax = "CLS",
	description = "Clear sign flag"
)
public class CLS
	extends BCLR
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public CLS()
	{
		super( StatusRegister.S );
	}

	public static CLS newInstance( Integer opcode, Integer opcode2 )
	{
		return new CLS();
	}
}
