package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.meta.ResidenceTypeOverviewController;
import de.spiritaner.maz.model.Residence;
import de.spiritaner.maz.model.meta.ResidenceType;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.PopOverVisitor;
import de.spiritaner.maz.util.validator.Selected;
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

public class ResidenceEditorController extends EditorController {

	public ObjectProperty<Residence> residence = new SimpleObjectProperty<>();

	public Button addNewResidenceTypeButton;
	public BindableToggleSwitch preferredResidence;
	public BindableComboBox<ResidenceType> residenceTypeComboBox;
	public BindableToggleSwitch postAddressToggleSwitch;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		// Bind all bindable fields to the bindable property
		autoBinder.register(this);
		residence.addListener((observableValue, oldValue, newValue) -> autoBinder.rebindAll());

		// Change the validator visitor to PopOver and add Validations as well as change listeners
		autoValidator.visitor = new PopOverVisitor();
		autoValidator.add(new Selected(residenceTypeComboBox, guiText.getString("residence_type")));
		autoValidator.validateOnChange(residenceTypeComboBox);

		loadResidenceTypes();
	}

	private void loadResidenceTypes() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<ResidenceType> result = em.createNamedQuery("ResidenceType.findAll", ResidenceType.class).getResultList();

		residenceTypeComboBox.populate(result, null);
	}

	public void addNewResidenceType(ActionEvent actionEvent) {
		new ResidenceTypeOverviewController().create(actionEvent);

		loadResidenceTypes();
	}

	public BindableToggleSwitch getPreferredResidence() {
		return preferredResidence;
	}
}
