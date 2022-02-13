package be.lmenten.avr.core.exception;

public class ConfigurationException
	extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	private final String key;
	private final Object value;

	public ConfigurationException( String key, String message )
	{
		this( key, null, message, null );
	}

	public ConfigurationException( String key, String message, Throwable cause )
	{
		this( key, null, message, cause );
	}

	public ConfigurationException( String key, Object value, String message )
	{
		this( key, value, message, null );
	}

	public ConfigurationException( String key, Object value, String message, Throwable cause )
	{
		super( message, cause );

		this.key = key;
		this.value = value;
	}

	public String getKey()
	{
		return key;
	}

	public Object getValue()
	{
		return value;
	}

	@Override
	public String getMessage()
	{
		StringBuilder s = new StringBuilder();

		s.append( super.getMessage() );

		if( key != null )
		{
			s.append( " ")
			 .append( key )
			 ;
		}

		if( value != null )
		{
			s.append( "=" )
			 .append( value )
			 ;
		}

		return s.toString();
	}

	@Override
	public String toString()
	{
		return getLocalizedMessage();
	} 
}
