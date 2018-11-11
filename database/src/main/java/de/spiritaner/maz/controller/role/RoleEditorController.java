package de.spiritaner.maz.controller.role;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.meta.RoleTypeOverviewController;
import de.spiritaner.maz.model.Role;
import de.spiritaner.maz.model.meta.RoleType;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.PopOverVisitor;
import de.spiritaner.maz.util.validator.Selected;
import de.spiritaner.maz.view.binding.BindableProperty;
import de.spiritaner.maz.view.component.BindableComboBox;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class RoleEditorController extends EditorController {

	@BindableProperty
	public ObjectProperty<Role> role = new SimpleObjectProperty<>();

	public BindableComboBox<RoleType> roleTypeComboBox;
	public Button addNewRoleTypeButton;

	public void initialize(URL location, ResourceBundle resources) {
		// Bind all bindable fields to the bindable property
		autoBinder.register(this);
		role.addListener((observableValue, oldValue, newValue) -> autoBinder.rebindAll());

		// Change the validator visitor to PopOver and add Validations as well as change listeners
		autoValidator.visitor = new PopOverVisitor();
		autoValidator.add(new Selected(roleTypeComboBox, guiText.getString("role")));
		autoValidator.validateOnChange(roleTypeComboBox);

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
}
