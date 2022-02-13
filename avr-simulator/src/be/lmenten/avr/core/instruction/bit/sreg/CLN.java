package be.lmenten.avr.core.instruction.bit.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1001 0100 1010 1000",
	isAlias = true,
	alias = "BCLR 2",
	statusRegister = "-----0--",
	syntax = "CLN",
	description = "Clear negative flag"
)
public class CLN
	extends BCLR
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public CLN()
	{
		super( StatusRegister.N );
	}

	public static CLN newInstance( Integer opcode, Integer opcode2 )
	{
		return new CLN();
	}
}
