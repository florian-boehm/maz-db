package de.spiritaner.maz.view.component;

import de.spiritaner.maz.view.binding.Bindable;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;

public class BindableTextArea extends TextArea implements Bindable<String> {

    public StringProperty val = new SimpleStringProperty();

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
    public void bind(Property<String> p) {
        super.textProperty().bindBidirectional(p);
    }
}
