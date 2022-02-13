package be.lmenten.avr.core.analysis;

import be.lmenten.avr.core.CoreMemoryCell;

public class AccessEvent
{
	public static final int ACCESS_READ = 1;
	public static final int ACCESS_WRITE = 2;
	public static final int ACCESS_EXECUTE = 3;
	public static final int VALUE_CHANGED = 4;
	
	// ------------------------------------------------------------------------

	private final CoreMemoryCell cell;
	private int accessMode;
	private long tick;
	private int oldData;
	private int newData;

	// ========================================================================
	// === CONSTRUCTOR(s) =====================================================
	// ========================================================================

	public AccessEvent( CoreMemoryCell cell )
	{
		this.cell = cell;
	}

	// ========================================================================
	// === GETTER(s) / SETTER(s) ==============================================
	// ========================================================================

	public CoreMemoryCell getSource() 
	{
		return cell;
	}

	// ------------------------------------------------------------------------

	public void setAccessMode( int accessMode )
	{
		this.accessMode = accessMode;
	}

	public int getAccessMode()
	{
		return accessMode;
	}

	// ------------------------------------------------------------------------

	public void setTick( long tick )
	{
		this.tick = tick;
	}

	public long getTick()
	{
		return tick;
	}

	// ------------------------------------------------------------------------

	public void setNewData( int newData )
	{
		this.newData = newData;
	}

	public int getNewData()
	{
		return newData;
	}

	public void setOldData( int oldData )
	{
		this.oldData = oldData;
	}

	public int getOldData()
	{
		return oldData;
	}
}
