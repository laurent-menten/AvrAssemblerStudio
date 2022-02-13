package be.lmenten.avr.core.event;

public enum CoreEventType
{
	INVALID_OPCODE,
	INVALID_OPCODE_2,
	INVALID_DATA,

	NO_INSTRUCTION,
	NO_INSTRUCTION_FOR_INTERRUPT,
	INFINITE_LOOP,

	STACK_OVERFLOW,

	INVALID_SLEEP_MODE,
	CORE_MODE_CHANGED,

	EEPROM_ADDRESS_OUT_OF_RANGE,
	
	LDS16_ADDRESS_OUT_OF_RANGE,
	
	UNSUPPORTED_INSTRUCTION,

	FLASH_MEMORY_OVERWRITING,
	FLASH_MEMORY_UNINITIALIZED,
	;
}