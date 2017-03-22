package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Residence;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

public class ResidenceOverviewController extends OverviewController<Residence> {

	@FXML
	private TableColumn<Residence, String> residenceTypeColumn;
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
			return FXCollections.observableArrayList(person.getResidences());
		}
		return null;
	}

	@Override
	protected String getLoadingText() {
		return "Lade Wohnorte ...";
	}

	@Override
	protected void handleException(RollbackException e) {
		ExceptionDialog.show(e);
	}

	@Override
	protected void postInit() {
		residenceTypeColumn.setCellValueFactory(cellData -> cellData.getValue().getResidenceType().descriptionProperty());
		preferredAddressColumn.setCellValueFactory(cellData -> cellData.getValue().preferredAddressProperty());
		streetColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().streetProperty());
		houseNumberColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().houseNumberProperty());
		postCodeColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().postCodeProperty());
		cityColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().cityProperty());
		stateColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().stateProperty());
		countryColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().countryProperty());
		additionColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().additionProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}
