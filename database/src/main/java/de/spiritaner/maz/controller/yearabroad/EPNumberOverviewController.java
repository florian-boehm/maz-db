package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.model.EPNumber;
import de.spiritaner.maz.model.Site;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

public class EPNumberOverviewController extends OverviewController<EPNumber> {

	@FXML
	private TableColumn<EPNumber, Integer> numberColumn;
	@FXML
	private TableColumn<EPNumber, String> descriptionColumn;
	@FXML
	private TableColumn<EPNumber, Long> idColumn;

	private Site site;

	public EPNumberOverviewController() {
		super(EPNumber.class, true);
	}

	@Override
	protected Collection<EPNumber> preLoad(EntityManager em) {
		if(site != null) {
			Hibernate.initialize(site.getEpNumbers());
			return site.getEpNumbers();
		} else {
			return em.createNamedQuery("EPNumber.findAllWithoutSite", EPNumber.class).getResultList();
		}
	}

	@Override
	protected String getLoadingText() {
		return "Lade EP-Nummern ...";
	}

	@Override
	protected void handleException(RollbackException e) {
		ExceptionDialog.show(e);
	}

	@Override
	protected void postInit() {
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
		descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		numberColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty().asObject());
	}

	public void setSite(Site site) {
		this.site = site;
	}
}
