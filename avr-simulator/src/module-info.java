module avr.simulator
{
	exports be.lmenten.avr.core.event;
	exports be.lmenten.avr.core;
	exports be.lmenten.avr.core.descriptor;
	exports be.lmenten.avr.core.exception;
	exports be.lmenten.avr.binfmt.hex;
	exports be.lmenten.avr.core.analysis;
	exports be.lmenten.avr.core.data;
	exports be.lmenten.avr.core.instruction;
	exports be.lmenten.avr.core.instruction.flow;
	exports be.lmenten.avr.core.instruction.transfer;
	exports be.lmenten.avr.core.register;
	requires java.logging;
	requires json.simple;
}