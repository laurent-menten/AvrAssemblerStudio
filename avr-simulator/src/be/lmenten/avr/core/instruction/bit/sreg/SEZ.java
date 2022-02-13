package be.lmenten.avr.core.instruction.bit.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1001 0100 0001 1000",
	isAlias = true,
	alias = "BSET 1",
	statusRegister = "------0-",
	syntax = "SEZ",
	description = "Set zero flag"
)
public class SEZ
	extends BSET
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public SEZ()
	{
		super( StatusRegister.Z );
	}

	public static SEZ newInstance( Integer opcode, Integer opcode2 )
	{
		return new SEZ();
	}
}
