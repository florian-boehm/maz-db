package de.spiritaner.maz.view.binding;

import de.spiritaner.maz.controller.EditorController;
import javafx.beans.property.Property;
import javafx.scene.Node;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class AutoBinder {

	private final static Logger logger = Logger.getLogger(AutoBinder.class);
	private final EditorController controller;

	public AutoBinder(EditorController controller) {
		this.controller = controller;
	}

	public void rebindAll() {
		Arrays.stream(controller.getClass().getDeclaredFields()).filter(field -> {
			try {
				return field.get(controller) instanceof Bindable;
			} catch (IllegalAccessException e) {
				return false;
			}
		}).forEach(field -> {
			try {
				Bindable bindable = (Bindable) field.get(controller);

				if(!bindable.getVal().isEmpty()) {
					Property property = getPropertyRecursive(controller, bindable.getVal());

					if (property != null) bindable.bind(property);
				}
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
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
				n.disableProperty().bindBidirectional(controller.readOnly);
			} catch (IllegalAccessException e) {
				logger.error(e);
			}
		});
	}

	private static Property getPropertyRecursive(Object target, String keys) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		String propertyName = keys.split("\\.")[0];
		boolean fieldExists = false;
		Field targetField = null;

		try {
			targetField = target.getClass().getDeclaredField(propertyName);
			fieldExists = true;
		} catch (NoSuchFieldException e) {
			fieldExists = false;
		}

		Method targetMethod;
		Object property;

		if(!fieldExists || (targetField.getModifiers() & Modifier.PRIVATE) >= 1) {
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
