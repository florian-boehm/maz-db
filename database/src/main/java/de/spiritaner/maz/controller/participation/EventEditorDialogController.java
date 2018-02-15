package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.view.dialog.EditorDialog;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/participation/event_editor_dialog.fxml", objDesc = "Veranstaltung")
public class EventEditorDialogController extends EditorController<Event> {

	final static Logger logger = Logger.getLogger(EventEditorDialogController.class);

	@FXML
	private Button saveEventButton;
	@FXML
	private Text titleText;
	@FXML
	private GridPane eventEditor;
	@FXML
	private EventEditorController eventEditorController;

	@Override
	public void setIdentifiable(Event event) {
		super.setIdentifiable(event);

		if (event != null) {
			eventEditorController.setAll(event);

			if (event.getId() != 0L) {
				titleText.setText("Veranstaltung bearbeiten");
				saveEventButton.setText("Speichern");
			} else {
				titleText.setText("Veranstaltung anlegen");
				saveEventButton.setText("Anlegen");
			}
		}
	}

	@Override
	public void onReopen() {
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}

	public void saveEvent(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			boolean eventValid = eventEditorController.isValid();

			if (eventValid) {
				EntityManager em = CoreDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				eventEditorController.getAll(getIdentifiable());

				try {
					Event managedEvent = (!em.contains(getIdentifiable())) ? em.merge(getIdentifiable()) : getIdentifiable();
					em.getTransaction().commit();
					setResult(managedEvent);
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

	public void closeDialog(ActionEvent actionEvent) {
		Platform.runLater(() -> getStage().close());
	}
}
