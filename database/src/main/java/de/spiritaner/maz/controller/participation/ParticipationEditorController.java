package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.meta.ParticipationTypeOverviewController;
import de.spiritaner.maz.model.Participation;
import de.spiritaner.maz.model.meta.ParticipationType;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.ComboBoxValidator;
import de.spiritaner.maz.view.binding.AutoBinder;
import de.spiritaner.maz.view.component.BindableComboBox;
import de.spiritaner.maz.view.component.BindableToggleSwitch;
import de.spiritaner.maz.view.renderer.MetaClassListCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

public class ParticipationEditorController extends EditorController {

	final static Logger logger = Logger.getLogger(EventEditorController.class);

	public ObjectProperty<Participation> participation = new SimpleObjectProperty<>();

	public BindableComboBox<ParticipationType> participationTypeComboBox;
	public Button addNewParticipationTypeButton;
	public BindableToggleSwitch participatedToogleSwitch;

	private ComboBoxValidator<ParticipationType> participantTypeValidator;

	public void initialize(URL location, ResourceBundle resources) {
		AutoBinder ab = new AutoBinder(this);
		participation.addListener((observableValue, oldValue, newValue) -> ab.rebindAll());

		participantTypeValidator = new ComboBoxValidator<>(participationTypeComboBox).fieldName("Funktion").validateOnChange();

		loadParticipantType();
	}

	private void loadParticipantType() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<ParticipationType> result = em.createNamedQuery("ParticipationType.findAll", ParticipationType.class).getResultList();

		participationTypeComboBox.populate(result, null);
	}

	public void addNewParticipationType(ActionEvent actionEvent) {
		new ParticipationTypeOverviewController().create(actionEvent);

		loadParticipantType();
	}

	public boolean isValid() {
		boolean participationTypeValid = participantTypeValidator.validate();

		return participationTypeValid;
	}
}
