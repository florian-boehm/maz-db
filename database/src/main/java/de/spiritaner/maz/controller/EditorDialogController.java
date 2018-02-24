package de.spiritaner.maz.controller;

import de.spiritaner.maz.model.Identifiable;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public abstract class EditorDialogController<T extends Identifiable> implements Controller {

	private Stage stage;
	private T identifiable;
	private Optional result = Optional.empty();

	public Stage getStage() {
		return stage;
	}

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public T getIdentifiable() {
		return identifiable;
	}

	public void setIdentifiable(T obj) {
		this.identifiable = obj;
	}

	public String getIdentifiableName() {
		Identifiable.Annotation annotation = identifiable.getClass().getAnnotation(Identifiable.Annotation.class);
		return annotation.identifiableName();
	}

	public void closeDialog(ActionEvent actionEvent) {
		// Platform.runLater(() -> getStage().close());
		requestClose();
	}

	@Override
	public void onReopen() {
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
	}

	public Optional getResult() {
		return result;
	}

	public void setResult(T obj) {
		this.result = Optional.of(obj);
	}

	public void setResult(Optional result) {
		this.result = result;
	}

	public void requestClose() {
		stage.fireEvent(
				  new WindowEvent(
							 stage,
							 WindowEvent.WINDOW_CLOSE_REQUEST
				  )
		);
	}
}
