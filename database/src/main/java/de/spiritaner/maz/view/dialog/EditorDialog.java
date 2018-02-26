package de.spiritaner.maz.view.dialog;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.model.Identifiable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class EditorDialog<T extends EditorDialogController> {

	private final Stage stage;
	private final T controller;
	private Optional result = Optional.empty();

	private EditorDialog(Stage parent, String fxmlFile, Identifiable identifiable, String identifiableName) throws IOException {
		FXMLLoader loader = new FXMLLoader(Scene.class.getClass().getResource(fxmlFile));
		Parent root = loader.load();
		controller = loader.getController();

		stage = new Stage();
		stage.setTitle((identifiable == null || identifiable.getId() == 0L) ? identifiableName + " anlegen" : identifiableName + " bearbeiten");
		stage.initOwner(parent);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(true);
		stage.setScene(new Scene(root));
		stage.sizeToScene();

		root.getStylesheets().add(EditorDialog.class.getClass().getResource("/css/editor_dialog.css").toExternalForm());

		// Initialize the controller
		controller.setStage(stage);
		controller.identifiable.set(identifiable);

		// TODO Seems to work correctly on windows, but not on linux
		stage.setOnShown(event -> {
			stage.sizeToScene();
			stage.setMaxHeight(stage.getHeight());
			stage.setMinHeight(stage.getHeight());
			stage.setMinWidth(stage.getWidth());
		});

		stage.setOnCloseRequest(event -> {
			this.result = controller.getResult();
		});
	}

	public Optional showAndWait() {
		stage.showAndWait();
		return this.result;
	}

	public static Optional showAndWait(Identifiable identifiable, Stage parent) {
		try {
			final Identifiable.Annotation identifiableAnnotation = identifiable.getClass().getAnnotation(Identifiable.Annotation.class);
			final Annotation annotation = (Annotation) identifiableAnnotation.editorDialogClass().getAnnotation(Annotation.class);
			return new EditorDialog(parent, annotation.fxmlFile(), identifiable, annotation.objDesc()).showAndWait();
		} catch (IOException e) {
			ExceptionDialog.show(e);
		}

		return Optional.empty();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Annotation {
		String fxmlFile() default "";
		String objDesc() default "";
	}
}
