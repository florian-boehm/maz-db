package de.spiritaner.maz.view.component;

import de.spiritaner.maz.model.meta.MetaClass;
import de.spiritaner.maz.view.binding.Bindable;
import de.spiritaner.maz.view.renderer.MetaClassListCell;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

import java.util.Collection;

public class BindableComboBox<T extends MetaClass> extends ComboBox<T> implements Bindable<T> {

	public StringProperty val = new SimpleStringProperty();

	public BindableComboBox() {
		super.setCellFactory(column -> new MetaClassListCell<>());
		super.setButtonCell(new MetaClassListCell<>());
	}

	@Override
	public String getVal() {
		return val.get();
	}

	@Override
	public StringProperty valProperty() {
		return val;
	}

	@Override
	public void setVal(String val) {
		this.val.set(val);
	}

	@Override
	public void bind(Property<T> p) {
		super.valueProperty().bindBidirectional(p);
	}

	public void populate(Collection<T> coll, T empty) {
		T selectedBefore = this.getValue();
		super.getItems().clear();
		super.getItems().addAll(FXCollections.observableArrayList(coll));
		if(empty != null) super.getItems().add(empty);
		super.setValue(selectedBefore);
	}
}
