package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.meta.ParticipationTypeOverviewController;
import de.spiritaner.maz.model.Participation;
import de.spiritaner.maz.model.meta.ParticipationType;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.PopOverVisitor;
import de.spiritaner.maz.util.validator.Selected;
import de.spiritaner.maz.view.binding.BindableProperty;
import de.spiritaner.maz.view.component.BindableComboBox;
import de.spiritaner.maz.view.component.BindableToggleSwitch;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class ParticipationEditorController extends EditorController {

	@BindableProperty
	public ObjectProperty<Participation> participation = new SimpleObjectProperty<>();

	public BindableComboBox<ParticipationType> participationTypeComboBox;
	public Button addNewParticipationTypeButton;
	public BindableToggleSwitch participatedToogleSwitch;

	public void initialize(URL location, ResourceBundle resources) {
		// Bind all bindable fields to the bindable property
		autoBinder.register(this);
		participation.addListener((observableValue, oldValue, newValue) -> autoBinder.rebindAll());

		// Change the validator visitor to PopOver and add Validations as well as change listeners
		autoValidator.visitor = new PopOverVisitor();
		autoValidator.add(new Selected(participationTypeComboBox, guiText.getString("role")));
		autoValidator.validateOnChange(participationTypeComboBox);

		// Custom format of some fields
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
}
