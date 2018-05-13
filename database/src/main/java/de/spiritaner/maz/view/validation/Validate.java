package de.spiritaner.maz.view.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Validate {

	Class<? extends Validator> with();

	String name();

	boolean empty() default false;

	boolean onChange() default true;

	int max() default -1;
}