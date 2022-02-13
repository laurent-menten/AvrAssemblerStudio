package be.lmenten.avr.core.driver.xmemory;

import java.util.logging.Logger;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.CoreConfiguration;
import be.lmenten.avr.core.data.CoreData;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.driver.DriverBase;
import be.lmenten.avr.core.driver.MemoryDriver;
import be.lmenten.avr.core.exception.ConfigurationException;

public class ExternalMemoryDriver
	extends DriverBase
	implements MemoryDriver
{
	private final CoreRegister XMCRA;
	private final CoreRegister XMCRB;

	// ------------------------------------------------------------------------

	private final Integer externalRamSize;
	private final CoreData [] externalRam;

	// ========================================================================
	// = Constructor ==========================================================
	// ========================================================================

	public ExternalMemoryDriver( String id, Core core )
	{
		super( id, core );

		// --------------------------------------------------------------------

		XMCRA = core.getRegister( "XMCRA" );
		XMCRB = core.getRegister( "XMCRB" );

		// --------------------------------------------------------------------

		externalRamSize = core.getIntegerConfig( CoreConfiguration.CONFIG_EXTERNAL_SRAM );
		if( externalRamSize != null )
		{
			if( (externalRamSize < 0) || (externalRamSize > core.getDescriptor().getMaxExternalSramSize()) )
			{
				throw new ConfigurationException(
					CoreConfiguration.CONFIG_EXTERNAL_SRAM,
					externalRamSize,
					"Value out of range [0 .. "
						+ Integer.toHexString( core.getDescriptor().getMaxExternalSramSize()) + "]" 
				);
			}

			externalRam = new CoreData [ externalRamSize ];
		}
		else
		{
			externalRam = null;
		}

		LOG.info( " > External memory size = " + externalRamSize + " bytes" );
	}

	// ========================================================================
	// = MemoryDriver interface ===============================================
	// ========================================================================

	@Override
	public int getManagedMemoryBase()
	{
		return cdesc.getExternalSramBase();
	}

	@Override
	public int getManagedMemorySize()
	{
		return externalRamSize != null ? externalRamSize : 0;
	}

	@Override
	public int getManagedMemoryLimit()
	{
		return getManagedMemoryBase() + getManagedMemorySize() - 1;
	}

	// ------------------------------------------------------------------------

	@Override
	public CoreData getMemoryCell( int address )
	{
		int unmappedAddress = address - cdesc.getExternalSramBase();

		if( unmappedAddress >= externalRamSize )
		{
			throw new IndexOutOfBoundsException( address );
		}

		if( externalRam[ unmappedAddress ] == null )
		{
			externalRam[ unmappedAddress ] = new CoreData( address );
		}

		return externalRam[ unmappedAddress ];
	}

	// ========================================================================
	// = Utilities ============================================================
	// ========================================================================

	private static final Logger LOG
		= Logger.getLogger( ExternalMemoryDriver.class.getName() );
}
