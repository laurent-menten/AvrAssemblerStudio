package be.lmenten.avr.core.instruction;

public enum OperandType
{
	d	( "Rd", "Destination register", "#ffa07a" ),
	r	( "Rr", "Source register", "#90ee90" ),
	K	( "K", "Constant data", "#87cefa" ),
	k	( "k", "Constant address", "#87cefa" ),
	A	( "A", "I/O address", "#87cefa" ),
	q	( "q", "Displacement", "#87cefa" ),
	s	( "s", "Status register bit", "#d8bfd8" ),
	b	( "b", "I/O or register bit", "#d8bfd8" ),
	x	( "x", "-", "#a9a9a9" ),
	;

	private final String code;
	private final String description;
	private final String color;

	private OperandType( String code, String description, String color )
	{
		this.code = code;
		this.description = description;
		this.color = color;
	}

	public String getCode()
	{
		return code;
	}

	public String getDescription()
	{
		return description;
	}

	public String getColor()
	{
		return color;
	}
};

