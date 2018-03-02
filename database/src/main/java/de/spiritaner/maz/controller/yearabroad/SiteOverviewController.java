package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.model.Site;
import de.spiritaner.maz.view.dialog.RemoveDialog;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

@OverviewController.Annotation(fxmlFile = "/fxml/yearabroad/site_overview.fxml", objDesc = "$site")
public class SiteOverviewController extends OverviewController<Site> {

	public TableColumn<Site, String> nameColumn;
	public TableColumn<Site, String> organizationColumn;

	public SiteOverviewController() {
		super(Site.class, true);
	}

	@Override
	protected Collection<Site> preLoad(EntityManager em) {
		return em.createNamedQuery("Site.findAll",Site.class).getResultList();
	}

	@Override
	protected String getLoadingText() {
		return guiText.getString("loading") + " " + guiText.getString("site") + " ...";
	}

	@Override
	protected void handleException(RollbackException e, Site site) {
		String objName = guiText.getString("site");
		RemoveDialog.showFailureAndWait(objName, objName, e);
	}

	@Override
	protected void postInit() {
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		organizationColumn.setCellValueFactory(cellData -> cellData.getValue().organizationProperty());
	}
}
