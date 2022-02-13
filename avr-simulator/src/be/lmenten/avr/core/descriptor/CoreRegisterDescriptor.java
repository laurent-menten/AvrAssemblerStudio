package be.lmenten.avr.core.descriptor;

import java.util.ResourceBundle;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import be.lmenten.avr.core.data.CoreRegisterType;
import be.lmenten.avr.utils.StringUtils;

/**
 * 
 * @author lmenten
 */
public class CoreRegisterDescriptor
{
	private static final String KEY_ADDRESS = "address";
	private static final String KEY_NAME = "name";
	private static final String KEY_DEFAULT = "default";
	private static final String KEY_BITS = "bits";
	private static final String KEY_MASK = "mask";

	// ------------------------------------------------------------------------

	private final CoreDescriptor cdesc;
	private final CoreRegisterType type;

	private final boolean hasAddress;
	private final int address;

	private final boolean hasName;
	private final String name;
	private final String description;
	
	private final boolean hasDefaultValue;
	private final byte defaultValue;
	private final byte mask;

	private final boolean hasBitNames;
	private final boolean [] bitKnown
		= new boolean [8]; 
	private final String [] bitNames
		= new String [8];
	private final String [] bitDescriptions
		= new String [8];


	// ========================================================================
	// === CONSTRUCTOR(S) =====================================================
	// ========================================================================

	public CoreRegisterDescriptor( CoreDescriptor cdesc, CoreRegisterType type, int address, String name )
	{
		this.cdesc = cdesc;
		this.type = type;

		this.hasAddress = true;
		this.address = address;

		this.hasName = true;		
		this.name = name;
		this.description = null;

		this.defaultValue = (byte) 0;
		this.hasDefaultValue = false;
		this.mask = (byte) 0b1111_1111;

		this.hasBitNames = false;
	}

	// ------------------------------------------------------------------------

	public CoreRegisterDescriptor( CoreDescriptor cdesc, CoreRegisterType type, JSONObject o )
	{
		this( cdesc, type, o, null );
	}

	public CoreRegisterDescriptor( CoreDescriptor cdesc, CoreRegisterType type, JSONObject o, ResourceBundle res )
	{
		this.cdesc = cdesc;
		this.type = type;

		String tmp;

		tmp = (String) o.get( KEY_ADDRESS );
		if( tmp == null )
		{
			address = 0x0000;
			hasAddress = false;
		}
		else
		{
			address = (int) StringUtils.parseNumber( tmp );
			hasAddress = true;
		}

		name = (String) o.get( KEY_NAME );
		description = (res != null) ? res.getString( name ) : null;
		hasName = (name != null);

		tmp = (String) o.get( KEY_DEFAULT );
		if( tmp == null )
		{
			defaultValue = 0b0000_0000;
			hasDefaultValue = false;
		}
		else
		{
			defaultValue = (byte) StringUtils.parseNumber( tmp );
			hasDefaultValue = true;
		}

		boolean hasBitNames = false;
		JSONArray bits = (JSONArray) o.get( KEY_BITS );
		if( bits != null)
		{
			for( int i = 0 ; i < 8 ; i++ )
			{
				if( bits.get( i ) != null )
				{
					hasBitNames = true;
				}

				bitKnown[7-i] = (bitNames[7-i] = (String) bits.get( i )) != null;
				if( bitKnown[7-i] )
				{
					String resEntryName = name + "." + bitNames[7-i];
					bitDescriptions[7-i] = (res != null) ? res.getString( resEntryName ) : null;
				}
			}
		}
		this.hasBitNames = hasBitNames;

		tmp = (String) o.get( KEY_MASK );
		if( tmp == null )
		{
			mask = (byte) 0b1111_1111;
		}
		else
		{
			mask = (byte) StringUtils.parseNumber( tmp );
		}
	}

	// ========================================================================
	// ===
	// ========================================================================

	public CoreDescriptor getCoreDescriptor()
	{
		return cdesc;
	}

	public CoreRegisterType getType()
	{
		return type;
	}

	// ------------------------------------------------------------------------

	public boolean hasAddress()
	{
		return hasAddress;
	}
	
	public int getAddress()
	{
		return address;
	}

	// ------------------------------------------------------------------------

	public boolean hasName()
	{
		return hasName;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	// ------------------------------------------------------------------------

	public boolean hasDefaultValue()
	{
		return hasDefaultValue;
	}

	public byte getDefaultValue()
	{
		return defaultValue;
	}

	public byte getMask()
	{
		return mask;
	}

	// ------------------------------------------------------------------------

	public boolean hasBitNames()
	{
		return hasBitNames;
	}

	public boolean isBitKnown( int bit )
	{
		if( (bit < 0) || (bit > 7) )
		{
			throw new IllegalArgumentException( "bit number = " + bit );
		}

		return bitKnown[ bit ];
	}

	public String getBitName( int bit )
	{
		if( (bit < 0) || (bit > 7) )
		{
			throw new IllegalArgumentException( "bit number = " + bit );
		}

		return bitNames[ bit ];
	}

	public String getBitDescription( int bit )
	{
		if( (bit < 0) || (bit > 7) )
		{
			throw new IllegalArgumentException( "bit number = " + bit );
		}

		return bitDescriptions[ bit ];
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public int getBitByName( String name )
	{
		for( int i = 0 ; i < 8 ; i++ )
		{
			if( (bitNames[i] != null) && bitNames[i].equalsIgnoreCase( name ) )
			{
				return i;
			}
		}

		return -1;
	}

	public boolean getDefaultBitValue( int bit )
	{
		if( (bit < 0) || (bit > 7) )
		{
			throw new IllegalArgumentException();
		}

		return (defaultValue & (1 << bit)) == (1 << bit);
	}

	public boolean isBitUsed( int bit )
	{
		if( (bit < 0) || (bit > 7) )
		{
			throw new IllegalArgumentException();
		}

		return (mask & (1 << bit)) == (1 << bit);
	}

	// ========================================================================
	// === Object =============================================================
	// ========================================================================

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();

		s.append( getName() ).append( " - " ).append( getDescription() ).append( '\n' );
		for( int i = 7; i >= 0 ; i-- )
		{
			s.append( "   " ) .append( i ).append( ": ");

			if( isBitKnown( i ) )
			{
				s.append( getBitName(i) ).append( " - " ).append( getBitDescription(i) );
			}

			s.append( '\n' );	
		}

		return s.toString();
	}
}
