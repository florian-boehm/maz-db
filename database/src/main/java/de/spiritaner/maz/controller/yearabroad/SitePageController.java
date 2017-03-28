package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.Controller;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.Site;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.controlsfx.control.MaskerPane;

import java.net.URL;
import java.util.ResourceBundle;

public class SitePageController implements Controller {

	@FXML
	private AnchorPane siteResponsibles;
	@FXML
	private ResponsibleOverviewController siteResponsiblesController;

	@FXML
	private AnchorPane siteYearsAbroad;
	@FXML
	private YearAbroadOverviewController siteYearsAbroadController;

	@FXML
	private AnchorPane siteEPNumbers;
	@FXML
	private EPNumberOverviewController siteEPNumbersController;

	@FXML
	private AnchorPane siteOverview;
	@FXML
	private SiteOverviewController siteOverviewController;

	@FXML
	private AnchorPane detailPane;
	@FXML
	private TabPane detailTabPane;
	@FXML
	private MaskerPane detailsMasker;

	private Stage stage;

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;

		siteResponsiblesController.setStage(stage);
		siteYearsAbroadController.setStage(stage);
		siteEPNumbersController.setStage(stage);
		siteOverviewController.setStage(stage);
	}

	@Override
	public void onReopen() {
		siteOverviewController.onReopen();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		siteOverviewController.getTable().getSelectionModel().selectedItemProperty().addListener((observable, oldSite, newSite) -> {
			if (newSite != null) {
				loadSiteDetails(newSite);
			} else {
				detailsMasker.setVisible(true);
			}
		});

		detailTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
			reloadSpecificTab(newTab.getId());
		});
	}

	private void reloadSpecificTab(String tabFxId) {
		switch(tabFxId) {
			case "responsibleTab": siteResponsiblesController.onReopen(); break;
			case "yearAbroadTab": siteYearsAbroadController.onReopen(); break;
			case "epNumberTab": siteEPNumbersController.onReopen(); break;
		}
	}

	private void loadSiteDetails(Site site) {
		Platform.runLater(() -> {
			siteYearsAbroadController.setSite(site);
			siteResponsiblesController.setSite(site);
			siteEPNumbersController.setSite(site);
			detailsMasker.setVisible(false);
			reloadSpecificTab(detailTabPane.getSelectionModel().getSelectedItem().getId());
		});
	}
}
