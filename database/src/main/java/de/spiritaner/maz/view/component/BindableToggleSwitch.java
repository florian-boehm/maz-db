package de.spiritaner.maz.view.component;

import de.spiritaner.maz.view.binding.Bindable;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.controlsfx.control.ToggleSwitch;

public class BindableToggleSwitch extends ToggleSwitch implements Bindable<Boolean> {

	public StringProperty val = new SimpleStringProperty();

	@Override
	public String getVal() {
		return val.get();
	}

	@Override
	public StringProperty valProperty() {
		return val;
	}

	public void setVal(String val) {
		this.val.set(val);
	}

	@Override
	public void bind(Property<Boolean> p) {
		super.selectedProperty().bindBidirectional(p);
	}
}
