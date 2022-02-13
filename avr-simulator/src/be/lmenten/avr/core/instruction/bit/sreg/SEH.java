package be.lmenten.avr.core.instruction.bit.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1001 0100 0101 1000",
	isAlias = true,
	alias = "BSET 5",
	statusRegister = "--0-----",
	syntax = "SEH",
	description = "Set half carry flag"
)
public class SEH
	extends BSET
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public SEH()
	{
		super( StatusRegister.H );
	}

	public static SEH newInstance( Integer opcode, Integer opcode2 )
	{
		return new SEH();
	}
}
