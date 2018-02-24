package de.spiritaner.maz.controller;

import de.spiritaner.maz.view.binding.Bindable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

abstract public class EditorController implements Initializable {

	final static Logger logger = Logger.getLogger(EditorController.class);

	public BooleanProperty readOnly = new SimpleBooleanProperty(false);

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void rebindAll() {
		Arrays.stream(this.getClass().getDeclaredFields()).filter(field -> {
			try {
				return field.get(this) instanceof Bindable;
			} catch (IllegalAccessException e) {
				return false;
			}
		}).forEach(field -> {
			try {
				Bindable bindable = (Bindable) field.get(this);
				Property property = getPropertyRecursive(this, bindable.getVal());

				if(property != null) bindable.bind(property);
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | NoSuchFieldException e) {
				logger.error(e);
			}
		});

		Arrays.stream(this.getClass().getDeclaredFields()).filter(field -> {
			try {
				return field.get(this) instanceof Node;
			} catch (IllegalAccessException e) {
				return false;
			}
		}).forEach(field -> {
			try {
				Node n = (Node) field.get(this);
				n.disableProperty().bind(readOnly);
			} catch (IllegalAccessException e) {
				logger.error(e);
			}
		});
	}

	private Property getPropertyRecursive(Object target, String keys) throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		String propertyName = keys.split("\\.")[0];
		Field targetField = target.getClass().getDeclaredField(propertyName);
		Method targetMethod = null;
		Object property = null;

		if(target != this && (targetField.getModifiers() & Modifier.PRIVATE) >= 1) {
			targetMethod = target.getClass().getDeclaredMethod(propertyName+"Property");
			property = targetMethod.invoke(target);
		} else
			property = targetField.get(target);

		if(keys.split("\\.").length == 1) {
			if(property instanceof Property)
				return (Property) property;
			else
				return null;
		} else {
			if(property instanceof Property) {
				return getPropertyRecursive(((Property) property).getValue(), keys.substring(keys.indexOf(".")+1));
			} else {
				return getPropertyRecursive(property, keys.substring(keys.indexOf(".") + 1));
			}
		}
	}
}
