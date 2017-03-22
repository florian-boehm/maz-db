package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.Controller;
import de.spiritaner.maz.model.Event;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.controlsfx.control.MaskerPane;

import java.net.URL;
import java.util.ResourceBundle;

public class EventPageController implements Initializable, Controller {

	@FXML
	private AnchorPane eventOverview;
	@FXML
	private EventOverviewController eventOverviewController;
	@FXML
	private AnchorPane detailPane;
	@FXML
	private TabPane detailTabPane;
	@FXML
	private MaskerPane detailsMasker;

	@FXML
	private AnchorPane eventParticipants;
	@FXML
	private ParticipationOverviewController eventParticipantsController;

	private Stage stage;

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;

		eventParticipantsController.setStage(stage);
	}

	@Override
	public void onReopen() {
		eventOverviewController.onReopen();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		eventOverviewController.getTable().getSelectionModel().selectedItemProperty().addListener((observable, oldEvent, newEvent) -> {
			if (newEvent != null/* && eventOverviewController.getPersonDetailsToggle().isSelected()*/) {
				loadEventDetails(newEvent);
			} else if (newEvent == null) {
				detailsMasker.setVisible(true);
			}
		});

		detailTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
			reloadSpecificTab(newTab.getId());
		});
	}

	private void reloadSpecificTab(String tabFxId) {
		switch(tabFxId) {
			case "participantTab": eventParticipantsController.onReopen(); break;
		}
	}

	private void loadEventDetails(Event event) {
		Platform.runLater(() -> {
			eventParticipantsController.setEvent(event);
			detailsMasker.setVisible(false);
			reloadSpecificTab(detailTabPane.getSelectionModel().getSelectedItem().getId());
		});
	}
}
