package be.lmenten.avr.binfmt.hex;

import java.io.IOException;

public interface HexImageWriter
	extends AutoCloseable, HexImage
{
		public void writeByte( int data ) throws IOException;
		public void writeWord( int data ) throws IOException;
		public void writeDWord( long data ) throws IOException;

		// ------------------------------------------------------------------------

		public void setAddress( int address );
		public void setSegmentAddress( int address );

		public void setStartAddress( int address );
		public void setSegmentStartAddress( int address );
}
