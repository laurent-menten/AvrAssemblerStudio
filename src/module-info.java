module AvrAssemblerStudio
{
	requires lib.lmenten;
	requires lib.lmenten.fx;

	requires avr.simulator;

	requires java.desktop;
	requires java.logging;
	requires java.management;
	requires java.prefs;

	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;

	exports be.lmenten.avr.simulator;
	exports be.lmenten.avr.simulator.ui;
	opens be.lmenten.avr.simulator.ui;
	exports be.lmenten.avr.simulator.ui.control;
}
