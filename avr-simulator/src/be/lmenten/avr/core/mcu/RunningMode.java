package be.lmenten.avr.core.mcu;

public enum RunningMode
{
	// ------------------------------------------------------------------------
	// --- RUNNING MODE -------------------------------------------------------
	// ------------------------------------------------------------------------

	STOPPED,
	STEPPING,
	RUNNING,

	INTERRUPTED,
	ABORTED,

	// ------------------------------------------------------------------------
	// --- BREAK MODE ---------------------------------------------------------
	// ------------------------------------------------------------------------

	BREAK,
	
	// ------------------------------------------------------------------------
	// --- SLEEP MODES --------------------------------------------------------
	// ------------------------------------------------------------------------

	IDLE,
	ADC_NOISE_REDUCTION,
	POWER_DOWN,
	POWDER_SAVE,
	STANDBY,
	EXTENDED_STANDBY,
	;
}
