package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.controller.person.PersonOverviewController;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.dialog.OverviewDialog;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Site;
import de.spiritaner.maz.model.YearAbroad;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/yearabroad/yearabroad_editor_dialog.fxml", objDesc = "Auslandsjahr")
public class YearAbroadEditorDialogController extends EditorController<YearAbroad> {

	@FXML private Text titleText;
	@FXML private GridPane siteEditor;
	@FXML private SiteEditorController siteEditorController;
	@FXML private GridPane yearAbroadEditor;
	@FXML private YearAbroadEditorController yearAbroadEditorController;
	@FXML private GridPane personEditor;
	@FXML private PersonEditorController personEditorController;
	@FXML private Button saveYearAbroadButton;
	@FXML private Button searchPersonButton;
	@FXML private Button searchSiteButton;

	@Override
	public void setIdentifiable(YearAbroad yearAbroad) {
		super.setIdentifiable(yearAbroad);

		siteEditorController.setReadonly(true);
		personEditorController.setReadonly(true);

		if (yearAbroad != null) {
			if (yearAbroad.getSite() != null) {
				siteEditorController.setAll(yearAbroad.getSite());
				searchSiteButton.setDisable(true);
			}

			if (yearAbroad.getPerson() != null) {
				personEditorController.setAll(yearAbroad.getPerson());
				searchPersonButton.setDisable(true);
			}

			yearAbroadEditorController.setAll(yearAbroad);

			if (yearAbroad.getId() != 0L) {
				titleText.setText("Auslandsjahr bearbeiten");
				saveYearAbroadButton.setText("Speichern");
			} else {
				titleText.setText("Auslandsjahr anlegen");
				saveYearAbroadButton.setText("Anlegen");
			}
		}
	}

	@Override
	public void onReopen() {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void saveYearAbroad(ActionEvent actionEvent) {
		System.out.println("TO BE IMPLEMENTED");
	}

	public void closeDialog(ActionEvent actionEvent) {
		Platform.runLater(() -> getStage().close());
	}

	public void searchPerson(ActionEvent actionEvent) {
		OverviewDialog<PersonOverviewController, Person> dialog = new OverviewDialog<>(PersonOverviewController.class);
		Optional<Person> result = dialog.showAndWait(getStage());

		result.ifPresent((Person person) -> {
			getIdentifiable().setPerson(person);
			personEditorController.setAll(person);
		});
	}

	public void searchSite(ActionEvent actionEvent) {
		OverviewDialog<SiteOverviewController, Site> dialog = new OverviewDialog<>(SiteOverviewController.class);
		Optional<Site> result = dialog.showAndWait(getStage());

		result.ifPresent((Site site) -> {
			getIdentifiable().setSite(site);
			siteEditorController.setAll(site);
		});
	}
}
