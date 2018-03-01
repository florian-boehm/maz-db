package de.spiritaner.maz.controller.person;

import de.spiritaner.maz.controller.Controller;
import de.spiritaner.maz.controller.approval.ApprovalOverviewController;
import de.spiritaner.maz.controller.contactmethod.ContactMethodOverviewController;
import de.spiritaner.maz.controller.experienceabroad.ExperienceAbroadOverviewController;
import de.spiritaner.maz.controller.participation.ParticipationOverviewController;
import de.spiritaner.maz.controller.relationship.RelationshipOverviewController;
import de.spiritaner.maz.controller.residence.ResidenceOverviewController;
import de.spiritaner.maz.controller.role.RoleOverviewController;
import de.spiritaner.maz.controller.yearabroad.YearAbroadOverviewController;
import de.spiritaner.maz.model.Person;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.controlsfx.control.MaskerPane;

import java.net.URL;
import java.util.ResourceBundle;

public class PersonPageController implements Initializable, Controller {

	private static final Logger logger = Logger.getLogger(PersonPageController.class);

	public TabPane detailTabPane;

	public AnchorPane personOverview;
	public PersonOverviewController personOverviewController;
	public AnchorPane detailPane;

	public MaskerPane detailsMasker;
	public SplitPane personSplitPane;

	public AnchorPane personResidences;
	public ResidenceOverviewController personResidencesController;

	public AnchorPane personContactMethods;
	public ContactMethodOverviewController personContactMethodsController;

	public AnchorPane personParticipations;
	public ParticipationOverviewController personParticipationsController;

	public AnchorPane personApprovals;
	public ApprovalOverviewController personApprovalsController;

	public AnchorPane personRoles;
	public RoleOverviewController personRolesController;

	public AnchorPane personRelationships;
	public RelationshipOverviewController personRelationshipsController;

	public AnchorPane personYearsAbroad;
	public YearAbroadOverviewController personYearsAbroadController;

	public AnchorPane personExperiencesAbroad;
	public ExperienceAbroadOverviewController personExperiencesAbroadController;

	private Stage stage;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		personOverviewController.getTable().getSelectionModel().selectedItemProperty().addListener((observable, oldPerson, newPerson) -> {
			if (newPerson != null /*&& personOverviewController.getPersonDetailsToggle().isSelected()*/) {
				loadPersonDetails(newPerson);
			} else if (newPerson == null) {
				detailsMasker.setVisible(true);
			}
		});

		/*personOverviewController.getTable().setRowFactory(tv -> {
			TableRow<Person> row = new TableRow<>();

			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					Person selectedPerson = row.getItem();
					loadPersonDetails(selectedPerson);
				}
			});

			return row;
		});*/

		detailTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
			reloadSpecificTab(newTab.getId());
		});
	}

	private void reloadSpecificTab(String tabFxId) {
		switch(tabFxId) {
			case "residenceTab": personResidencesController.onReopen(); break;
			case "approvalTab": personApprovalsController.onReopen(); break;
			case "contactMethodTab": personContactMethodsController.onReopen(); break;
			case "participationTab": personParticipationsController.onReopen(); break;
			case "roleTab": personRolesController.onReopen(); break;
			case "relationshipTab": personRelationshipsController.onReopen(); break;
			case "yearAbroadTab": personYearsAbroadController.onReopen(); break;
			case "experienceAbroadTab": personExperiencesAbroadController.onReopen(); break;
		}
	}

	private void loadPersonDetails(Person person) {
		Platform.runLater(() -> {
			personResidencesController.person.set(person);
			personContactMethodsController.person.set(person);
			personApprovalsController.person.set(person);
			personParticipationsController.setPerson(person);
			personRolesController.person.set(person);
			personRelationshipsController.person.set(person);
			personYearsAbroadController.setPerson(person);
			personExperiencesAbroadController.person.set(person);

			detailsMasker.setVisible(false);

			reloadSpecificTab(detailTabPane.getSelectionModel().getSelectedItem().getId());
		});
	}

	public void setStage(Stage stage) {
		this.stage = stage;

		personOverviewController.setStage(stage);
		personResidencesController.setStage(stage);
		personParticipationsController.setStage(stage);
		personContactMethodsController.setStage(stage);
		personRolesController.setStage(stage);
		personRelationshipsController.setStage(stage);
		personYearsAbroadController.setStage(stage);
		personExperiencesAbroadController.setStage(stage);
	}

	@Override
	public void onReopen() {
		personOverviewController.onReopen();
	}
}
