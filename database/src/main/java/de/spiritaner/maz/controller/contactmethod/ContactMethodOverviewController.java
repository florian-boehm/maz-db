package de.spiritaner.maz.controller.contactmethod;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.model.ContactMethod;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.meta.ContactMethodType;
import de.spiritaner.maz.view.dialog.RemoveDialog;
import de.spiritaner.maz.view.renderer.BooleanTableCell;
import de.spiritaner.maz.view.renderer.MetaClassTableCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

public class ContactMethodOverviewController extends OverviewController<ContactMethod> {

	public TableColumn<ContactMethod, Boolean> preferredColumn;
	public TableColumn<ContactMethod, String> remarkColumn;
	public TableColumn<ContactMethod, ContactMethodType> contactMethodTypeColumn;
	public TableColumn<ContactMethod, String> valueColumn;

	public ObjectProperty<Person> person = new SimpleObjectProperty<>();

	public ContactMethodOverviewController() {
		super(ContactMethod.class, Boolean.TRUE);
	}

	@Override
	protected void preCreate(ContactMethod newContactMethod) {
		newContactMethod.setPerson(person.get());
	}

	@Override
	protected void postRemove(ContactMethod obsoleteEntity) {
		person.get().getContactMethods().remove(obsoleteEntity);
	}

	@Override
	protected Collection<ContactMethod> preLoad(EntityManager em) {
		if(person.get() != null) {
			Hibernate.initialize(person.get().getContactMethods());
			return FXCollections.observableArrayList(person.get().getContactMethods());
		} else {
			return FXCollections.emptyObservableList();
		}
	}

	@Override
	// TODO Extract strings
	protected String getLoadingText() {
		return "Lade Kontaktwege ...";
	}

	@Override
	// TODO Extract strings
	protected void handleException(RollbackException e, ContactMethod contactMethod) {
		RemoveDialog.showFailureAndWait("Kontaktweg","Kontaktweg '"+contactMethod.getValue()+"'", e);
	}

	@Override
	protected void postInit() {
		contactMethodTypeColumn.setCellValueFactory(cellData -> cellData.getValue().contactMethodTypeProperty());
		valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
		preferredColumn.setCellValueFactory(cellData -> cellData.getValue().preferredProperty());
		remarkColumn.setCellValueFactory(cellData -> cellData.getValue().remarkProperty());

		contactMethodTypeColumn.setCellFactory(column -> new MetaClassTableCell<>());
		preferredColumn.setCellFactory(column -> new BooleanTableCell<>());
	}
}
