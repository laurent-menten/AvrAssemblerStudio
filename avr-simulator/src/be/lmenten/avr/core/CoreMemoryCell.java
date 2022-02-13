// = ======================================================================== =
// = === AVR Simulator =============== Copyright (c) 2022+ Laurent Menten === =
// = ======================================================================== =
// = = This program is free software: you can redistribute it and/or modify = =
// = = it under the terms of the GNU General Public License as published by = =
// = = the Free Software Foundation, either version 3 of the License, or    = =
// = = (at your option) any later version.                                  = =
// = =                                                                      = =
// = = This program is distributed in the hope that it will be useful, but  = =
// = = WITHOUT ANY WARRANTY; without even the implied warranty of           = =
// = = MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU    = =
// = = General Public License for more details.                             = =
// = =                                                                      = =
// = = You should have received a copy of the GNU General Public License    = =
// = = along with this program. If not, see                                 = =
// = = <https://www.gnu.org/licenses/>.                                     = =
// = ======================================================================== =

package be.lmenten.avr.core;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import be.lmenten.avr.core.analysis.Access;
import be.lmenten.avr.core.analysis.AccessEvent;
import be.lmenten.avr.core.analysis.AccessEventListener;
import be.lmenten.avr.core.analysis.AccessType;
import be.lmenten.avr.core.instruction.Instruction;

/**
 * <p>
 * {@link CoreMemoryCell} is root of any data or instruction held in one of
 * the memories of an Avr MCU.
 * 
 * <p>
 * Features:
 * <ul>
 * 	<li>dirty status</li>
 * 	<li>access events</li>
 * 	<li>recording of accesses</li>
 * </ul>
 *
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten<a>
 * @version 1.0
 * @since 1.0 - 2022 / 02 / 13
 */
public abstract class CoreMemoryCell
	extends CoreMemoryValue
{
	private final List<Access> accessList
		= new ArrayList<>();

	private boolean recordReadAccesses = false;
	private boolean recordWriteAccesses = false;
	private boolean recordExecuteAccesses = false;

	// -------------------------------------------------------------------------

	private final List<AccessEventListener> accessEventListeners
		= new ArrayList<>();

	private final List<AccessEventListener> driversAccessEventListeners
		= new ArrayList<>();

	// -------------------------------------------------------------------------

	private boolean hasAddress;
	private int address;

	private boolean hasInitialData;
	private int initialData;
	
	private boolean hasData;

	private boolean isDirty = false;

	// =========================================================================
	// = Constructors ==========================================================
	// =========================================================================

	/**
	 * Create a new memory cell with address and initial data.
	 * 
	 * @param address the cell address
	 * @param data the cell initial data
	 */
	public CoreMemoryCell( int address, int data )
	{
		this.hasAddress = true;
		this.address = address;

		this.hasInitialData = true;
		this.initialData = data;

		this.hasData = true;
		setData( data );
	}

	// ------------------------------------------------------------------------

	/**
	 * Create a new memory cell with address and no initial data.
	 * 
	 * @param address the cell address
	 */
	public CoreMemoryCell( int address )
	{
		this.hasAddress = true;
		this.address = address;

		this.hasInitialData = false;
		this.initialData = 0;

		this.hasData = false;
		setData( 0 );
	}

	// ------------------------------------------------------------------------

	/**
	 * Create a new memory cell with no address and no initial data.
	 */
	public CoreMemoryCell()
	{
		this.hasAddress = false;
		this.address = 0;

		this.hasInitialData = false;
		this.initialData = 0;

		this.hasData = false;
		setData( 0 );
	}

	// =========================================================================
	// = Reset =================================================================
	// =========================================================================

	/**
	 * Reset this memory cell to its default state.
	 */
	public void reset()
	{
		setData( hasInitialData ? initialData : 0 );

		isDirty = false;

		accessList.clear();
	}

	// =========================================================================
	// = Getters & setters =====================================================
	// =========================================================================

	// ------------------------------------------------------------------------

	/**
	 * Check if this memory cell has an address.
	 * 
	 * @return true if cell has an address.
	 */
	public boolean hasCellAddress()
	{
		return hasAddress;
	}

	/**
	 * Set the address of this memory cell.
	 * 
	 * @param address the cell address
	 */
	public void setCellAddress( int address )
	{
		this.address = address;
		this.hasAddress = true;
	}

	/**
	 * Get the address of this memory cell.
	 * 
	 * @return the address
	 */
	public int getCellAddress()
	{
		return address;
	}

	// ------------------------------------------------------------------------

	/**
	 * Check if this memory cell has initial data.
	 * 
	 * @return true if cell has initial data
	 */
	public boolean hasInitialData()
	{
		return hasInitialData;
	}

	/**
	 * Set the initial data of this memory cell.
	 *
	 * @param initialData the initial data
	 */
	public void setInitialData( int initialData )
	{
		this.initialData = initialData;
		this.hasInitialData = true;
	}

	/**
	 * Get the initial data of this memory cell.
	 * 
	 * @return the initial data
	 */
	public int getInitialData()
	{
		return initialData;
	}

	// ------------------------------------------------------------------------

	/**
	 * Check if this memory cell's value has changed.
	 *
	 * @return true if the cell has been written
	 */
	public boolean isDirty()
	{
		return isDirty;
	}

	/**
	 * Set the dirty status of the cell.
	 *
	 * @param dirty the dirty status
	 */
	public void setDirty( boolean dirty )
	{
		this.isDirty = dirty;
	}

	// ------------------------------------------------------------------------

	/**
	 * Check if this memory cell has data.
	 * 
	 * @return true if cell has data
	 */
	public boolean hasData()
	{
		return hasData;
	}

	/**
	 * Set the data of this memory cell. This method may trigger AccessEvent
	 * and the dirty status is updated.
	 *
	 * @param data the data
	 */
	@Override
	public void setData( int data )
	{
		setData( data, false );
	}

	/**
	 * <p>
	 * Set the data of this memory cell. This method may trigger AccessEvent
	 * and the dirty status is updated.
	 *
	 * <p>
	 * This version allow preventing AccessEvent trigger loop for the drivers.
	 *
	 * @param data the data
	 */
	public void setData( int data, boolean fromDriver )
	{
		fireWriteAccessEvent( super.getData(), data, fromDriver );

		silentSetData( data );
	}

	/**
	 * Get the data of this memory cell. This method may trigger AccessEvent.
	 *
	 * @return the value
	 */
	@Override
	public int getData()
	{
		return getData( false );
	}

	/**
	 * <p>
	 * Get the data of this memory cell. This method may trigger AccessEvent.
	 *
	 * <p>
	 * This version allow preventing AccessEvent trigger loop for the drivers.
	 *
	 * @return the value
	 */
	public int getData( boolean fromDriver )
	{
		fireReadAccessEvent( super.getData(), fromDriver );

		return silentGetData();
	}

	// ------------------------------------------------------------------------

	/**
	 * Set this cell content but no event is fired. The dirty status is
	 * updated.
	 *
	 * @param data the data
	 */
	public void silentSetData( int data )
	{
		super.setData( data & getDataMask() );
		hasData = true;

		isDirty = true;
	}

	/**
	 * <P>
	 * Get this cell content but no event is fired.
	 *
	 * @return the data
	 */
	public int silentGetData()
	{
		return ( super.getData() & getDataMask() );
	}

	// ------------------------------------------------------------------------

	/**
	 * Set the data of this memory cell. This method may trigger AccessEvent
	 * and the dirty status is updated. It is not intended to be used by drivers.
	 *
	 * @param value the value
	 */
	@Override
	public void setValue( Value value )
	{
		fireWriteAccessEvent( super.getData(), value.getData(), false );

		super.setValue( value );
	}

	/**
	 * Get the data of this memory cell. This method may trigger AccessEvent.
	 * It is not intended to be used by drivers.
	 *
	 * @return the value
	 */
	@Override
	public Value getValue()
	{
		Value v = super.getValue();

		fireReadAccessEvent( v.getData(), false );

		return v;
	}

	// =========================================================================
	// === ACCESSES ============================================================
	// =========================================================================

	/**
	 * Enable recording of read accesses.
	 * 
	 * @param enabled true if recording is enabled
	 */
	public void setReadAccessRecordingEnabled( boolean enabled )
	{
		recordReadAccesses = enabled;
	}

	/**
	 * Check if this cell records its read accesses.
	 *
	 * @return true is recording enabled
	 */
	public boolean isReadAccessRecordingEnabled()
	{
		return recordReadAccesses;
	}

	/**
	 * Record a read access.
	 *
	 * @param core the executing core
	 * @param instruction the instruction that did the read
	 */
	public void recordReadAccess( Core core, Instruction instruction )
	{
		if( recordReadAccesses )
		{
			accessList.add(
				new Access(
						core.getClockCyclesCounter(),
						instruction,
						AccessType.READ
				)
			);
		}
	}

	/**
	 *
	 * @param oldData the data
	 */
	protected void fireReadAccessEvent( int oldData, boolean fromDriver )
	{
		AccessEvent event = new AccessEvent( this );
		event.setAccessMode( AccessEvent.ACCESS_READ );
		event.setOldData( oldData );

		if( fromDriver )
		{
			fireDriverAccessEvent( event );
		}
		else
		{
			fireAccessEvent( event );
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Enable recording of write accesses.
	 * 
	 * @param enabled true if recording is enabled
	 */
	public void setWriteAccessRecordingEnabled( boolean enabled )
	{
		recordWriteAccesses = enabled;
	}

	/**
	 * Check if this cell records its write accesses.
	 *
	 * @return true is recording enabled
	 */
	public boolean isWriteAccessRecordingEnabled()
	{
		return recordWriteAccesses;
	}

	/**
	 * Record a write access.
	 *
	 * @param core the executing core
	 * @param instruction the instruction that did the write access
	 */
	public void recordWriteAccess( Core core, Instruction instruction )
	{
		if( recordWriteAccesses )
		{
			accessList.add(
				new Access(
						core.getClockCyclesCounter(),
						instruction,
						AccessType.WRITE
				)
			);
		}
	}

	/**
	 *
	 * @param oldData the old data
	 * @param newData the new data
	 */
	protected void fireWriteAccessEvent( int oldData, int newData, boolean fromDriver )
	{
		AccessEvent event = new AccessEvent( this );
		event.setAccessMode( AccessEvent.ACCESS_WRITE );
		event.setOldData( oldData );
		event.setNewData( newData );

		if( fromDriver )
		{
			fireDriverAccessEvent( event );
		}
		else
		{
			fireAccessEvent( event );
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Enable recording of execute accesses.
	 * 
	 * @param enabled true if recording is enabled
	 */
	public void setExecuteAccessRecordingEnabled( boolean enabled )
	{
		recordExecuteAccesses = enabled;
	}

	/**
	 * Check if this cell records its execute accesses.
	 * 
	 * @return true is recording enabled
	 */
	public boolean isExecuteAccessRecordingEnabled()
	{
		return recordExecuteAccesses;
	}

	/**
	 * Record a execute access.
	 * 
	 * @param core the executing core
	 * @param instruction the instruction executed
	 */
	public void recordExecuteAccess( Core core, Instruction instruction )
	{
		if( recordExecuteAccesses )
		{
			accessList.add(
				new Access(
					core.getClockCyclesCounter(),
					instruction,
					AccessType.EXECUTE
				)
			);
		}
	}

	/**
	 *
	 * @param tick the clock cycle count at execution time
	 */
	protected void fireExecuteAccessEvent( long tick, boolean fromDriver )
	{
		AccessEvent event = new AccessEvent( this );
		event.setAccessMode( AccessEvent.ACCESS_WRITE );
		event.setTick( tick );

		if( fromDriver )
		{
			fireDriverAccessEvent( event );
		}
		else
		{
			fireAccessEvent( event );
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Get an iterator for the accesses that occurred on this cell.
	 * 
	 * @return the iterator
	 */
	public ListIterator<Access> getAccessesIterator()
	{
		return accessList.listIterator();
	}

	/**
	 * Get the count of accesses that occurred on this cell.
	 * 
	 * @return the count of accesses
	 */
	public int getAccessesCount()
	{
		return accessList.size();
	}

	/**
	 * Check if this cell has been accessed.
	 * 
	 * @return true if cell was accessed
	 */
	public boolean wasAccessed()
	{
		return getAccessesCount() != 0;
	}

	// =========================================================================
	// === EVENTS (drivers) ====================================================
	// =========================================================================

	/**
	 * Add an AccessEventListener to this cell.
	 * 
	 * @param listener the listener
	 */
	public void addDriverAccessListener( AccessEventListener listener )
	{
		if( ! driversAccessEventListeners.contains( listener ) )
		{
			driversAccessEventListeners.add( listener );
		}
	}

	/**
	 * Remove an AccessEventListener from this cell.
	 * 
	 * @param listener the listener
	 */
	public void removeDriverAccessListener( AccessEventListener listener )
	{
		driversAccessEventListeners.remove( listener );
	}

	/**
	 * 
	 * @param event the event
	 */
	protected void fireDriverAccessEvent( AccessEvent event )
	{
		for( AccessEventListener listener : driversAccessEventListeners )
		{
			listener.onAccessEvent( event );
		}
	}

	// =========================================================================
	// === EVENTS ==============================================================
	// =========================================================================

	/**
	 * Add an AccessEventListener to this cell.
	 * 
	 * @param listener the listener
	 */
	public void addAccessListener( AccessEventListener listener )
	{
		if( ! accessEventListeners.contains( listener ) )
		{
			accessEventListeners.add( listener );
		}
	}

	/**
	 * Remove an AccessEventListener from this cell.
	 * 
	 * @param listener the listener
	 */
	public void removeAccessListener( AccessEventListener listener )
	{
		accessEventListeners.remove( listener );
	}

	/**
	 * 
	 * @param event the event
	 */
	protected void fireAccessEvent( AccessEvent event )
	{
		for( AccessEventListener listener : accessEventListeners )
		{
			listener.onAccessEvent( event );
		}
	}

	// ========================================================================
	//  Bits manipulations ====================================================
	// ========================================================================

}
