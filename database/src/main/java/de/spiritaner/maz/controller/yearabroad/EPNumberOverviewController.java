package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.model.EPNumber;
import de.spiritaner.maz.model.Site;
import de.spiritaner.maz.view.dialog.RemoveDialog;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;
import java.util.List;

public class EPNumberOverviewController extends OverviewController<EPNumber> {

	public TableColumn<EPNumber, Integer> numberColumn;
	public TableColumn<EPNumber, String> descriptionColumn;

	public ObjectProperty<Site> site = new SimpleObjectProperty<>();

	public EPNumberOverviewController() {
		super(EPNumber.class, true);
	}

	@Override
	protected Collection<EPNumber> preLoad(EntityManager em) {
		if(site.get() != null) {
			Hibernate.initialize(site.get().getEpNumbers());

			Collection<EPNumber> allEpNumbers = FXCollections.observableArrayList();
			if(site.get().getEpNumbers() != null) allEpNumbers.addAll(site.get().getEpNumbers());

			if(site.get().getId() <= 0L) {
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
		String objName = guiText.getString("ep_number");
		RemoveDialog.showFailureAndWait(objName, objName, e);
	}

	@Override
	protected void postInit() {
		descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		numberColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty().asObject());
	}
}
