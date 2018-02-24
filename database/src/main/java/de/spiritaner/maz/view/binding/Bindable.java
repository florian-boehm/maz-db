package de.spiritaner.maz.view.binding;

import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;

public interface Bindable<T> {

	String getVal();

	StringProperty valProperty();

	void setVal(String val);

	void bind(Property<T> p);
}
