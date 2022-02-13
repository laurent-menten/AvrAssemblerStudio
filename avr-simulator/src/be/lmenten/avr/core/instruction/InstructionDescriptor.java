package be.lmenten.avr.core.instruction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import be.lmenten.avr.core.descriptor.CoreVersion;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface InstructionDescriptor
{
	public static final String EMPTY = "";

	// ------------------------------------------------------------------------

	public String opcode();
	public boolean is32bits() default false;
	public CoreVersion [] coreVersionSpecific() default {};

	public String statusRegister();

	public boolean isAlias() default false;
	public String alias() default EMPTY;

	public String syntax();
	public String description();
	public String remark() default EMPTY;
}
