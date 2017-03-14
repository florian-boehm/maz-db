package de.spiritaner.maz.controller.person;

import de.spiritaner.maz.controller.Controller;
import de.spiritaner.maz.controller.approval.ApprovalOverviewController;
import de.spiritaner.maz.controller.contactmethod.ContactMethodOverviewController;
import de.spiritaner.maz.controller.event.EventOverviewController;
import de.spiritaner.maz.controller.residence.ResidenceOverviewController;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.dialog.RemoveDialog;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.util.DataDatabase;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.ToggleSwitch;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PersonPageController implements Initializable, Controller {

	private static final Logger logger = Logger.getLogger(PersonPageController.class);

	@FXML
	private TabPane detailTabPane;

	@FXML
	private AnchorPane personOverview;
	@FXML
	private PersonOverviewController personOverviewController;
	@FXML
	private AnchorPane detailPane;

	@FXML
	private MaskerPane detailsMasker;
	@FXML
	private SplitPane personSplitPane;

	@FXML
	private AnchorPane personResidences;
	@FXML
	private ResidenceOverviewController personResidencesController;

	@FXML
	private AnchorPane personContactMethods;
	@FXML
	private ContactMethodOverviewController personContactMethodsController;

	@FXML
	private AnchorPane personEvents;
	@FXML
	private EventOverviewController personEventsController;

	@FXML
	private AnchorPane personApprovals;
	@FXML
	private ApprovalOverviewController personApprovalsController;

	private Stage stage;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		personOverviewController.getPersonTable().getSelectionModel().selectedItemProperty().addListener((observable, oldPerson, newPerson) -> {
			if (newPerson != null && personOverviewController.getPersonDetailsToggle().isSelected()) {
				loadPersonDetails(newPerson);
			} else if (newPerson == null) {
				detailsMasker.setVisible(true);
			}
		});

		personOverviewController.getPersonTable().setRowFactory(tv -> {
			TableRow<Person> row = new TableRow<>();

			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					Person selectedPerson = row.getItem();
					loadPersonDetails(selectedPerson);
				}
			});

			return row;
		});

		detailTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
			reloadSpecificTab(newTab.getId());
		});
	}

	private void reloadSpecificTab(String tabFxId) {
		switch(tabFxId) {
			case "residenceTab": personResidencesController.onReopen(); break;
			case "approvalTab": personApprovalsController.onReopen(); break;
			case "contactMethodTab": personContactMethodsController.onReopen(); break;
		}
	}

	private void loadPersonDetails(Person person) {
		Platform.runLater(() -> {
			// TODO implement person history display
			//AuditReader reader = AuditReaderFactory.get(DataDatabase.getFactory().createEntityManager());
			//List<Number> revisions = reader.getRevisions(Person.class, person.getId());

			/*for (Number revision : revisions) {
				logger.info("Found revision " + revision + " for person with first name " + person.getFirstName());
				Person revPerson = reader.find(Person.class, person.getId(), revision);
				logger.info("First in this revision was: " + revPerson.getFirstName());
			}*/

			personResidencesController.setPerson(person);
			personContactMethodsController.setPerson(person);
			personApprovalsController.setPerson(person);

			detailsMasker.setVisible(false);

			reloadSpecificTab(detailTabPane.getSelectionModel().getSelectedItem().getId());
		});
	}

	public void setStage(Stage stage) {
		this.stage = stage;

		personOverviewController.setStage(stage);
		personResidencesController.setStage(stage);
		//personEventsController.setStage(stage);
		personContactMethodsController.setStage(stage);
	}

	@Override
	public void onReopen() {
		personOverviewController.onReopen();
	}
}
