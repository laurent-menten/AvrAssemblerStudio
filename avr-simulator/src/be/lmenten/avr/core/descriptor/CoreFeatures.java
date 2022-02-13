package be.lmenten.avr.core.descriptor;

public enum CoreFeatures
{
	SPH,							// Stack Pointer High
	EIND,							// Extended Indirect Addressing
	RAMPZ,							// Extended Z-Pointer
	RAMPY,							// Extended Y-Pointer
	RAMPX,							// Extended X-Pointer
	RAMPD,							// Extended Direct Addressing

	EXTERNAL_SRAM,					// Off-chip memory
	BOOT_LOADER,					// Boot loader support
	;
};
