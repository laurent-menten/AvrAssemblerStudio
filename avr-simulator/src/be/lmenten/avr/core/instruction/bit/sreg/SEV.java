package be.lmenten.avr.core.instruction.bit.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1001 0100 0011 1000",
	isAlias = true,
	alias = "BSET 3",
	statusRegister = "----0---",
	syntax = "SEV",
	description = "Set overflow flag"
)
public class SEV
	extends BSET
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public SEV()
	{
		super( StatusRegister.V );
	}

	public static SEV newInstance( Integer opcode, Integer opcode2 )
	{
		return new SEV();
	}
}
