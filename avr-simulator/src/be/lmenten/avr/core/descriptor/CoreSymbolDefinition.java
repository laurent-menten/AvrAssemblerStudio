package be.lmenten.avr.core.descriptor;

import org.json.simple.JSONObject;

public class CoreSymbolDefinition
{
	private static final String KEY_NAME = "name";
	private static final String KEY_VALUE = "value";
	private static final String KEY_COMMENT = "comment";

	// ------------------------------------------------------------------------

	private final String symbol;
	private final String value;
	private final String comment;

	// ========================================================================
	// === CONSTRUCTOR(s) =====================================================
	// ========================================================================

	/*protected*/ CoreSymbolDefinition( JSONObject o )
	{
		String tmp;

		symbol = (String) o.get( KEY_NAME );

		tmp = (String) o.get( KEY_VALUE );
		if( tmp != null )
		{
			value = tmp;
		}
		else
		{
			value = "";
		}

		comment = (String) o.get( KEY_COMMENT );
	}

	// ========================================================================
	// ===
	// ========================================================================

	public String getSymbol()
	{
		return symbol;
	}

	public String getValue()
	{
		return value;
	}

	public String getComment()
	{
		return comment;
	}
}
