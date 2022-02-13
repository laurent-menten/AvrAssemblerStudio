package be.lmenten.avr.core.driver;

import be.lmenten.avr.core.data.CoreData;

public interface MemoryDriver
	extends Driver
{
	public int getManagedMemoryBase();
	public int getManagedMemorySize();
	public int getManagedMemoryLimit();

	public CoreData getMemoryCell( int address );
}
