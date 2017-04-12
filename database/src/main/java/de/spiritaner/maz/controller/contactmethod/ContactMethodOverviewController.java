package de.spiritaner.maz.controller.contactmethod;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.model.ContactMethod;
import de.spiritaner.maz.model.Person;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

public class ContactMethodOverviewController extends OverviewController<ContactMethod> {

	@FXML
	private TableColumn<ContactMethod, String> preferredColumn;
	@FXML
	private TableColumn<ContactMethod, String> remarkColumn;
	@FXML
	private TableColumn<ContactMethod, String> contactMethodTypeColumn;
	@FXML
	private TableColumn<ContactMethod, String> valueColumn;
	@FXML
	private TableColumn<ContactMethod, Long> idColumn;

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
	protected void handleException(RollbackException e) {
		ExceptionDialog.show(e);
	}

	@Override
	protected void postInit() {
		contactMethodTypeColumn.setCellValueFactory(cellData -> cellData.getValue().getContactMethodType().descriptionProperty());
		valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
		preferredColumn.setCellValueFactory(cellData -> cellData.getValue().preferredContactMethodProperty());
		remarkColumn.setCellValueFactory(cellData -> cellData.getValue().remarkProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}
