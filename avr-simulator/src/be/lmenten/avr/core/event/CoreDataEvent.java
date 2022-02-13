package be.lmenten.avr.core.event;

import be.lmenten.avr.core.Core;

public class CoreDataEvent
	extends CoreEvent
{
	public static final int ACCESS_READ = 0;
	public static final int ACCESS_WRITE = 1;

	public static final int FLASH = 0;
	public static final int SRAM = 1;
	public static final int EEPROM = 2;

	// ========================================================================
	// ===
	// ========================================================================

	private int memoryType;
	private int accessType;
	private int address;

	private boolean cellJustCreated = false;

	// ========================================================================
	// === CONSTRUTOR(s) ======================================================
	// ========================================================================

	public CoreDataEvent( Core core )
	{
		super( null, core );
	}

	// ========================================================================
	// ===
	// ========================================================================

	public void setMemoryType( int memoryType )
	{
		this.memoryType = memoryType;
	}

	public int getMemoryType()
	{
		return memoryType;
	}

	public void setAccessType( int accessType )
	{
		this.accessType = accessType;
	}

	public int getAccessType()
	{
		return accessType;
	}

	public void setAddress( int address )
	{
		this.address = address;
	}

	public int getAddress()
	{
		return address;
	}

	// ------------------------------------------------------------------------

	public void cellIsNotFlashed( boolean notFlashed )
	{
		this.cellJustCreated = notFlashed;
	}

	public boolean isCellFlashed()
	{
		return this.cellJustCreated;
	}

	public void cellWasJustCreated( boolean justCreated )
	{
		this.cellJustCreated = justCreated;
	}

	public boolean wasCellJustCreated()
	{
		return cellJustCreated;
	}
}
