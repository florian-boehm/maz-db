package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.residence.AddressEditorController;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.util.DataDatabase;
import de.spiritaner.maz.util.validator.DateValidator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/participation/event_editor_dialog.fxml", objDesc = "Veranstaltung")
public class EventEditorDialogController extends EditorController<Event> {

	final static Logger logger = Logger.getLogger(EventEditorDialogController.class);

	@FXML
	private ToggleSwitch addressToggleSwitch;
	@FXML
	private Button saveEventButton;
	@FXML
	private Text titleText;
	@FXML
	private GridPane eventEditor;
	@FXML
	private EventEditorController eventEditorController;
	@FXML
	private GridPane addressEditor;
	@FXML
	private AddressEditorController addressEditorController;

	private Event event;

	@Override
	public void setIdentifiable(Event obj) {
		setEvent(obj);
	}

	@Override
	public void onReopen() {
	}

	public void setEvent(Event event) {
		this.event = event;

		if (event != null) {
			// Check if a person is already set in this residence
			if (event.getAddress() != null) {
				addressEditorController.setAll(event.getAddress());
			}

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
	public void initialize(URL url, ResourceBundle resourceBundle) {
		addressToggleSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
			addressEditorController.setReadonly(!newValue);
		});

		addressToggleSwitch.setSelected(false);
		addressEditorController.setReadonly(true);
	}

	public void saveEvent(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			boolean addressValid = (addressToggleSwitch.isSelected()) ? addressEditorController.isValid() : true;
			boolean eventValid = eventEditorController.isValid();

			if (addressValid && eventValid) {
				EntityManager em = DataDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				if(addressToggleSwitch.isSelected()) event.setAddress(Address.findSame(em, addressEditorController.getAll(event.getAddress())));
				eventEditorController.getAll(event);

				try {
					if (!em.contains(event)) em.merge(event);

					em.getTransaction().commit();
					getStage().close();
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
