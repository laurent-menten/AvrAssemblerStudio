package be.lmenten.avr.binfmt.hex;

public interface HexImage
{
	public void setEndianness( HexDataEndianness endianness );
	public HexDataEndianness getEndianness();
}
