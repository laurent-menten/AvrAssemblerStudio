package be.lmenten.avr.core.instruction.bit.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1001 0100 1101 1000",
	isAlias = true,
	alias = "BCLR 5",
	statusRegister = "--0-----",
	syntax = "CLH",
	description = "Clear half carry flag"
)
public class CLH
	extends BCLR
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public CLH()
	{
		super( StatusRegister.H );
	}

	public static CLH newInstance( Integer opcode, Integer opcode2 )
	{
		return new CLH();
	}
}
