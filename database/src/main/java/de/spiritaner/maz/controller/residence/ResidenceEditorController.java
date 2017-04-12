package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.controller.meta.ResidenceTypeOverviewController;
import de.spiritaner.maz.model.Residence;
import de.spiritaner.maz.model.meta.ResidenceType;
import de.spiritaner.maz.util.DataDatabase;
import de.spiritaner.maz.util.factory.MetaClassListCell;
import de.spiritaner.maz.util.validator.ComboBoxValidator;
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

public class ResidenceEditorController implements Initializable {

	@FXML private
	Button addNewResidenceTypeButton;
	@FXML
	private ToggleSwitch preferredResidence;
	@FXML
	private ComboBox<ResidenceType> residenceTypeComboBox;

	private ComboBoxValidator<ResidenceType> residenceTypeValidator;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		residenceTypeValidator = new ComboBoxValidator<>(residenceTypeComboBox).fieldName("Wohnortart").isSelected(true).validateOnChange();

		residenceTypeComboBox.setButtonCell(new MetaClassListCell<>());
		residenceTypeComboBox.setCellFactory(param -> new MetaClassListCell<>());

		loadResidenceTypes();
	}

	public void setAll(Residence residence) {
		preferredResidence.setSelected(residence.getPreferredAddress());
		residenceTypeComboBox.setValue(residence.getResidenceType());
	}

	public Residence getAll(Residence residence) {
		if (residence == null) residence = new Residence();
		residence.setResidenceType(residenceTypeComboBox.getValue());
		return residence;
	}

	public void setReadonly(boolean readonly) {
		preferredResidence.setDisable(readonly);
		residenceTypeComboBox.setDisable(readonly);
	}

	public void loadResidenceTypes() {
		EntityManager em = DataDatabase.getFactory().createEntityManager();
		Collection<ResidenceType> result = em.createNamedQuery("ResidenceType.findAll", ResidenceType.class).getResultList();

		ResidenceType selectedBefore = residenceTypeComboBox.getValue();
		residenceTypeComboBox.getItems().clear();
		residenceTypeComboBox.getItems().addAll(FXCollections.observableArrayList(result));
		residenceTypeComboBox.setValue(selectedBefore);
	}

	public boolean isValid() {
		return residenceTypeValidator.validate();
	}

	public void addNewResidenceType(ActionEvent actionEvent) {
		new ResidenceTypeOverviewController().create(actionEvent);

		loadResidenceTypes();
	}

	public ToggleSwitch getPreferredResidence() {
		return preferredResidence;
	}
}
