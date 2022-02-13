package be.lmenten.avr.core.descriptor;

/*
Core version         Description                    Parts
----------------------------------------------------------------------------
V0                   for parts w/o SRAM             1200
V0E                  + LPM                          tiny11/12/15
V1                   + LD/ST/LDD/STD, IJMP/ICALL    2313,2323,2343,8515,8535,
V1E?                 + LPM Rd,Z                     tiny26
V2                   + LPM Rd,Z                     mega103, tiny13,tiny2313
V2E                  + MUL LPM Rd,Z+                megaxxx, AT90CAN128
V3                   + EIJMP/EICALL                 mega256
V3X                  + SPM z+, DES                  old xmega
V3XJ                 + TA* instructions             new xmega
V8L0                 AVR8L ~= V0 + LD/ST IJMP/ICALL tiny10
                               + PUSH/POP
----------------------------------------------------------------------------
BREAK instruction seems to be randomly supported (depending on OCD capability,
presumably).
*/

public enum CoreVersionASM
{
	V0,				// for parts w/o SRAM
	V8L0,			// V0 + LD/ST IJMP/ICALL + PUSH/POP
	V0E,			// + LPM
	V1,				// + LD/ST/LDD/STD, IJMP/ICALL
	V1E,			// + LPM Rd,Z
	V2,				// + LPM Rd,Z
	V2E,			// + MUL, LPM Rd,Z+
	V3,				// + EIJMP/EICALL
	V3X,			// + SPM z+, DES
	V3XJ,			// + TA* instructions
	;
}
