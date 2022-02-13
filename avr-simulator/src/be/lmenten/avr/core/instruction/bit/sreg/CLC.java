package be.lmenten.avr.core.instruction.bit.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1001 0100 1000 1000",
	isAlias = true,
	alias = "BCLR 0",
	statusRegister = "-------0",
	syntax = "CLC",
	description = "Clear carry flag"
)
public class CLC
	extends BCLR
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public CLC()
	{
		super( StatusRegister.C );
	}

	public static CLC newInstance( Integer opcode, Integer opcode2 )
	{
		return new CLC();
	}
}
