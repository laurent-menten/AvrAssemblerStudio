package be.lmenten.avr.core.instruction;

public enum InstructionStatus
{
	NOT_CHECKED,
	OK,
	RESERVED_IO,
	SRAM_ADDRESS_OUT_OF_RANGE,
	FLASH_ADDRESS_OUT_OF_RANGE,
	EXECUTE_BOOTLOADER_FROM_APPLICATION,
	;
}
