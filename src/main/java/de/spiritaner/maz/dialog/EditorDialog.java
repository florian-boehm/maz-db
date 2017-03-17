package de.spiritaner.maz.dialog;

import de.spiritaner.maz.controller.Controller;
import de.spiritaner.maz.controller.approval.ApprovalEditorDialogController;
import de.spiritaner.maz.controller.contactmethod.ContactMethodEditorDialogController;
import de.spiritaner.maz.controller.participant.EventEditorDialogController;
import de.spiritaner.maz.controller.participant.ParticipantEditorDialogController;
import de.spiritaner.maz.controller.person.PersonEditorDialogController;
import de.spiritaner.maz.controller.residence.AddressEditorDialogController;
import de.spiritaner.maz.controller.residence.ResidenceEditorDialogController;
import de.spiritaner.maz.model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class EditorDialog<T extends Controller> {

	private final Identifiable identifiable;
	private final String identifiableName;
	private final Stage stage;
	private final Stage parent;
	private final String fxmlFile;
	private final FXMLLoader loader;
	private final Parent root;
	private final T controller;

	private EditorDialog(Stage parent, String fxmlFile, Identifiable identifiable, String identfiableName) throws IOException {
		this.parent = parent;
		this.fxmlFile = fxmlFile;
		this.identifiable = identifiable;
		this.identifiableName = identfiableName;
		this.loader = new FXMLLoader(Scene.class.getClass().getResource(fxmlFile));
		this.root = loader.load();
		this.controller = loader.getController();
		this.stage = new Stage();
		stage.setTitle((identifiable == null || identifiable.getId() == 0L) ? identifiableName + " anlegen" : identifiableName + " bearbeiten");
		stage.initOwner(parent);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(true);
		stage.setScene(new Scene(root));
		stage.sizeToScene();

		// TODO Seems to work correctly on windows, but not on linux
		stage.setOnShown(event -> {
			stage.sizeToScene();
			stage.setMaxHeight(stage.getHeight());
			stage.setMinHeight(stage.getHeight());
			stage.setMinWidth(stage.getWidth());
		});
	}

	public void showAndWait() {
		initController(controller, stage);
		stage.showAndWait();
	}

	public static ContactMethod showAndWait(final ContactMethod contactMethod, final Stage parent) {
		try {
			EditorDialog<ContactMethodEditorDialogController> editorDialog = new EditorDialog<ContactMethodEditorDialogController>(parent, "/fxml/contactmethod/contactmethod_editor_dialog.fxml", contactMethod, "Kontaktweg") {
				@Override
				public void initController(final ContactMethodEditorDialogController controller, final Stage stage) {
					controller.setContactMethod(contactMethod);
					controller.setStage(stage);
				}
			};
			editorDialog.showAndWait();
		} catch (IOException e) {
			ExceptionDialog.show(e);
		}

		return contactMethod;
	}

	public static void showAndWait(final Address address, final Stage parent) {
		try {
			EditorDialog<AddressEditorDialogController> editorDialog = new EditorDialog<AddressEditorDialogController>(parent, "/fxml/residence/address_editor_dialog.fxml", address, "Adresse") {
				@Override
				public void initController(final AddressEditorDialogController controller, final Stage stage) {
					controller.setAddress(address);
					controller.setStage(stage);
				}
			};
			editorDialog.showAndWait();
		} catch (IOException e) {
			ExceptionDialog.show(e);
		}
	}

	public static void showAndWait(final Person person, final Stage parent) {
		try {
			EditorDialog<PersonEditorDialogController> editorDialog = new EditorDialog<PersonEditorDialogController>(parent, "/fxml/person/person_editor_dialog.fxml", person, "Person") {
				@Override
				public void initController(final PersonEditorDialogController controller, final Stage stage) {
					controller.setPerson(person);
					controller.setStage(stage);
				}
			};
			editorDialog.showAndWait();
		} catch (IOException e) {
			ExceptionDialog.show(e);
		}
	}

	public static void showAndWait(final Residence residence, final Stage parent) {
		try {
			EditorDialog<ResidenceEditorDialogController> editorDialog = new EditorDialog<ResidenceEditorDialogController>(parent, "/fxml/residence/residence_editor_dialog.fxml", residence, "Wohnort") {
				@Override
				public void initController(final ResidenceEditorDialogController controller, final Stage stage) {
					controller.setResidence(residence);
					controller.setStage(stage);
				}
			};
			editorDialog.showAndWait();
		} catch (IOException e) {
			ExceptionDialog.show(e);
		}
	}

	public static void showAndWait(final Approval approval, final Stage parent) {
		try {
			EditorDialog<ApprovalEditorDialogController> editorDialog = new EditorDialog<ApprovalEditorDialogController>(parent, "/fxml/approval/approval_editor_dialog.fxml", approval, "Einwilligung") {
				@Override
				public void initController(final ApprovalEditorDialogController controller, final Stage stage) {
					controller.setApproval(approval);
					controller.setStage(stage);
				}
			};
			editorDialog.showAndWait();
		} catch (IOException e) {
			ExceptionDialog.show(e);
		}
	}

	public static void showAndWait(Event event, Stage parent) {
		try {
			EditorDialog<EventEditorDialogController> editorDialog = new EditorDialog<EventEditorDialogController>(parent, "/fxml/participant/event_editor_dialog.fxml", event, "Veranstaltung") {
				@Override
				public void initController(final EventEditorDialogController controller, final Stage stage) {
					controller.setEvent(event);
					controller.setStage(stage);
				}
			};
			editorDialog.showAndWait();
		} catch (IOException e) {
			ExceptionDialog.show(e);
		}
	}

	public static void showAndWait(Participant participant, Stage parent) {
		try {
			EditorDialog<ParticipantEditorDialogController> editorDialog = new EditorDialog<ParticipantEditorDialogController>(parent, "/fxml/participant/participant_editor_dialog.fxml", participant, "Teilnehmer") {
				@Override
				public void initController(final ParticipantEditorDialogController controller, final Stage stage) {
					controller.setParticipant(participant);
					controller.setStage(stage);
				}
			};
			editorDialog.showAndWait();
		} catch (IOException e) {
			ExceptionDialog.show(e);
		}
	}

	public abstract void initController(final T controller, final Stage stage);
}
