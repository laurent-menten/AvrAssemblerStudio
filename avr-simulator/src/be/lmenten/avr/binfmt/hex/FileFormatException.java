package be.lmenten.avr.binfmt.hex;

import java.io.IOException;

public class FileFormatException
	extends IOException
{
	private static final long serialVersionUID = 1L;

	public FileFormatException( String message )
	{
		super( message );
	}

	public FileFormatException( String fileName, String message )
	{
		super( fileName + ": " + message );
	}

	public FileFormatException( String fileName, int line, String message )
	{
		super( fileName + "(" + line +  "): " + message );
	}

	public FileFormatException( String fileName, int line, int index, String message )
	{
		super( fileName + "(" + line +  "," + index + "): " + message );
	}
}
