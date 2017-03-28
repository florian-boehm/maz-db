package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.model.Site;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

@OverviewController.Annotation(fxmlFile = "/fxml/yearabroad/site_overview.fxml", objDesc = "Einsatzstelle")
public class SiteOverviewController extends OverviewController<Site> {

	@FXML
	private TableColumn<Site, String> nameColumn;
	@FXML
	private TableColumn<Site, String> organizationColumn;
	@FXML
	private TableColumn<Site, Long> idColumn;

	public SiteOverviewController() {
		super(Site.class, true);
	}

	@Override
	protected Collection<Site> preLoad(EntityManager em) {
		return em.createNamedQuery("Site.findAll",Site.class).getResultList();
	}

	@Override
	protected String getLoadingText() {
		return "Lade Einsatzstellen ...";
	}

	@Override
	protected void handleException(RollbackException e) {
		ExceptionDialog.show(e);
	}

	@Override
	protected void postInit() {
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		organizationColumn.setCellValueFactory(cellData -> cellData.getValue().organizationProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
	}
}
