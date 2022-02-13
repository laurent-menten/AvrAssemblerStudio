package be.lmenten.avr.core.instruction.bit.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1001 0100 1111 1000",
	isAlias = true,
	alias = "BCLR 7",
	statusRegister = "0-------",
	syntax = "CLI",
	description = "Clear global interrupt flag"
)
public class CLI
	extends BCLR
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public CLI()
	{
		super( StatusRegister.I );
	}

	public static CLI newInstance( Integer opcode, Integer opcode2 )
	{
		return new CLI();
	}
}
