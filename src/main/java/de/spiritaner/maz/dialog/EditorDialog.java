package de.spiritaner.maz.dialog;

import de.spiritaner.maz.controller.EditorController;
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

public class EditorDialog<T extends EditorController> {

	private final Stage stage;
	private final T controller;

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
		controller.setIdentifiable(identifiable);

		// TODO Seems to work correctly on windows, but not on linux
		stage.setOnShown(event -> {
			stage.sizeToScene();
			stage.setMaxHeight(stage.getHeight());
			stage.setMinHeight(stage.getHeight());
			stage.setMinWidth(stage.getWidth());
		});
	}

	public Identifiable showAndWait() {
		stage.showAndWait();
		return controller.getIdentifiable();
	}

	public static Identifiable showAndWait(Identifiable identifiable, Stage parent) {
		try {
			final Identifiable.Annotation identifiableAnnotation = identifiable.getClass().getAnnotation(Identifiable.Annotation.class);
			final Annotation annotation = (Annotation) identifiableAnnotation.editorDialogClass().getAnnotation(Annotation.class);
			return new EditorDialog(parent, annotation.fxmlFile(), identifiable, annotation.objDesc()).showAndWait();
		} catch (IOException e) {
			ExceptionDialog.show(e);
		}

		return null;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Annotation {
		String fxmlFile() default "";
		String objDesc() default "";
	}
}
