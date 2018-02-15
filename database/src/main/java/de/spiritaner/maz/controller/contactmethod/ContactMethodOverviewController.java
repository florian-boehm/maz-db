package de.spiritaner.maz.controller.contactmethod;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.model.ContactMethod;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.meta.ContactMethodType;
import de.spiritaner.maz.view.dialog.RemoveDialog;
import de.spiritaner.maz.view.renderer.BooleanTableCell;
import de.spiritaner.maz.view.renderer.MetaClassTableCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

public class ContactMethodOverviewController extends OverviewController<ContactMethod> {

	@FXML
	private TableColumn<ContactMethod, Boolean> preferredColumn;
	@FXML
	private TableColumn<ContactMethod, String> remarkColumn;
	@FXML
	private TableColumn<ContactMethod, ContactMethodType> contactMethodTypeColumn;
	@FXML
	private TableColumn<ContactMethod, String> valueColumn;

	private Person person;

	public ContactMethodOverviewController() {
		super(ContactMethod.class, Boolean.TRUE);
	}

	@Override
	protected void preCreate(ContactMethod newContactMethod) {
		newContactMethod.setPerson(person);
	}

	@Override
	protected void postRemove(ContactMethod obsoleteEntity) {
		person.getContactMethods().remove(obsoleteEntity);
	}

	@Override
	protected Collection<ContactMethod> preLoad(EntityManager em) {
		if(person != null) {
			Hibernate.initialize(person.getContactMethods());
			return FXCollections.observableArrayList(person.getContactMethods());
		} else {
			return FXCollections.emptyObservableList();
		}
	}

	@Override
	protected String getLoadingText() {
		return "Lade Kontaktwege ...";
	}

	@Override
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

	public void setPerson(Person person) {
		this.person = person;
	}
}
