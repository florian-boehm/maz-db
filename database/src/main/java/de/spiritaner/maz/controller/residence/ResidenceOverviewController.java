package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.dialog.RemoveDialog;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Residence;
import de.spiritaner.maz.model.YearAbroad;
import de.spiritaner.maz.model.meta.ResidenceType;
import de.spiritaner.maz.util.factory.MetaClassTableCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class ResidenceOverviewController extends OverviewController<Residence> {

	@FXML
	private TableColumn<Residence, ResidenceType> residenceTypeColumn;
	@FXML
	private TableColumn<Residence, String> preferredAddressColumn;
	@FXML
	private TableColumn<Residence, String> streetColumn;
	@FXML
	private TableColumn<Residence, String> houseNumberColumn;
	@FXML
	private TableColumn<Residence, String> postCodeColumn;
	@FXML
	private TableColumn<Residence, String> cityColumn;
	@FXML
	private TableColumn<Residence, String> stateColumn;
	@FXML
	private TableColumn<Residence, String> countryColumn;
	@FXML
	private TableColumn<Residence, String> additionColumn;
	@FXML
	private TableColumn<Residence, Long> idColumn;

	private Person person;

	public ResidenceOverviewController() {
		super(Residence.class, true);
	}

	@Override
	protected void preCreate(Residence object) {
		object.setPerson(person);
	}

	@Override
	protected Collection<Residence> preLoad(EntityManager em) {
		if(person != null) {
			Hibernate.initialize(person.getResidences());
			person.initVolatiles();
			return FXCollections.observableArrayList(person.getResidences());
		}
		return null;
	}

	@Override
	protected String getLoadingText() {
		return "Lade Wohnorte ...";
	}

	@Override
	protected void handleException(RollbackException e, Residence residence) {
		RemoveDialog.showFailureAndWait("Wohnort","Wohnort", e);
	}

	@Override
	protected void preRemove(Residence residence, EntityManager em) {
		Person managedPerson = (em.contains(residence.getPerson())) ? residence.getPerson() : em.find(Person.class, residence.getPerson().getId());
		managedPerson.setPreferredResidence(null);
	}

	@Override
	protected void postInit() {
		residenceTypeColumn.setCellValueFactory(cellData -> cellData.getValue().residenceTypeProperty());
		preferredAddressColumn.setCellValueFactory(cellData -> cellData.getValue().preferredAddressProperty());
		streetColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().streetProperty());
		houseNumberColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().houseNumberProperty());
		postCodeColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().postCodeProperty());
		cityColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().cityProperty());
		stateColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().stateProperty());
		countryColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().countryProperty());
		additionColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().additionProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

		residenceTypeColumn.setCellFactory(column -> new MetaClassTableCell<>());
	}

	@Override
	protected boolean isRemoveButtonDisabled(Residence oldVal, Residence newVal) {
		return newVal == null || (newVal.getId() < 0);
	}

	@Override
	protected boolean isEditButtonDisabled(Residence oldVal, Residence newVal) {
		return newVal == null || (newVal.getId() < 0);
	}


	public void setPerson(Person person) {
		this.person = person;
	}
}
