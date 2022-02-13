package be.lmenten.avr.core.instruction.bit.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1001 0100 1001 1000",
	isAlias = true,
	alias = "BCLR 1",
	statusRegister = "------0-",
	syntax = "CLZ",
	description = "Clear zero flag"
)
public class CLZ
	extends BCLR
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public CLZ()
	{
		super( StatusRegister.Z );
	}

	public static CLZ newInstance( Integer opcode, Integer opcode2 )
	{
		return new CLZ();
	}
}
