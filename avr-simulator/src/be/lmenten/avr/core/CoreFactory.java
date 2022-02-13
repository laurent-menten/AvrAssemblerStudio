package be.lmenten.avr.core;

import java.util.Properties;

import be.lmenten.avr.core.descriptor.AvrDevice;

/**
 * Core instance factory.
 * 
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten</a>
 * @version 1.0
 * @since 1.0 - 2021 / 08 / 23
 */
public final class CoreFactory
{
	// ========================================================================
	// = 
	// ========================================================================

	/**
	 * Get a new default core instance.
	 * 
	 * @param name the device name
	 * @return a core instance
	 */
	public static Core forDevice( String name )
	{
		return forDevice( name, null );
	}

	/**
	 * Get a new default core instance.
	 * 
	 * @param type the device type
	 * @return a core instance
	 */
	public static Core forDevice( AvrDevice type )
	{
		return new CoreBaseImpl( type.getDescriptor() );
	}

	// ========================================================================
	// = 
	// ========================================================================

	/**
	 * Get a new and configured core instance.
	 * 
	 * @param name the device name
	 * @param config the configuration
	 * @return a core instance
	 */
	public static Core forDevice( String name, Properties config )
	{
		AvrDevice type = AvrDevice.valueOf( name );

		return forDevice( type, null );
	}

	/**
	 * Get a new and configured core instance.
	 * 
	 * @param type the device type
	 * @param config the configuration
	 * @return a core instance
	 */
	public static Core forDevice( AvrDevice type, Properties config )
	{
		return new CoreBaseImpl( type.getDescriptor(), config );
	}	
}
