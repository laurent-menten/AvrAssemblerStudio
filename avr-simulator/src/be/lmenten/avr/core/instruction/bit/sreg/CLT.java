package be.lmenten.avr.core.instruction.bit.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1001 0100 1110 1000",
	isAlias = true,
	alias = "BCLR 6",
	statusRegister = "-0------",
	syntax = "CLT",
	description = "Clear T flag"
)
public class CLT
	extends BCLR
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public CLT()
	{
		super( StatusRegister.T );
	}

	public static CLT newInstance( Integer opcode, Integer opcode2 )
	{
		return new CLT();
	}
}
