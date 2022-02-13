package be.lmenten.avr.core.instruction.bit.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1001 0100 0111 1000",
	isAlias = true,
	alias = "BSET 7",
	statusRegister = "0-------",
	syntax = "SEI",
	description = "Set global interrupt flag"
)
public class SEI
	extends BSET
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public SEI()
	{
		super( StatusRegister.I );
	}

	public static SEI newInstance( Integer opcode, Integer opcode2 )
	{
		return new SEI();
	}
}
