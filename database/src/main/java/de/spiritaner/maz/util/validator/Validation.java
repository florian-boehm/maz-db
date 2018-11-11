package de.spiritaner.maz.util.validator;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

public abstract class Validation {

	public String fieldName;
	public Node node;
	public StringProperty msg = new SimpleStringProperty("");

	public Validation(Node node, String fieldName) {
		this.node = node;
		this.fieldName = fieldName;
	}

	public abstract boolean isValid();

	void accept(ValidationVisitor visitor) {
		visitor.visit(this);
	}
}
