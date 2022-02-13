package be.lmenten.avr.core.driver;

public interface Driver
{
	/**
	 * <p>
	 * Returns the unique ID for this driver instance.
	 * 
	 * <p>
	 * This ID is defined in the CoreDescriptor data file's "simulator"
	 * section.
	 * 
	 * @return
	 */
	public String getId();


	/**
	 * Every time the core is reset, this method is called. Default values
	 * for I/O register is normal set by the core itself but may be set
	 * here.
	 */
	public void onReset();

	/**
	 * After every instruction simulation, this method is called.
	 * 
	 * @param currentTicksCount
	 */
	public void onTick( long currentTicksCount );
}
