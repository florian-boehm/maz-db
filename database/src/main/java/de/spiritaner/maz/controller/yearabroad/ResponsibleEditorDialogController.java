package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.controller.person.PersonOverviewController;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Responsible;
import de.spiritaner.maz.model.Role;
import de.spiritaner.maz.model.meta.RoleType;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.view.dialog.EditorDialog;
import de.spiritaner.maz.view.dialog.OverviewDialog;
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
import java.util.Optional;

@EditorDialog.Annotation(fxmlFile = "/fxml/yearabroad/responsible_editor_dialog.fxml", objDesc = "$responsible")
public class ResponsibleEditorDialogController extends EditorDialogController<Responsible> {

	final static Logger logger = Logger.getLogger(ResponsibleEditorDialogController.class);

	public GridPane personEditor;
	public PersonEditorController personEditorController;
	public GridPane responsibleEditor;
	public ResponsibleEditorController responsibleEditorController;
	public Button searchPersonButton;

	@Override
	protected void bind(Responsible responsible) {
		responsibleEditorController.site.bindBidirectional(identifiable.get().site);
		personEditorController.person.bindBidirectional(identifiable.get().person);
		personEditorController.readOnly.bind(identifiable.get().person.isNotNull());
		responsibleEditorController.responsible.bindBidirectional(identifiable);
	}

	@Override
	protected boolean allValid() {
		boolean personValid = personEditorController.isValid();
		boolean responsibleValid = responsibleEditorController.isValid();

		return personValid && responsibleValid;
	}

	@Override
	protected void preSave(Responsible managedResponsible, EntityManager em) {
		if(!managedResponsible.getSite().getResponsibles().contains(managedResponsible)) managedResponsible.getSite().getResponsibles().add(managedResponsible);

		// Automatically add the role 'Ansprechperson Einsatzstelle'
		//RoleType siteResponsibleRoleType = em.createNamedQuery("RoleType.findByDesc", RoleType.class).setParameter("description","Ansprechperson Einsatzstelle").getSingleResult();
		Hibernate.initialize(managedResponsible.getPerson().getRoles());
		final RoleType siteResponsibleRoleType = em.find(RoleType.class, 9L);

		if(siteResponsibleRoleType != null) {
			Hibernate.initialize(managedResponsible.getPerson().getRoles());

			if(managedResponsible.getPerson().getRoles() != null) {
				boolean roleAlreadyExists = false;

				for (Role role : managedResponsible.getPerson().getRoles()) {
					if (role.getRoleType().getId() == 9L) {
						roleAlreadyExists = true;
					}
				}

				if (!roleAlreadyExists) {
					Role role = new Role();
					role.setPerson(managedResponsible.getPerson());
					role.setRoleType(siteResponsibleRoleType);
					em.merge(role);
				}
			}
		}
	}

	public void searchPerson(ActionEvent actionEvent) {
		OverviewDialog<PersonOverviewController, Person> dialog = new OverviewDialog<>(PersonOverviewController.class);
		Optional<Person> result = dialog.showAndSelect(getStage());

		result.ifPresent((Person person) -> {
			getIdentifiable().setPerson(person);
			personEditorController.person.set(person);
			personEditorController.readOnly.set(true);
		});
	}
}
