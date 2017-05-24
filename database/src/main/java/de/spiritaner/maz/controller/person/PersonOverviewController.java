package de.spiritaner.maz.controller.person;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.dialog.RemoveDialog;
import de.spiritaner.maz.model.Approval;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.meta.*;
import de.spiritaner.maz.util.database.DataDatabase;
import de.spiritaner.maz.util.factory.DateAsStringListCell;
import de.spiritaner.maz.util.factory.MetaClassTableCell;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@OverviewController.Annotation(fxmlFile = "/fxml/person/person_overview.fxml", objDesc = "Person")
public class PersonOverviewController extends OverviewController<Person> {

	private static final Logger logger = Logger.getLogger(PersonOverviewController.class);

	@FXML
	private TableColumn<Person, String> firstNameColumn;
	@FXML
	private TableColumn<Person, String> familyNameColumn;
	@FXML
	private TableColumn<Person, String> birthNameColumn;
	@FXML
	private TableColumn<Person, String> birthplaceColumn;
	@FXML
	private TableColumn<Person, Long> idColumn;
	@FXML
	private TableColumn<Person, LocalDate> birthdayColumn;
	@FXML
	private TableColumn<Person, LocalDate> ageColumn;
	@FXML
	private TableColumn<Person, Gender> genderColumn;
	@FXML
	private TableColumn<Person, Diocese> dioceseColumn;
	@FXML
	private TableColumn<Person, Religion> religionColumn;
	@FXML
	private TableColumn<Person, String> honorificColumn;
	@FXML
	private TableColumn<Person, Salutation> salutationColumn;
	@FXML
	private ToggleSwitch personDetailsToggle;
	@FXML
	private Button personSearchButton;
	@FXML
	private TextField personSearchText;

	public PersonOverviewController() {
		super(Person.class, true);
	}

	@Override
	protected void postInit() {
		firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
		familyNameColumn.setCellValueFactory(cellData -> cellData.getValue().familyNameProperty());
		birthNameColumn.setCellValueFactory(cellData -> cellData.getValue().birthNameProperty());
		genderColumn.setCellValueFactory(cellData -> cellData.getValue().genderProperty());
		dioceseColumn.setCellValueFactory(cellData -> cellData.getValue().dioceseProperty());
		birthdayColumn.setCellValueFactory(cellData -> cellData.getValue().birthdayProperty());
		ageColumn.setCellValueFactory(cellData -> cellData.getValue().birthdayProperty());
		birthplaceColumn.setCellValueFactory(cellData -> cellData.getValue().birthplaceProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
		honorificColumn.setCellValueFactory(cellData -> cellData.getValue().honorificProperty());
		salutationColumn.setCellValueFactory(cellData -> cellData.getValue().salutationProperty());
		religionColumn.setCellValueFactory(cellData -> cellData.getValue().religionProperty());

		genderColumn.setCellFactory(column -> new MetaClassTableCell<>());
		dioceseColumn.setCellFactory(column -> new MetaClassTableCell<>());
		religionColumn.setCellFactory(column -> new MetaClassTableCell<>());
		salutationColumn.setCellFactory(column -> new MetaClassTableCell<>());
		birthdayColumn.setCellFactory(column -> DateAsStringListCell.localDateTableCell());
		ageColumn.setCellFactory(column -> DateAsStringListCell.localDateTableCellYearsToNow());

		personSearchButton.setDefaultButton(true);
	}

	@Override
	protected Collection<Person> preLoad(EntityManager em) {
		return em.createNamedQuery("Person.findAll", Person.class).getResultList();
	}

	@Override
	protected String getLoadingText() {
		return "Lade Personen ...";
	}

	@Override
	protected void handleException(RollbackException e, Person selectedPerson) {
		RemoveDialog.showFailureAndWait("Person","Person ("+selectedPerson.getFullName()+")", e);
	}

	@Override
	protected void postCreate(Person person) {
		EntityManager em = DataDatabase.getFactory().createEntityManager();
		em.getTransaction().begin();

		// Get a managed copy of the person
		person = (em.contains(person)) ? person : em.merge(person);

		// Create the three default approvals when a person is created
		for(int approvalTypeId = 1; approvalTypeId <= 3; approvalTypeId++) {
			Approval approval = new Approval();
			approval.setPerson(person);
			approval.setApprovalType(em.find(ApprovalType.class, Long.valueOf(approvalTypeId)));
			approval.setApproved(false);

			// Persist approval before
			em.persist(approval);
			person.getApprovals().add(approval);
		}

		em.getTransaction().commit();
	}

	public ToggleSwitch getPersonDetailsToggle() {
		return personDetailsToggle;
	}

	@SuppressWarnings("unchecked")
	public void searchForPersons(ActionEvent actionEvent) {
		// TODO implement full text search
		EntityManager em = DataDatabase.getFactory().createEntityManager();
		FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
		em.getTransaction().begin();

		// create native Lucene query unsing the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Person.class).get();
		org.apache.lucene.search.Query luceneQuery = qb.keyword().onFields("firstName", "familyName")
				  .matching(personSearchText.getText()+"*")
				  .createQuery();

		// wrap Lucene query in a javax.persistence.Query
		javax.persistence.Query jpaQuery =
				  fullTextEntityManager.createFullTextQuery(luceneQuery, Person.class);

		// execute search
		List<Person> result = jpaQuery.getResultList();

		getTable().getItems().clear();
		getTable().getItems().addAll(FXCollections.observableArrayList(result));

		em.getTransaction().commit();
	}
}
