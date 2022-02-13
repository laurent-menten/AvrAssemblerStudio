package be.lmenten.avr.core.driver;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.descriptor.CoreDescriptor;

public abstract class DriverBase
	implements Driver
{
	private final String id;
	protected final Core core;
	protected final CoreDescriptor cdesc;

	// ========================================================================
	// = 
	// ========================================================================

	protected DriverBase( String id, Core core )
	{
		this.id = id;
		this.core = core;
		this.cdesc = core.getDescriptor();
	}

	// ========================================================================
	// = 
	// ========================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getId()
	{
		return id;
	}

	// ------------------------------------------------------------------------


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTick( long ticksCount )
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onReset()
	{
	}
}
