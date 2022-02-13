package be.lmenten.avr.core.register;

import be.lmenten.avr.core.data.CoreControlRegister;

/**
 * 
 * @author Laurent Menten
 * @since 1.0
 */
public interface IRegisterIndex
	extends IRegisterPair
{
	public CoreControlRegister getExtendedRegister();
	public String getExtendedRegisterName();
}
