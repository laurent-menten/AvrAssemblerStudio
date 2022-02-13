package be.lmenten.avr.binfmt.hex;

import java.io.IOException;

public interface HexImageReader
	extends AutoCloseable, HexImage
{
	public boolean eof() throws IOException;

	// ------------------------------------------------------------------------

	public int readByte() throws IOException;
	public int readWord() throws IOException;
	public long readDWord() throws IOException;

	// ------------------------------------------------------------------------

	public boolean hasLinearAddress();
	public boolean hasSegmentAddress();

	public int getAddress();
	public int getStartAddress();
}
