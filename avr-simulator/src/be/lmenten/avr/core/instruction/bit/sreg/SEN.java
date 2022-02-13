package be.lmenten.avr.core.instruction.bit.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1001 0100 0010 1000",
	isAlias = true,
	alias = "BSET 2",		
	statusRegister = "-----0--",
	syntax = "SEN",
	description = "Set negative flag"
)
public class SEN
	extends BSET
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public SEN()
	{
		super( StatusRegister.N );
	}

	public static SEN newInstance( Integer opcode, Integer opcode2 )
	{
		return new SEN();
	}
}
