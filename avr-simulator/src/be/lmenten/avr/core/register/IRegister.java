package be.lmenten.avr.core.register;

/**
 * 
 * @author Laurent Menten
 * @since 1.0
 */
public interface IRegister
{
	/**
	 * Get index of register in register file.
	 * 
	 * @return
	 */
	int getIndex();

	/**
	 * Get index of register for opcode encoding.
	 * 
	 * @return
	 */
	int getOperandIndex();

	/**
	 * 
	 * @return
	 */
	String getAlias();
}
