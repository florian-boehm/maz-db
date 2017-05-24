package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.meta.ParticipationTypeOverviewController;
import de.spiritaner.maz.model.Participation;
import de.spiritaner.maz.model.meta.ParticipationType;
import de.spiritaner.maz.util.database.DataDatabase;
import de.spiritaner.maz.util.factory.MetaClassListCell;
import de.spiritaner.maz.util.validator.ComboBoxValidator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.apache.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class ParticipationEditorController implements Initializable {

	final static Logger logger = Logger.getLogger(EventEditorController.class);

	@FXML
	private ComboBox<ParticipationType> participationTypeComboBox;
	@FXML
	private Button addNewParticipationTypeButton;
	@FXML
	ToggleSwitch participatedToogleSwitch;

	private ComboBoxValidator<ParticipationType> participantTypeValidator;

	public void initialize(URL location, ResourceBundle resources) {
		participantTypeValidator = new ComboBoxValidator<>(participationTypeComboBox).fieldName("Funktion").validateOnChange();

		participationTypeComboBox.setCellFactory(column -> new MetaClassListCell<>());
		participationTypeComboBox.setButtonCell(new MetaClassListCell<>());

		loadParticipantType();
	}

	public void setAll(Participation participation) {
		participationTypeComboBox.setValue(participation.getParticipationType());
		participatedToogleSwitch.setSelected(participation.getHasParticipated());
	}

	public Participation getAll(Participation participation) {
		if (participation == null) participation = new Participation();
		participation.setHasParticipated(participatedToogleSwitch.isSelected());
		participation.setParticipationType(participationTypeComboBox.getValue());
		return participation;
	}

	public void setReadonly(boolean readonly) {
		participatedToogleSwitch.setDisable(readonly);
		participationTypeComboBox.setDisable(readonly);
		addNewParticipationTypeButton.setDisable(readonly);
	}

	public void loadParticipantType() {
		EntityManager em = DataDatabase.getFactory().createEntityManager();
		Collection<ParticipationType> result = em.createNamedQuery("ParticipationType.findAll", ParticipationType.class).getResultList();

		ParticipationType selectedBefore = participationTypeComboBox.getValue();
		participationTypeComboBox.getItems().clear();
		participationTypeComboBox.getItems().addAll(FXCollections.observableArrayList(result));
		participationTypeComboBox.setValue(selectedBefore);
	}

	public boolean isValid() {
		boolean participationTypeValid = participantTypeValidator.validate();

		return participationTypeValid;
	}

	public void addNewParticipationType(ActionEvent actionEvent) {
		new ParticipationTypeOverviewController().create(actionEvent);

		loadParticipantType();
	}
}
