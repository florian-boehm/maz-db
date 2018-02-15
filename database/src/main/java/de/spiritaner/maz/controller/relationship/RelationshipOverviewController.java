package de.spiritaner.maz.controller.relationship;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Relationship;
import de.spiritaner.maz.view.dialog.RemoveDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

public class RelationshipOverviewController extends OverviewController<Relationship> {

	@FXML
	private TableColumn<Relationship, String> relationshipTypeColumn;
	@FXML
	private TableColumn<Relationship, String> fromPersonColumn;
	@FXML
	private TableColumn<Relationship, String> toPersonColumn;

	private Person person;

	public RelationshipOverviewController() {
		super(Relationship.class, Boolean.TRUE);
	}

	@Override
	protected void preCreate(Relationship newRelationship) {
		newRelationship.setFromPerson(person);
	}

	@Override
	protected void postRemove(Relationship obsoleteEntity) {
		person.getRelationships().remove(obsoleteEntity);
	}

	@Override
	protected Collection<Relationship> preLoad(EntityManager em) {
		if(person != null) {
			Hibernate.initialize(person.getRelationships());
			return FXCollections.observableArrayList(person.getRelationships());
		} else {
			return FXCollections.emptyObservableList();
		}
	}

	@Override
	protected String getLoadingText() {
		return "Lade Beziehungen ...";
	}

	@Override
	protected void handleException(RollbackException e, Relationship relationship) {
		// TODO choose better text here
		RemoveDialog.showFailureAndWait("Beziehung","Beziehung", e);
	}

	@Override
	protected void postInit() {
		relationshipTypeColumn.setCellValueFactory(cellData -> cellData.getValue().getRelationshipType().descriptionProperty());
		toPersonColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getToPersonFullName()));
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}
