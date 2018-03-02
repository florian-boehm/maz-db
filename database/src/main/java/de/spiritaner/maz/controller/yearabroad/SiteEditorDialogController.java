package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.controller.residence.AddressEditorController;
import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.model.EPNumber;
import de.spiritaner.maz.model.Site;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.view.dialog.EditorDialog;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/yearabroad/site_editor_dialog.fxml", objDesc = "$site")
public class SiteEditorDialogController extends EditorDialogController<Site> {

	final static Logger logger = Logger.getLogger(SiteEditorDialogController.class);

	public GridPane siteEditor;
	public SiteEditorController siteEditorController;
	public GridPane addressEditor;
	public AddressEditorController addressEditorController;
	public AnchorPane epNumberOverview;
	public EPNumberOverviewController epNumberOverviewController;

	private ArrayList<EPNumber> removedEPNumbers = new ArrayList<>();

	@Override
	protected boolean allValid() {
		boolean siteValid = siteEditorController.isValid();
		boolean addressValid = addressEditorController.isValid();

		return siteValid && addressValid;
	}

	@Override
	protected void bind() {
		epNumberOverviewController.setStage(getStage());
	}

	@Override
	protected void preSave(EntityManager em) {
		getIdentifiable().setAddress(Address.findSame(em, addressEditorController.address.get()));
		getIdentifiable().setEpNumbers(epNumberOverviewController.getTable().getItems());
	}

	@Override
	protected void preSave(Site managedSite, EntityManager em) {
		// Remove this site from removed ep numbers
		for(EPNumber epNumber : removedEPNumbers) {
			final EPNumber managedEPNumber = (!em.contains(epNumber)) ? em.find(EPNumber.class, epNumber.getId()) : epNumber;
			managedEPNumber.setSite(null);
		}

		// Add site to all ep selected ep numbers afterwards
		if(managedSite != null) {
			for(EPNumber epNumber : epNumberOverviewController.getTable().getItems()) {
				final EPNumber managedEPNumber = (!em.contains(epNumber)) ? em.find(EPNumber.class, epNumber.getId()) : epNumber;
				managedEPNumber.setSite(managedSite);
				//em.merge(managedEPNumber);
			}
		}
	}

	// TODO Remove after check if correctly implemented
	/*public void saveSite(ActionEvent actionEvent) {
				getIdentifiable().setAddress(Address.findSame(em, addressEditorController.getAll(getIdentifiable().getAddress())));
				getIdentifiable().setEpNumbers(epNumberOverviewController.getTable().getItems());
				siteEditorController.getAll(getIdentifiable());

				try {
					final Site managedSite = (!em.contains(getIdentifiable())) ? em.merge(getIdentifiable()) : getIdentifiable();

					// Remove this site from removed ep numbers
					for(EPNumber epNumber : removedEPNumbers) {
						final EPNumber managedEPNumber = (!em.contains(epNumber)) ? em.find(EPNumber.class, epNumber.getId()) : epNumber;
						managedEPNumber.setSite(null);
					}

					// Add site to all ep selected ep numbers afterwards
					if(managedSite != null) {
						for(EPNumber epNumber : epNumberOverviewController.getTable().getItems()) {
							final EPNumber managedEPNumber = (!em.contains(epNumber)) ? em.find(EPNumber.class, epNumber.getId()) : epNumber;
							managedEPNumber.setSite(managedSite);
							//em.merge(managedEPNumber);
						}
					}

					em.getTransaction().commit();
					setResult(managedSite);
					requestClose();
					//getStage().close();
				} catch (PersistenceException e) {
					em.getTransaction().rollback();
					logger.warn(e);
				} finally {
					em.close();
				}
			}
		});
	}*/

	@Override
	public void bind(Site site) {
		addressEditorController.address.bindBidirectional(site.address);
		siteEditorController.site.bindBidirectional(identifiable);
		epNumberOverviewController.site.bindBidirectional(identifiable);
	}
}
