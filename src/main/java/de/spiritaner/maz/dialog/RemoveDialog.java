package de.spiritaner.maz.dialog;

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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ein Fehler ist aufgetreten!");

        Throwable t = e.getCause();
        while ((t != null) && !(t instanceof ConstraintViolationException)) {
            t = t.getCause();
        }

        if (t instanceof ConstraintViolationException) {
            alert.setContentText("Der Löschvorgang würde die Datenintegrität verletzten. Möglicherweise verwendet ein anderes Objekt in der Datenbank diese Metadaten bereits.");
        } else {
            ExceptionDialog.show(e);
        }

        alert.initStyle(StageStyle.UTILITY);
        alert.setHeaderText(metaName+" '"+metaClassObj.getDescription()+"' konnte nicht gelöscht werden!");
        alert.showAndWait();
    }

    public static Alert create(MetaClass metaClassObj, String metaName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(metaName + " löschen");
        alert.setHeaderText(null);
        alert.initStyle(StageStyle.UTILITY);
        alert.setContentText(metaName + " '" + metaClassObj.getDescription() +"' wirklich löschen?");
        return alert;
    }

    public static Alert create(MetaClass metaClassObj, String metaName, Stage owner) {
        Alert alert = create(metaClassObj,metaName);
        alert.initOwner(owner);
        return alert;
    }

    public static Alert create(Person person, Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Person löschen");
        alert.setHeaderText(null);
        alert.initStyle(StageStyle.UTILITY);
        alert.setContentText("Person '" + person.getFullName() +"' wirklich löschen?");
        alert.initOwner(stage);
        return alert;
    }

	public static Alert create(ContactMethod selectedContactMethod, Stage stage) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Kontaktweg löschen");
		alert.setHeaderText(null);
		alert.initStyle(StageStyle.UTILITY);
		alert.setContentText(selectedContactMethod.getContactMethodType().getDescription() + " '" + selectedContactMethod.getValue() +"' wirklich löschen?");
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

    public static Alert create(Participant participant, Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Teilnehmer löschen");
        alert.setHeaderText(null);
        alert.initStyle(StageStyle.UTILITY);
        alert.setContentText("Teilnehmer '" + participant.getPerson().getFullName() + "' wirklich löschen?");
        alert.initOwner(stage);
        return alert;
    }
}
