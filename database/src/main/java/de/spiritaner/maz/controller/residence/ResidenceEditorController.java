package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.meta.ResidenceTypeOverviewController;
import de.spiritaner.maz.model.Residence;
import de.spiritaner.maz.model.meta.ResidenceType;
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
import org.controlsfx.control.ToggleSwitch;

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

	private ComboBoxValidator<ResidenceType> residenceTypeValidator;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		AutoBinder ab = new AutoBinder(this);
		residence.addListener((observableValue, oldValue, newValue) -> ab.rebindAll());

		residenceTypeValidator = new ComboBoxValidator<>(residenceTypeComboBox).fieldName(guiText.getString("residence_type")).isSelected(true).validateOnChange();

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

	public boolean isValid() {
		return residenceTypeValidator.validate();
	}

	public BindableToggleSwitch getPreferredResidence() {
		return preferredResidence;
	}
}
