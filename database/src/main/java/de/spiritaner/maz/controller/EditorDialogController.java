package de.spiritaner.maz.controller;

import de.spiritaner.maz.model.Identifiable;
import de.spiritaner.maz.util.database.CoreDatabase;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public abstract class EditorDialogController<T extends Identifiable> implements Controller {

	private final static Logger logger = Logger.getLogger(EditorDialogController.class);

	private Stage stage;
	public ObjectProperty<T> identifiable;
	private Optional<T> result = Optional.empty();

	public Button saveButton;
	public Button cancelButton;
	public Text titleText;

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public T getIdentifiable() {
		return identifiable.get();
	}

	public void setIdentifiable(T obj) {
		identifiable.set(obj);

		if(obj != null) {
			if (obj.getId() != 0L) {
				titleText.setText(getIdentifiableName() + " bearbeiten");
				saveButton.setText("Speichern");
			} else {
				titleText.setText(getIdentifiableName() + " anlegen");
				saveButton.setText("Anlegen");
			}
		}
	}

	public String getIdentifiableName() {
		Identifiable.Annotation annotation = identifiable.getClass().getAnnotation(Identifiable.Annotation.class);
		return annotation.identifiableName();
	}

	@Deprecated
	public void closeDialog(ActionEvent actionEvent) {
		// Platform.runLater(() -> getStage().close());
		requestClose();
	}

	@Override
	public void onReopen() {
		// TODO Implement properly
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		init();
	}

	public Optional<T> getResult() {
		return result;
	}

	public void setResult(T obj) {
		this.result = Optional.of(obj);
	}

	public void setResult(Optional<T> result) {
		this.result = result;
	}

	public void requestClose() {
		stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
	}

	public void save(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			if(allValid()) {
				EntityManager em = CoreDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				preSave(em);

				try {
					T managed = (!em.contains(getIdentifiable())) ? em.merge(getIdentifiable()) : getIdentifiable();
					em.getTransaction().commit();
					setResult(managed);
					requestClose();
				} catch (PersistenceException e) {
					em.getTransaction().rollback();
					logger.warn(e);
				} finally {
					em.close();
				}
			}
		});
	}

	protected void preSave(final EntityManager em) {
	}

	protected boolean allValid() {
		return true;
	}

	protected void init() {
	}

	public Stage getStage() {
		return stage;
	}
}
