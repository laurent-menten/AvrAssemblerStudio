package be.lmenten.avr.core.instruction.bit.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1001 0100 0100 1000",
	isAlias = true,
	alias = "BSET 4",
	statusRegister = "---0----",
	syntax = "SES",
	description = "Set sign flag"
)
public class SES
	extends BSET
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public SES()
	{
		super( StatusRegister.S );
	}

	public static SES newInstance( Integer opcode, Integer opcode2 )
	{
		return new SES();
	}
}
