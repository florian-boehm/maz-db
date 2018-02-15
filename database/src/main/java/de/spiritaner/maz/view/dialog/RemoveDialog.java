package de.spiritaner.maz.view.dialog;

import de.spiritaner.maz.model.*;
import de.spiritaner.maz.model.meta.MetaClass;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Optional;

public class RemoveDialog {

	public static Optional<ButtonType> showAndWait(MetaClass metaClassObj, String metaName) {
		return create(metaClassObj, metaName).showAndWait();
	}

	public static void showFailureAndWait(MetaClass metaClassObj, String metaName, Exception e) {
		RemoveDialog.showFailureAndWait("Metadaten", metaName + " '" + metaClassObj.getDescription()+"'",e);
	}

	public static void showFailureAndWait(String objName, String headerPrefix, Exception e) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Ein Fehler ist aufgetreten!");

		Throwable t = e.getCause();
		while ((t != null) && !(t instanceof ConstraintViolationException)) {
			t = t.getCause();
		}

		if (t instanceof ConstraintViolationException) {
			alert.setContentText("Der Löschvorgang würde die Datenintegrität verletzten. Möglicherweise verwendet ein anderes Objekt in der Datenbank diese "+objName+" bereits.");
		} else {
			ExceptionDialog.show(e);
		}

		alert.initStyle(StageStyle.UTILITY);
		alert.setHeaderText(headerPrefix + " konnte nicht gelöscht werden!");
		alert.showAndWait();
	}

	public static Alert create(MetaClass metaClassObj, String metaName) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle(metaName + " löschen");
		alert.setHeaderText(null);
		alert.initStyle(StageStyle.UTILITY);
		alert.setContentText(metaName + " '" + metaClassObj.getDescription() + "' wirklich löschen?");
		return alert;
	}

	public static Alert create(MetaClass metaClassObj, String metaName, Stage owner) {
		Alert alert = create(metaClassObj, metaName);
		alert.initOwner(owner);
		return alert;
	}

	public static Alert create(Person person, Stage stage) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Person löschen");
		alert.setHeaderText(null);
		alert.initStyle(StageStyle.UTILITY);
		alert.setContentText("Person '" + person.getFullName() + "' wirklich löschen?");
		alert.initOwner(stage);
		return alert;
	}

	public static Alert create(ContactMethod selectedContactMethod, Stage stage) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Kontaktweg löschen");
		alert.setHeaderText(null);
		alert.initStyle(StageStyle.UTILITY);
		alert.setContentText(selectedContactMethod.getContactMethodType().getDescription() + " '" + selectedContactMethod.getValue() + "' wirklich löschen?");
		alert.initOwner(stage);
		return alert;
	}

	public static Alert create(Approval approval, Stage stage) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Einwilligung löschen");
		alert.setHeaderText(null);
		alert.initStyle(StageStyle.UTILITY);
		alert.setContentText("Einwilligung zur/zum " + approval.getApprovalType().getDescription() + " wirklich löschen?");
		alert.initOwner(stage);
		return alert;
	}

	public static Alert create(Event event, Stage stage) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Veranstaltung löschen");
		alert.setHeaderText(null);
		alert.initStyle(StageStyle.UTILITY);
		alert.setContentText("Veranstaltung '" + event.getName() + "' wirklich löschen?");
		alert.initOwner(stage);
		return alert;
	}

	public static Alert create(Participation participation, Stage stage) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Teilnehmer löschen");
		alert.setHeaderText(null);
		alert.initStyle(StageStyle.UTILITY);
		alert.setContentText("Teilnehmer '" + participation.getPerson().getFullName() + "' wirklich löschen?");
		alert.initOwner(stage);
		return alert;
	}

	public static Alert create(Relationship relationship, Stage stage) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Beziehung löschen");
		alert.setHeaderText(null);
		alert.initStyle(StageStyle.UTILITY);
		alert.setContentText("Beziehung von '" + relationship.getFromPerson().getFullName() + "' zu '" + relationship.getToPersonFullName() + "' wirklich löschen?");
		alert.initOwner(stage);
		return alert;
	}

	public static Alert create(EPNumber epNumber, Stage stage) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("EP-Nummer löschen");
		alert.setHeaderText(null);
		alert.initStyle(StageStyle.UTILITY);
		alert.setContentText("EP-Nummer '" + epNumber.getNumber() + "' wirklich löschen?");
		alert.initOwner(stage);
		return alert;
	}

	public static Alert create(Site site, Stage stage) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Einsatzstelle löschen");
		alert.setHeaderText(null);
		alert.initStyle(StageStyle.UTILITY);
		alert.setContentText("Einsatzstelle '" + site.getName() + "' wirklich löschen?");
		alert.initOwner(stage);
		return alert;
	}

	public static Alert create(Residence site, Stage stage) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Wohnort löschen");
		alert.setHeaderText(null);
		alert.initStyle(StageStyle.UTILITY);
		alert.setContentText("Wohnort '" + site.getAddress().getStreet() + " " + site.getAddress().getHouseNumber() + "' wirklich löschen?");
		alert.initOwner(stage);
		return alert;
	}

	public static Alert create(Responsible responsible, Stage stage) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Verantwortliche(n) löschen");
		alert.setHeaderText(null);
		alert.initStyle(StageStyle.UTILITY);
		alert.setContentText("Verantwortliche(n) '" + responsible.getPerson().getFullName() + "' von der Einsatzstelle '" + responsible.getSite().getName() + "' wirklich löschen?");
		alert.initOwner(stage);
		return alert;
	}

	public static Alert create(Role role, Stage stage) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Funktion löschen");
		alert.setHeaderText(null);
		alert.initStyle(StageStyle.UTILITY);
		alert.setContentText("Funktion '" + role.getRoleType().getDescription() + "' von Person '" + role.getPerson().getFullName() + "' wirklich löschen?");
		alert.initOwner(stage);
		return alert;
	}

	public static Alert create(YearAbroad yearAbroad, Stage stage) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Auslandsjahr löschen");
		alert.setHeaderText(null);
		alert.initStyle(StageStyle.UTILITY);
		alert.setContentText("Auslandsjahr bei '" + yearAbroad.getSite().getName() + "' von Person '" + yearAbroad.getPerson().getFullName() + "' wirklich löschen?");
		alert.initOwner(stage);
		return alert;
	}

	public static Alert create(ExperienceAbroad experienceAbroad, Stage stage) {
		return generate(experienceAbroad, stage, "bei '" + experienceAbroad.getCommunity() + "' von Person '" + experienceAbroad.getPerson().getFullName() + "'");
	}

	public static Alert create(User user, Stage stage) {
		return generate(user, stage, "");
	}

	private static Alert generate(Identifiable identifiable, Stage stage, String details) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		Identifiable.Annotation annotation = identifiable.getClass().getAnnotation(Identifiable.Annotation.class);
		alert.setTitle(annotation.identifiableName() + " löschen");
		alert.setHeaderText(null);
		alert.initStyle(StageStyle.UTILITY);
		alert.setContentText(annotation.identifiableName() + ((details.isEmpty()) ? "" : " " + details) + " wirklich löschen?");
		alert.initOwner(stage);
		return alert;
	}
}
