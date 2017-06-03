package de.spiritaner.maz.model;

import javafx.beans.property.LongProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Identifiable {

	Long getId();

	LongProperty idProperty();

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@interface Annotation {
    	String identifiableName() default "";
    	Class editorDialogClass();
	}
}
