package be.lmenten.avr.core.descriptor;

public class CoreMemorySectionDescriptor
{
	private final String name;
	private final int address;
	private final int size;
	private final int limit;

	// ========================================================================
	// ===
	// ========================================================================

	/*package*/ CoreMemorySectionDescriptor( String name, int address, int size )
	{
		this.name = name;
		this.address = address;
		this.size = size;
		this.limit = (address + size) - 1;
	}

	// ========================================================================
	// ===
	// ========================================================================

	public String getName()
	{
		return name;
	}

	// ------------------------------------------------------------------------

	public int getAddress()
	{
		return address;
	}

	public int getSize()
	{
		return size;
	}

	public int getLimit()
	{
		return limit;
	}

	// ========================================================================
	// ===
	// ========================================================================

	public boolean contains( int address )
	{
		return (address >= getAddress()) && (address <= getLimit());
	}
}
