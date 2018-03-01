package de.spiritaner.maz.controller.role;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Role;
import de.spiritaner.maz.model.meta.RoleType;
import de.spiritaner.maz.view.dialog.RemoveDialog;
import de.spiritaner.maz.view.renderer.MetaClassTableCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

public class RoleOverviewController extends OverviewController<Role> {

	public TableColumn<Role, RoleType> roleTypeColumn;
	public ObjectProperty<Person> person = new SimpleObjectProperty<>();
	public RoleOverviewController() {
		super(Role.class, true);
	}

	@Override
	protected void preCreate(Role object) {
		object.setPerson(person.get());
	}

	@Override
	protected Collection<Role> preLoad(EntityManager em) {
		if(person != null) {
			Hibernate.initialize(person.get().getRoles());
			return FXCollections.observableArrayList(person.get().getRoles());
		}
		return null;
	}

	@Override
	protected String getLoadingText() {
		return guiText.getString("loading") + " " + guiText.getString("roles") + " ...";
	}

	@Override
	protected void handleException(RollbackException e, Role approval) {
		RemoveDialog.showFailureAndWait("Funktion","Funktion '"+approval.getRoleType().getDescription()+"' von '"+approval.getPerson().getFullName()+"'", e);
	}

	@Override
	protected void postInit() {
		roleTypeColumn.setCellValueFactory(cellData -> cellData.getValue().roleTypeProperty());

		roleTypeColumn.setCellFactory(column -> new MetaClassTableCell<>());
	}
}
