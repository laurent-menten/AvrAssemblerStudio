package be.lmenten.avr.core.instruction.bit.sreg;

import be.lmenten.avr.core.instruction.InstructionDescriptor;
import be.lmenten.avr.core.register.StatusRegister;

@InstructionDescriptor
(
	opcode = "1001 0100 1011 1000",
	isAlias = true,
	alias = "BCLR 3",
	statusRegister = "----0---",
	syntax = "CLV",
	description = "Clear overflow flag"
)
public class CLV
	extends BCLR
{
	// ========================================================================
	// === Constructors and factories =========================================
	// ========================================================================

	public CLV()
	{
		super( StatusRegister.V );
	}

	public static CLV newInstance( Integer opcode, Integer opcode2 )
	{
		return new CLV();
	}
}
