package be.lmenten.avr.core.instruction.bit.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1001 0100 0000 1000",
	isAlias = true,
	alias = "BSET 0",
	statusRegister = "-------0",
	syntax = "SEC",
	description = "Set carry flag"
)
public class SEC
	extends BSET
{
	// ========================================================================
	// ===
	// ========================================================================

	public SEC()
	{
		super( StatusRegister.C );
	}

	public static SEC newInstance( Integer opcode, Integer opcode2 )
	{
		return new SEC();
	}
}
