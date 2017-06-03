package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.view.dialog.RemoveDialog;
import de.spiritaner.maz.model.EPNumber;
import de.spiritaner.maz.model.Site;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;
import java.util.List;

public class EPNumberOverviewController extends OverviewController<EPNumber> {

	@FXML
	private TableColumn<EPNumber, Integer> numberColumn;
	@FXML
	private TableColumn<EPNumber, String> descriptionColumn;

	private Site site;

	public EPNumberOverviewController() {
		super(EPNumber.class, true);
	}

	@Override
	protected Collection<EPNumber> preLoad(EntityManager em) {
		if(site != null) {
			Hibernate.initialize(site.getEpNumbers());

			Collection<EPNumber> allEpNumbers = FXCollections.observableArrayList();
			if(site.getEpNumbers() != null) allEpNumbers.addAll(site.getEpNumbers());

			if(site.getId() <= 0L) {
				List<EPNumber> epNumbersWithoutSite = em.createNamedQuery("EPNumber.findAllWithoutSite", EPNumber.class).getResultList();
				if (epNumbersWithoutSite.size() > 0) allEpNumbers.addAll(epNumbersWithoutSite);
			}

			return allEpNumbers;
		} else {
			return em.createNamedQuery("EPNumber.findAllWithoutSite", EPNumber.class).getResultList();
		}
	}

	@Override
	protected String getLoadingText() {
		return "Lade EP-Nummern ...";
	}

	@Override
	protected void handleException(RollbackException e, EPNumber epNumber) {
		RemoveDialog.showFailureAndWait("EP-Nummer","EP-Nummer",e);
	}

	@Override
	protected void postInit() {
		descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		numberColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty().asObject());
	}

	public void setSite(Site site) {
		this.site = site;
	}
}
