package de.spiritaner.maz.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Identifiable {

	Long getId();

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@interface Annotation {
    	String identifiableName() default "";
    	Class editorDialogClass();
	}
}
