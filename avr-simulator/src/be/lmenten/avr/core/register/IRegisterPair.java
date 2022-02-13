package be.lmenten.avr.core.register;

/**
 * 
 * @author Laurent Menten
 * @since 1.0
 */
public interface IRegisterPair
	extends IRegister
{
	int getUpperIndex();

	public Register getLowerRegister();
	public Register getUpperRegister();
}
