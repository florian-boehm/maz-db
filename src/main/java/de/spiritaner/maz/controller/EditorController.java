package de.spiritaner.maz.controller;

import de.spiritaner.maz.model.Identifiable;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

public abstract class EditorController<T extends Identifiable> implements Controller, Initializable {

	private Stage stage;
	private T identifiable;

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public Stage getStage() {
		return stage;
	}

	public void setIdentifiable(T obj) {
		this.identifiable = obj;
	}

	public T getIdentifiable() {
		return identifiable;
	}

	public String getIdentifiableName() {
		Identifiable.Annotation annotation = identifiable.getClass().getAnnotation(Identifiable.Annotation.class);
		return annotation.identifiableName();
	}
}
