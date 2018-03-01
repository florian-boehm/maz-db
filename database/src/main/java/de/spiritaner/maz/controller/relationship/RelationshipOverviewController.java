package de.spiritaner.maz.controller.relationship;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Relationship;
import de.spiritaner.maz.view.dialog.RemoveDialog;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

public class RelationshipOverviewController extends OverviewController<Relationship> {

	public TableColumn<Relationship, String> relationshipTypeColumn;
	public TableColumn<Relationship, String> fromPersonColumn;
	public TableColumn<Relationship, String> toPersonColumn;

	public ObjectProperty<Person> person = new SimpleObjectProperty<>();

	public RelationshipOverviewController() {
		super(Relationship.class, Boolean.TRUE);
	}

	@Override
	protected void preCreate(Relationship newRelationship) {
		newRelationship.setFromPerson(person.get());
	}

	@Override
	protected void postRemove(Relationship obsoleteEntity) {
		person.get().getRelationships().remove(obsoleteEntity);
	}

	@Override
	protected Collection<Relationship> preLoad(EntityManager em) {
		if(person != null) {
			Hibernate.initialize(person.get().getRelationships());
			return FXCollections.observableArrayList(person.get().getRelationships());
		} else {
			return FXCollections.emptyObservableList();
		}
	}

	@Override
	protected String getLoadingText() {
		return guiText.getString("loading") + " " + guiText.getString("relationships") + " ...";
	}

	@Override
	protected void handleException(RollbackException e, Relationship relationship) {
		// TODO choose better text here
		String objName = guiText.getString("relationship");
		RemoveDialog.showFailureAndWait(objName, objName, e);
	}

	@Override
	protected void postInit() {
		relationshipTypeColumn.setCellValueFactory(cellData -> cellData.getValue().getRelationshipType().descriptionProperty());
		toPersonColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getToPersonFullName()));
	}
}
