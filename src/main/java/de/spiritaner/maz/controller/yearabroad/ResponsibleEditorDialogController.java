package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.controller.person.PersonOverviewController;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.dialog.OverviewDialog;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Responsible;
import de.spiritaner.maz.model.Role;
import de.spiritaner.maz.model.meta.RoleType;
import de.spiritaner.maz.util.DataDatabase;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/yearabroad/responsible_editor_dialog.fxml", objDesc = "Verantwortliche(n)")
public class ResponsibleEditorDialogController extends EditorController<Responsible> {

	final static Logger logger = Logger.getLogger(ResponsibleEditorDialogController.class);

	@FXML
	private Button saveResponsibleButton;
	@FXML
	private Text titleText;
	@FXML
	private GridPane personEditor;
	@FXML
	private PersonEditorController personEditorController;
	@FXML
	private GridPane responsibleEditor;
	@FXML
	private ResponsibleEditorController responsibleEditorController;

	@Override
	public void setIdentifiable(Responsible responsible) {
		super.setIdentifiable(responsible);

		if (responsible != null) {
			if(responsible.getSite() != null) {
				responsibleEditorController.setSite(responsible.getSite());
			}

			responsibleEditorController.setAll(responsible);

			if (responsible.getId() != 0L) {
				titleText.setText("Verantwortliche(n) bearbeiten");
				saveResponsibleButton.setText("Speichern");
			} else {
				titleText.setText("Verantwortliche(n) anlegen");
				saveResponsibleButton.setText("Anlegen");
			}
		}
	}

	public void saveResponsible(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			boolean personValid = personEditorController.isValid();
			boolean responsibleValid = responsibleEditorController.isValid();

			if (personValid && responsibleValid) {
				EntityManager em = DataDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				getIdentifiable().setPerson(personEditorController.getAll(getIdentifiable().getPerson()));
				responsibleEditorController.getAll(getIdentifiable());

				try {
					Responsible managedResponsible = (!em.contains(getIdentifiable())) ? em.merge(getIdentifiable()) : getIdentifiable();

					if(!managedResponsible.getSite().getResponsibles().contains(managedResponsible)) managedResponsible.getSite().getResponsibles().add(managedResponsible);

					// Automatically add the role 'Ansprechperson Einsatzstelle'
					RoleType siteResponsibleRoleType = em.createNamedQuery("RoleType.findByDesc", RoleType.class).setParameter("description","Ansprechperson Einsatzstelle").getSingleResult();
					if(siteResponsibleRoleType != null) {
						Hibernate.initialize(managedResponsible.getPerson().getRoles());
						if(managedResponsible.getPerson().getRoles() == null || !managedResponsible.getPerson().getRoles().contains(siteResponsibleRoleType)) {
							Role role = new Role();
							role.setPerson(managedResponsible.getPerson());
							role.setRoleType(siteResponsibleRoleType);
							em.merge(role);
						}
					}

					em.getTransaction().commit();
					setResult(managedResponsible);
					requestClose();
				} catch (PersistenceException e) {
					em.getTransaction().rollback();
					logger.warn(e);
				} finally {
					em.close();
				}
			}
		});
	}

	public void searchPerson(ActionEvent actionEvent) {
		OverviewDialog<PersonOverviewController, Person> dialog = new OverviewDialog<>(PersonOverviewController.class);
		Optional<Person> result = dialog.showAndWait(getStage());

		result.ifPresent((Person person) -> {
			getIdentifiable().setPerson(person);
			personEditorController.setAll(person);
			personEditorController.setReadonly(true);
		});
	}
}