package be.lmenten.avr.core.descriptor;

import org.json.simple.JSONObject;

import be.lmenten.avr.utils.StringUtils;

public class CoreInterruptDescriptor
{
	private static final String KEY_VECTOR = "vector";
	private static final String KEY_NAME = "name";

	// ------------------------------------------------------------------------

	private final boolean hasVector;
	private final int vector;

	private final boolean hasName;
	private final String name;

	// ========================================================================
	// === CONSTRUCTOR(S) =====================================================
	// ========================================================================

	/*package*/ CoreInterruptDescriptor( JSONObject o )
	{
		String tmp;

		tmp = (String) o.get( KEY_VECTOR );
		if( tmp == null )
		{
			vector = 0;
			hasVector = false;
		}
		else
		{
			vector = (int) StringUtils.parseNumber( tmp );
			hasVector = true;
		}

		name = (String) o.get( KEY_NAME );
		hasName = (name != null);
	}

	// ========================================================================
	// ===
	// ========================================================================

	public boolean hasVector()
	{
		return hasVector;
	}

	public int getVector()
	{
		return vector;
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
}
