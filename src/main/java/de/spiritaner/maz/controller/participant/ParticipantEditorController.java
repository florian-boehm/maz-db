package de.spiritaner.maz.controller.participant;

import de.spiritaner.maz.controller.meta.ParticipantTypeEditorController;
import de.spiritaner.maz.model.Participant;
import de.spiritaner.maz.model.meta.ParticipantType;
import de.spiritaner.maz.util.DataDatabase;
import de.spiritaner.maz.util.validator.ComboBoxValidator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import org.apache.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class ParticipantEditorController implements Initializable {

	final static Logger logger = Logger.getLogger(EventEditorController.class);

	@FXML
	private ComboBox<ParticipantType> participantTypeComboBox;
	@FXML
	private Button addNewParticipantTypeButton;
	@FXML
	ToggleSwitch participatedToogleSwitch;

	private ComboBoxValidator<ParticipantType> participantTypeValidator;

	public void initialize(URL location, ResourceBundle resources) {
		participantTypeValidator = new ComboBoxValidator<ParticipantType>(participantTypeComboBox).fieldName("Funktion").validateOnChange();

		participantTypeComboBox.setCellFactory(column -> {
			return new ListCell<ParticipantType>() {
				@Override
				public void updateItem(ParticipantType item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
					} else {
						setText(item.getDescription());
					}
				}
			};
		});
		participantTypeComboBox.setButtonCell(new ListCell<ParticipantType>() {
			@Override
			protected void updateItem(ParticipantType item, boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setText(null);
				} else {
					setText(item.getDescription());
				}
			}
		});

		loadParticipantType();
	}

	public void setAll(Participant participant) {
		participantTypeComboBox.setValue(participant.getParticipantType());
		participatedToogleSwitch.setSelected(participant.getHasParticipated());
	}

	public Participant getAll(Participant participant) {
		if (participant == null) participant = new Participant();
		participant.setHasParticipated(participatedToogleSwitch.isSelected());
		participant.setParticipantType(participantTypeComboBox.getValue());
		return participant;
	}

	public void setReadonly(boolean readonly) {
		participatedToogleSwitch.setDisable(readonly);
		participantTypeComboBox.setDisable(readonly);
		addNewParticipantTypeButton.setDisable(readonly);
	}

	public void loadParticipantType() {
		EntityManager em = DataDatabase.getFactory().createEntityManager();
		Collection<ParticipantType> result = em.createNamedQuery("ParticipantType.findAll", ParticipantType.class).getResultList();

		ParticipantType selectedBefore = participantTypeComboBox.getValue();
		participantTypeComboBox.getItems().clear();
		participantTypeComboBox.getItems().addAll(FXCollections.observableArrayList(result));
		participantTypeComboBox.setValue(selectedBefore);
	}

	public boolean isValid() {
		boolean participantTypeValid = participantTypeValidator.validate();

		return participantTypeValid;
	}

	public void addNewParticipantType(ActionEvent actionEvent) {
		new ParticipantTypeEditorController().create(actionEvent);

		loadParticipantType();
	}
}
