package de.spiritaner.maz.controller.role;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.meta.RoleTypeOverviewController;
import de.spiritaner.maz.model.Role;
import de.spiritaner.maz.model.meta.RoleType;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.ComboBoxValidator;
import de.spiritaner.maz.view.binding.AutoBinder;
import de.spiritaner.maz.view.binding.BindableProperty;
import de.spiritaner.maz.view.component.BindableComboBox;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class RoleEditorController extends EditorController {

	final static Logger logger = Logger.getLogger(RoleEditorController.class);

	@BindableProperty
	public ObjectProperty<Role> role = new SimpleObjectProperty<>();

	public BindableComboBox<RoleType> roleTypeComboBox;
	public Button addNewRoleTypeButton;

	private ComboBoxValidator<RoleType> roleTypeValidator;

	public void initialize(URL location, ResourceBundle resources) {
		AutoBinder ab = new AutoBinder(this);
		role.addListener((observableValue, oldValue, newValue) -> ab.rebindAll());

		roleTypeValidator = new ComboBoxValidator<>(roleTypeComboBox).fieldName("Funktion").isSelected(true).validateOnChange();

		loadRoleType();
	}

	private void loadRoleType() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<RoleType> result = em.createNamedQuery("RoleType.findAll", RoleType.class).getResultList();

		roleTypeComboBox.populate(result, null);
	}

	public void addNewRoleType(ActionEvent actionEvent) {
		new RoleTypeOverviewController().create(actionEvent);

		loadRoleType();
	}

	@Override
	public boolean isValid() {
		boolean roleTypeValid = roleTypeValidator.validate();

		return roleTypeValid;
	}
}
