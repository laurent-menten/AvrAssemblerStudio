package be.lmenten.avr.core.descriptor;

import java.io.InputStream;
import java.util.ResourceBundle;

public enum AvrDevice
{
	// ------------------------------------------------------------------------
	// - 
	// ------------------------------------------------------------------------

//	ATMEGA640		( "ATmega640" ),
	
//	ATMEGA1280		( "ATmega1280" ),
	
//	ATMEGA1281		( "ATmega1281" ),
	
	ATMEGA2560		( "ATmega2560" ),
	
//	ATMEGA2561		( "ATmega2561" ),

	// ------------------------------------------------------------------------
	// - 
	// ------------------------------------------------------------------------

//	ATMEGA48A		( "ATmega48A" ),
//	ATMEGA48PA		( "ATmega48PA",		ATMEGA48A ),
	
//	ATMEGA88A		( "ATmega88A" ),
//	ATMEGA88PA		( "ATmega88PA",		ATMEGA88A ),

//	ATMEGA168A		( "ATmega168A" ),
//	ATMEGA168PA		( "ATmega168PA",	ATMEGA168A ),

	ATMEGA328		( "ATmega328" ),
	ATMEGA328P		( "ATmega328P",		ATMEGA328 ),
	ATMEGA328PB		( "ATmega328PB",	ATMEGA328 ),
	;

	// ------------------------------------------------------------------------

	private final String id;
	private final AvrDevice baseCore;
	private CoreDescriptor cdesc;

	// ========================================================================
	// === CONSTRUCTOR(s) =====================================================
	// ========================================================================

	private AvrDevice( String id )
	{
		this( id, null );
	}

	private AvrDevice( String id, AvrDevice baseCore )
	{
		this.id = id;
		this.baseCore = baseCore;
	}

	// ========================================================================
	// ===
	// ========================================================================

	public static AvrDevice lookup( String deviceName )
	{
		for( AvrDevice device : AvrDevice.values() )
		{
			if( device.getId().equalsIgnoreCase( deviceName )
					|| device.name().equalsIgnoreCase(deviceName) )
			{
				return device;
			}
		}

		return null;
	}

	// ========================================================================
	// ===
	// ========================================================================

	public String getId()
	{
		return id;
	}

	public AvrDevice getBaseCore()
	{
		return baseCore;
	}

	// ------------------------------------------------------------------------

	public CoreDescriptor getDescriptor()
	{
		if( cdesc == null )
		{
			cdesc = new CoreDescriptor( this );
		}

		return cdesc;
	}

	// ========================================================================
	// ===
	// ========================================================================

	/*package*/ InputStream getDescriptorDataAsStream()
	{
		return  getClass().getResourceAsStream( "data/" + id + ".json" );
	}

	/*package*/ ResourceBundle getDescriptorResourceBundle()
	{
		if( baseCore != null )
		{
			return ResourceBundle.getBundle(
					CoreDescriptor.class.getPackageName() + ".data." + baseCore.id );
		}
		else
		{
			return ResourceBundle.getBundle(
				CoreDescriptor.class.getPackageName() + ".data." + id );
		}
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public String toString()
	{
		return id;
	}
}
