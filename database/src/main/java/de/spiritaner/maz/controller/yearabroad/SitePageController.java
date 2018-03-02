package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.Controller;
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

	public AnchorPane siteResponsibles;
	public ResponsibleOverviewController siteResponsiblesController;

	public AnchorPane siteYearsAbroad;
	public YearAbroadOverviewController siteYearsAbroadController;

	public AnchorPane siteEPNumbers;
	public EPNumberOverviewController siteEPNumbersController;

	public AnchorPane siteOverview;
	public SiteOverviewController siteOverviewController;

	public AnchorPane detailPane;
	public TabPane detailTabPane;
	public MaskerPane detailsMasker;

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
			siteYearsAbroadController.site.set(site);
			siteResponsiblesController.site.set(site);
			siteEPNumbersController.site.set(site);
			detailsMasker.setVisible(false);
			reloadSpecificTab(detailTabPane.getSelectionModel().getSelectedItem().getId());
		});
	}
}
