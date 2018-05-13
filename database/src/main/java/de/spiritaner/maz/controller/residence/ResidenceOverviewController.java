package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Residence;
import de.spiritaner.maz.model.meta.ResidenceType;
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

public class ResidenceOverviewController extends OverviewController<Residence> {

	public TableColumn<Residence, ResidenceType> residenceTypeColumn;
	public TableColumn<Residence, String> preferredAddressColumn;
	public TableColumn<Residence, String> streetColumn;
	public TableColumn<Residence, String> houseNumberColumn;
	public TableColumn<Residence, String> postCodeColumn;
	public TableColumn<Residence, String> cityColumn;
	public TableColumn<Residence, String> stateColumn;
	public TableColumn<Residence, String> countryColumn;
	public TableColumn<Residence, String> additionColumn;
	public TableColumn<Residence, Boolean> forPostColumn;

	public ObjectProperty<Person> person = new SimpleObjectProperty<>();

	public ResidenceOverviewController() {
		super(Residence.class, true);
	}

	@Override
	protected void preCreate(Residence object) {
		object.setPerson(person.get());
		object.setAddress(new Address());
	}

	@Override
	protected Collection<Residence> preLoad(EntityManager em) {
		if(person.get() != null) {
			Hibernate.initialize(person.get().getResidences());
			person.get().initVolatiles();
			return FXCollections.observableArrayList(person.get().getResidences());
		}
		return null;
	}

	@Override
	protected String getLoadingText() {
		return guiText.getString("loading") + " " + guiText.getString("residences") + " ...";
	}

	@Override
	protected void handleException(RollbackException e, Residence residence) {
		String objName = guiText.getString("residence");
		RemoveDialog.showFailureAndWait(objName, objName, e);
	}

	@Override
	protected void preRemove(Residence residence, EntityManager em) {
		Person managedPerson = (em.contains(residence.getPerson())) ? residence.getPerson() : em.find(Person.class, residence.getPerson().getId());
		managedPerson.setPreferredResidence(null);
	}

	@Override
	protected void postInit() {
		residenceTypeColumn.setCellValueFactory(cellData -> cellData.getValue().residenceTypeProperty());
		preferredAddressColumn.setCellValueFactory(cellData -> cellData.getValue().preferredAddressStringProperty());
		streetColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().streetProperty());
		houseNumberColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().houseNumberProperty());
		postCodeColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().postCodeProperty());
		cityColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().cityProperty());
		stateColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().stateProperty());
		countryColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().countryProperty());
		additionColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().additionProperty());
		forPostColumn.setCellValueFactory(cellData -> cellData.getValue().forPostProperty());

		residenceTypeColumn.setCellFactory(column -> new MetaClassTableCell<>());
		forPostColumn.setCellFactory(column -> new BooleanTableCell<>());
	}

	@Override
	protected boolean isRemoveButtonDisabled(Residence oldVal, Residence newVal) {
		return newVal == null || (newVal.getId() < 0);
	}

	@Override
	protected boolean isEditButtonDisabled(Residence oldVal, Residence newVal) {
		return newVal == null || (newVal.getId() < 0);
	}
}
