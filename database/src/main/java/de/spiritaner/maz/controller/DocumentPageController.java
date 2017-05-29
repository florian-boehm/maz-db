package de.spiritaner.maz.controller;

import de.spiritaner.maz.controller.participation.EventOverviewController;
import de.spiritaner.maz.controller.person.PersonOverviewController;
import de.spiritaner.maz.util.document.Envelope;
import de.spiritaner.maz.util.document.ParticipantList;
import de.spiritaner.maz.view.renderer.TemplateFileListCell;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.controlsfx.control.MaskerPane;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class DocumentPageController implements Controller {

	private static final Logger logger = Logger.getLogger(DocumentPageController.class);

	// Tabs
	@FXML private TabPane tabPane;
	@FXML private Tab outTab;
	@FXML private Tab eventTab;
	@FXML private Tab docTab;
	@FXML private Tab personTab;
	// Document Page
	@FXML private RadioButton mailRadioButton;
	@FXML private ToggleGroup documentType;
	@FXML private RadioButton participantListRadioButton;
	@FXML private RadioButton envelopeRadioButton;
	@FXML private ComboBox<File> templateComboBox;
	@FXML private Button templateOtherButton;
	@FXML private ImageView templatePreviewImage;
	@FXML private Button docNextButton;
	// Event Page
	@FXML private Button eventBackButton;
	@FXML private Button eventNextButton;
	@FXML private CheckBox eventNameCheckBox;
	@FXML private CheckBox eventBeginCheckBox;
	@FXML private CheckBox eventEndCheckBox;
	@FXML private AnchorPane eventOverview;
	@FXML private EventOverviewController eventOverviewController;
	@FXML private CheckBox eventLocationCheckBox;
	@FXML private CheckBox participantFirstNameCheckBox;
	@FXML private CheckBox participantFamilyNameCheckBox;
	@FXML private CheckBox hasParticipatedCheckBox;
	@FXML private CheckBox participantPhotoApprovalCheckBox;
	// Out Page
	@FXML private ComboBox<String> fileTypeComboBox;
	@FXML private TextField outFilePathTextField;
	@FXML private Button outFileSearchButton;
	@FXML private Button outBackButton;
	@FXML private Button outFinishButton;
	@FXML private Text fileTypeHint;
	@FXML private MaskerPane progressMasker;
	// Person Page
	@FXML private AnchorPane personOverview;
	@FXML private PersonOverviewController personOverviewController;
	@FXML private Button personBackButton;
	@FXML private Button personNextButton;

	private enum Page {
		DOCUMENT, EVENT, PERSON, OUTPUT,
	}

	private Stage stage;

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void onReopen() {
		personOverviewController.onReopen();
		eventOverviewController.onReopen();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		eventTab.setDisable(true);
		personTab.setDisable(true);
		outTab.setDisable(true);

		eventOverviewController.setToolbarVisible(false);
		eventOverviewController.setEditOnDoubleclick(false);
		eventOverviewController.getTable().getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> eventNextButton.setDisable(newValue.intValue() < 0));

		personOverviewController.setToolbarVisible(false);
		personOverviewController.setEditOnDoubleclick(false);
		personOverviewController.getTable().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		personOverviewController.getTable().getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			personNextButton.setDisable(personOverviewController.getTable().getSelectionModel().getSelectedItems().size() == 0);
		});

		fileTypeComboBox.getItems().add("Word");
		//fileTypeComboBox.getItems().add("PDF");
		/*fileTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if(envelopeRadioButton.isSelected()) {
				fileTypeHint.setVisible(newValue.equals("Word"));
			} else {
				fileTypeHint.setVisible(false);
			}
		});*/
		fileTypeComboBox.getSelectionModel().clearAndSelect(0);

		templateComboBox.setCellFactory(cell -> new TemplateFileListCell());
		templateComboBox.setButtonCell(new TemplateFileListCell());
		templateComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			docNextButton.setDisable(newValue == null || !newValue.exists() || documentType.getSelectedToggle() == null);
		});

		documentType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			String subFolder = "";

			if(newValue.equals(participantListRadioButton)) {
				subFolder = "participantlist/";
			} else if(newValue.equals(envelopeRadioButton)) {
				subFolder = "envelope/";
			}

			File[] templates = new File("./templates/"+subFolder).listFiles((current, name) -> !new File(current, name).isDirectory() && name.endsWith(".docx"));

			if(templates != null) {
				templateComboBox.getItems().clear();
				templateComboBox.getItems().addAll(templates);
				templateComboBox.getSelectionModel().clearAndSelect(0);
			}
		});

		outFilePathTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			outFinishButton.setDisable(newValue.trim().isEmpty());
		});
	}

	public void docNext(ActionEvent actionEvent) {
		if(participantListRadioButton.isSelected()) {
			selectPage(Page.EVENT);
		} else if(envelopeRadioButton.isSelected()) {
			selectPage(Page.PERSON);
		}
	}

	public void eventBack(ActionEvent actionEvent) {
		selectPage(Page.DOCUMENT);
	}

	public void eventNext(ActionEvent actionEvent) {
		selectPage(Page.OUTPUT);
	}

	public void outBack(ActionEvent actionEvent) {
		if(participantListRadioButton.isSelected()) {
			selectPage(Page.EVENT);
		} else if(envelopeRadioButton.isSelected()) {
			selectPage(Page.PERSON);
		}
	}

	public void outFinish(ActionEvent actionEvent) {
		File template = templateComboBox.getSelectionModel().getSelectedItem();
		File outFile = new File(outFilePathTextField.getText());

		if(participantListRadioButton.isSelected()) {
			ParticipantList.createForEvent(eventOverviewController.getTable().getSelectionModel().getSelectedItem(), template, outFile, this);
		} else if(envelopeRadioButton.isSelected()) {
			Envelope.create(personOverviewController.getTable().getSelectionModel().getSelectedItems(), template, outFile, this);
		}

		setProgress(false,"",-1);
		selectPage(Page.DOCUMENT);
	}

	public void searchOutFile(ActionEvent actionEvent) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Ausgabedatei wählen ...");
		String extension = (fileTypeComboBox.getSelectionModel().getSelectedItem().equals("PDF")) ? "*.pdf" : "*.docx";
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(fileTypeComboBox.getSelectionModel().getSelectedItem(), extension);
		fileChooser.getExtensionFilters().add(filter);
		File file = fileChooser.showSaveDialog(stage);

		if(file != null) outFilePathTextField.setText(file.getAbsolutePath());
	}

	public void personBack(ActionEvent actionEvent) {
		eventBack(actionEvent);
	}

	public void personNext(ActionEvent actionEvent) {
		eventNext(actionEvent);
	}

	private void selectPage(Page p) {
		tabPane.getSelectionModel().getSelectedItem().setDisable(true);
		tabPane.getSelectionModel().select(p.ordinal());
		tabPane.getSelectionModel().getSelectedItem().setDisable(false);
	}

	public void setProgress(boolean visible, String text, double value) {
		Platform.runLater(() -> {
			progressMasker.setText(text);
			progressMasker.setProgress(value);
			progressMasker.setVisible(visible);
		});
	}

	public void loadCustomTemplate(ActionEvent actionEvent) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Vorlage laden ...");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Word","*.docx"));
		File template = fileChooser.showOpenDialog(stage);

		if(template != null) {
			templateComboBox.getItems().add(template);
			templateComboBox.getSelectionModel().clearSelection();
			templateComboBox.getSelectionModel().select(template);
		}
	}
}
