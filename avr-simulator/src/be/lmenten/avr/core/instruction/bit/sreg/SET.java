package be.lmenten.avr.core.instruction.bit.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1001 0100 0110 1000",
	isAlias = true,
	alias = "BSET 6",
	statusRegister = "-0------",
	syntax = "SET",
	description = "Set T flag"
)
public class SET
	extends BSET
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public SET()
	{
		super( StatusRegister.T );
	}

	public static SET newInstance( Integer opcode, Integer opcode2 )
	{
		return new SET();
	}
}
