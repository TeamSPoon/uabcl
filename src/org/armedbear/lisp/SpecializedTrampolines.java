package org.armedbear.lisp;
import java.lang.annotation.*;
import java.lang.annotation.ElementType;
import static java.lang.annotation.ElementType.*;

public interface SpecializedTrampolines {
	
	/**
	 * Indicates that the annotate method is a good place for the lisp compiler to target.
	 */
	@Target({TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ExposeForInline {
	    /**
	     * the annotate member is a good place for the lisp compiler to target.
	     * 
	     * the first argument is the symbol-name
	     */
	    String value();
	    String pkg() default "";
	    String argspec() default "";
	    boolean exported() default true;
	    String description() default "";

	}

}
