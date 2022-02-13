package be.lmenten.avr.binfmt.hex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class IntelHexWriter
	extends FileWriter
	implements HexImageWriter
{
	private HexDataEndianness endianness
		= HexDataEndianness.LITTLE_ENDIAN;

	private final String fileName;

	// ========================================================================
	// ===
	// ========================================================================

	public IntelHexWriter( String fileName )
		throws IOException
	{
		super( new File( fileName ) );

		this.fileName = fileName;
	}

	public IntelHexWriter( File file )
		throws IOException
	{
		super( file );

		this.fileName = file.toString();
	}

	// ========================================================================
	// ===
	// ========================================================================

	public String getFileName()
	{
		return fileName;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public void setEndianness( HexDataEndianness endianness )
	{
		this.endianness = endianness;
	}

	@Override
	public HexDataEndianness getEndianness()
	{
		return endianness;
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public void writeByte( int data )
		throws IOException
	{
	}

	@Override
	public void writeWord( int data )
		throws IOException
	{
	}

	@Override
	public void writeDWord( long data )
		throws IOException
	{
	}

	@Override
	public void setAddress( int address )
	{
	}

	@Override
	public void setSegmentAddress( int address )
	{
	}

	@Override
	public void setStartAddress( int address )
	{
	}

	@Override
	public void setSegmentStartAddress( int address )
	{
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public void close()
		throws IOException
	{
		super.close();
	}
}
