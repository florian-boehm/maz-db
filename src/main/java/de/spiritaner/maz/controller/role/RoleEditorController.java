package de.spiritaner.maz.controller.role;

import de.spiritaner.maz.controller.meta.ContactMethodTypeEditorController;
import de.spiritaner.maz.controller.meta.RoleTypeEditorController;
import de.spiritaner.maz.model.ContactMethod;
import de.spiritaner.maz.model.Role;
import de.spiritaner.maz.model.meta.ContactMethodType;
import de.spiritaner.maz.model.meta.RoleType;
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

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class RoleEditorController implements Initializable {

	final static Logger logger = Logger.getLogger(RoleEditorController.class);

	@FXML
	private ComboBox<RoleType> roleTypeComboBox;
	@FXML
	private Button addNewRoleTypeButton;

	private ComboBoxValidator<RoleType> roleTypeValidator;

	public void initialize(URL location, ResourceBundle resources) {
		roleTypeValidator = new ComboBoxValidator<RoleType>(roleTypeComboBox).fieldName("Funktion").isSelected(true).validateOnChange();

		roleTypeComboBox.setCellFactory(column -> {
			return new ListCell<RoleType>() {
				@Override
				public void updateItem(RoleType item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
					} else {
						setText(item.getDescription());
					}
				}
			};
		});
		roleTypeComboBox.setButtonCell(new ListCell<RoleType>() {
			@Override
			protected void updateItem(RoleType item, boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setText(null);
				} else {
					setText(item.getDescription());
				}
			}
		});

		loadRoleType();
	}

	public void setAll(Role role) {
		roleTypeComboBox.setValue(role.getRoleType());
	}

	public Role getAll(Role role) {
		if (role == null) role = new Role();
		role.setRoleType(roleTypeComboBox.getValue());
		return role;
	}

	public void setReadonly(boolean readonly) {
		roleTypeComboBox.setDisable(readonly);
		addNewRoleTypeButton.setDisable(true);
	}

	public void loadRoleType() {
		EntityManager em = DataDatabase.getFactory().createEntityManager();
		Collection<RoleType> result = em.createNamedQuery("RoleType.findAll", RoleType.class).getResultList();

		RoleType selectedBefore = roleTypeComboBox.getValue();
		roleTypeComboBox.getItems().clear();
		roleTypeComboBox.getItems().addAll(FXCollections.observableArrayList(result));
		roleTypeComboBox.setValue(selectedBefore);
	}

	public boolean isValid() {
		boolean roleTypeValid = roleTypeValidator.validate();

		return roleTypeValid;
	}

	public void addNewRoleType(ActionEvent actionEvent) {
		new RoleTypeEditorController().create(actionEvent);

		loadRoleType();
	}
}