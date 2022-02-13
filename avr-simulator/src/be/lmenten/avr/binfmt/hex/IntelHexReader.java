package be.lmenten.avr.binfmt.hex;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

import be.lmenten.avr.utils.StringUtils;

public class IntelHexReader
	extends PushbackReader
	implements HexImageReader
{
	private HexDataEndianness endianness
		= HexDataEndianness.LITTLE_ENDIAN;

	private final String fileName;
	private int line;
	private int index;

	private int partialChecksum;

	private HexRecordType currentRecordType;
	private int currentRecordSize = 0;
	private int currentAddress = 0x0000;
	private int currentOffset = 0;

	private boolean hasExtendedLinearAddress = false;
	private long extendedLinearAddress = 0x00000000;
	private long startLinearAddress = 0;

	private boolean hasExtendedSegmentAddress = false;
	private int extendedSegmentAddress = 0x00000;
	private int startSegmentAddress = 0;

	private boolean eof = false;

	// ========================================================================
	// ===
	// ========================================================================

	public IntelHexReader( String fileName )
		throws IOException
	{
		super( new FileReader( fileName ) );

		this.fileName = fileName;

		newLine();
	}

	public IntelHexReader( File file )
		throws IOException
	{
		super( new FileReader( file ) );

		this.fileName = file.toString();

		newLine();
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

	private void newLine() throws IOException
	{
		partialChecksum = 0;
		currentOffset = 0;
		line++;
		index = 1;

		int c = read();
		if( c == -1 )
		{
			endOfFile();
			return;
		}

		else if( (char)c != ':' )
		{
			throw new FileFormatException( fileName, line, index,
				"Bad record header character '" + (char)c + "'" );
		}

		currentRecordSize = privReadByte();
		int address = privReadWord();
		int recordType = privReadByte();

		currentRecordType = HexRecordType.lookup( recordType );
		if( currentRecordType == null )
		{
			throw new FileFormatException( fileName, line, index,
					"Bad record type :" + String.format( "%02X", recordType ) );
		}

		switch( currentRecordType )
		{
			case DATA:
			{
				currentAddress = address;
				break;				
			}

			case EOF:
			{
				endOfFile();;
				break;				
			}

			// ----------------------------------------------------------------

			case EXTENDED_SEGMENT_ADDRESS:
			{
				hasExtendedSegmentAddress = true;
				extendedSegmentAddress = privReadWord() << 4;

				endOfLine();
				break;
			}

			case START_SEGMENT_ADDRESS:
			{
				startSegmentAddress = privReadDWord();

				endOfLine();
				break;				
			}

			// ----------------------------------------------------------------

			case EXTENDED_LINEAR_ADDRESS:
			{
				hasExtendedLinearAddress = true;
				extendedLinearAddress = privReadWord() << 16;

				endOfLine();
				break;				
			}

			case START_LINEAR_ADDRESS:
			{
				startLinearAddress = privReadDWord();

				endOfLine();
				break;				
			}
		}
	}

	private void endOfLine()
		throws IOException
	{
		verifyChecksum();

		int c;
		do
		{
			c = read();
		}
		while( Character.isWhitespace( (char)c ) );

		eof = (c == -1);
		if( !eof )
		{
			unread( c );
			newLine();
		}
		
	}

	private void endOfFile()
		throws IOException
	{
		eof = true;

		verifyChecksum();
	}

	private void verifyChecksum()
		throws IOException
	{
		int checksum = privReadByte( false );

		partialChecksum = (byte)0 - (byte)partialChecksum;
		
		if( (byte)partialChecksum != (byte)checksum )
		{
			throw new FileFormatException( fileName, line,
				String.format( "Checksum error: expected %02X got %02X", checksum, partialChecksum ) );
		}
	}

	// ========================================================================
	// ===
	// ========================================================================

	/**
	 * 
	 * @param doChecksum
	 * @return
	 * @throws IOException
	 */
	private int privReadByte( boolean doChecksum )
		throws IOException
	{
		index++;
		int c = read();
		int v;
		if( (c == -1) || ((v = StringUtils.charToValue( (char)c )) == -1) )
		{
			throw new FileFormatException( fileName, line, index,
					"Invalid character '" + (char)c + "' in byte" );
		}

		int rc = (v * 16);

		index++;
		c = read();
		if( (c == -1) || ((v = StringUtils.charToValue( (char)c )) == -1) )
		{
			throw new FileFormatException( fileName, line, index,
					"Invalid character '" + (char)c + "' in byte" );
		}

		rc += v;

		if( doChecksum )
		{
			partialChecksum += rc;
		}

		return rc;
	}

	// ------------------------------------------------------------------------

	/**
	 * BIG ENDIAN
	 * 
	 * @return
	 * @throws IOException
	 */
	private int privReadByte()
		throws IOException
	{
		return privReadByte( true );
	}

	/**
	 * BIG ENDIAN
	 * 
	 * @return
	 * @throws IOException
	 */
	private int privReadWord()
		throws IOException
	{
		return (privReadByte() * 256) + (privReadByte());
	}

	/**
	 * BIG ENDIAN
	 * 
	 * @return
	 * @throws IOException
	 */
	private int privReadDWord()
		throws IOException
	{
		return ((privReadWord() * 65536) + privReadWord() );
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public boolean eof()
		throws IOException
	{
		if( currentOffset >= currentRecordSize )
		{
			endOfLine();
		}

		return eof;
	}

	// ------------------------------------------------------------------------

	@Override
	public int readByte()
		throws IOException
	{
		if( currentOffset >= currentRecordSize )
		{
			endOfLine();
		}
		
		int rc = privReadByte();

		currentOffset++;
		currentAddress++;
		return rc;
	}

	/**
	 * LITTLE ENDIAN
	 * 
	 * @return
	 * @throws IOException
	 */
	@Override
	public int readWord()
			throws IOException
	{
		if( endianness == HexDataEndianness.BIG_ENDIAN )
		{
			return ((readByte() * 256) + readByte());
		}

		return (readByte() + (readByte() * 256));
	}

	/**
	 * LITTLE ENDIAN
	 * 
	 * @return
	 * @throws IOException
	 */
	@Override
	public long readDWord()
			throws IOException
	{
		if( endianness == HexDataEndianness.BIG_ENDIAN )
		{
			return ((readWord() * 65536) + readWord());
		}

		return (readWord() + (readWord() * 65536));
	}

	// ========================================================================
	// ===
	// ========================================================================

	@Override
	public int getAddress()
	{
		if( hasLinearAddress() )
		{
			return (int) (extendedLinearAddress + currentAddress);
		}
		else if( hasSegmentAddress() )
		{
			return extendedSegmentAddress + currentAddress;
		}

		return currentAddress;
	}

	// ------------------------------------------------------------------------

	@Override
	public boolean hasLinearAddress()
	{
		return hasExtendedLinearAddress;
	}

	@Override
	public boolean hasSegmentAddress()
	{
		return hasExtendedSegmentAddress;
	}

	@Override
	public int getStartAddress()
	{
		if( hasLinearAddress() )
		{
			return (int) (startLinearAddress);
		}
		else if( hasSegmentAddress() )
		{
			return startSegmentAddress;
		}

		return 0x0000;
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
