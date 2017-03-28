package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.residence.AddressEditorController;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.model.EPNumber;
import de.spiritaner.maz.model.Site;
import de.spiritaner.maz.util.DataDatabase;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/yearabroad/site_editor_dialog.fxml", objDesc = "Einsatzstelle")
public class SiteEditorDialogController extends EditorController<Site> {

	final static Logger logger = Logger.getLogger(SiteEditorDialogController.class);

	@FXML
	private GridPane siteEditor;
	@FXML
	private SiteEditorController siteEditorController;
	@FXML
	private GridPane addressEditor;
	@FXML
	private AddressEditorController addressEditorController;
	@FXML
	private Button saveSiteButton;
	@FXML
	private AnchorPane epNumberOverview;
	@FXML
	private EPNumberOverviewController epNumberOverviewController;
	@FXML
	private Text titleText;
	@FXML
	public ListView<EPNumber> selectedEPNumberList;
	@FXML
	private Button addEPNumberButton;
	@FXML
	private Button removeEPNumberButton;

	private ArrayList<EPNumber> removedEPNumbers = new ArrayList<>();

	@Override
	public void onReopen() {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		epNumberOverviewController.setStage(getStage());
		selectedEPNumberList.setCellFactory(param -> new ListCell<EPNumber>() {
			@Override
			public void updateItem(EPNumber item, boolean empty) {
				super.updateItem(item, empty);

				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					setText(item == null ? "null" : item.getNumber() + " / " + item.getDescription());
					setGraphic(null);
				}
			}
		});
	}

	public void saveSite(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			boolean siteValid = siteEditorController.isValid();
			boolean addressValid = addressEditorController.isValid();

			if (siteValid && addressValid) {
				EntityManager em = DataDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				getIdentifiable().setAddress(Address.findSame(em, addressEditorController.getAll(getIdentifiable().getAddress())));
				getIdentifiable().setEpNumbers(selectedEPNumberList.getItems());
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
						for(EPNumber epNumber : selectedEPNumberList.getItems()) {
							final EPNumber managedEPNumber = (!em.contains(epNumber)) ? em.find(EPNumber.class, epNumber.getId()) : epNumber;
							managedEPNumber.setSite(managedSite);
							//em.merge(managedEPNumber);
						}
					}

					em.getTransaction().commit();
					getStage().close();
				} catch (PersistenceException e) {
					em.getTransaction().rollback();
					logger.warn(e);
				} finally {
					em.close();
				}
			}
		});
	}

	public void closeDialog(ActionEvent actionEvent) {
		Platform.runLater(() -> getStage().close());
	}

	@Override
	public void setIdentifiable(Site site) {
		super.setIdentifiable(site);

		if(site != null) {
			siteEditorController.setAll(site);

			if(site.getAddress() != null) {
				addressEditorController.setAll(site.getAddress());
			}

			if(site.getEpNumbers() != null) {
				selectedEPNumberList.getItems().addAll(site.getEpNumbers());
			}

			if (site.getId() != 0L) {
				titleText.setText("Einsatzstelle bearbeiten");
				saveSiteButton.setText("Speichern");
			} else {
				titleText.setText("Einsatzstelle anlegen");
				saveSiteButton.setText("Anlegen");
			}
		}

	}

	public void addEPNumber(ActionEvent actionEvent) {
		EPNumber epNumber = epNumberOverviewController.getTable().getSelectionModel().getSelectedItem();

		if (epNumber != null && !selectedEPNumberList.getItems().contains(epNumber)) {
			selectedEPNumberList.getItems().add(epNumber);
			epNumberOverviewController.removeItem(epNumber);

			if(removedEPNumbers.contains(epNumber)) removedEPNumbers.remove(epNumber);
		}
	}

	public void removeEPNumber(ActionEvent actionEvent) {
		EPNumber epNumber = selectedEPNumberList.getSelectionModel().getSelectedItem();

		if (epNumber != null) {
			selectedEPNumberList.getItems().remove(epNumber);
			epNumberOverviewController.addItem(epNumber);

			if(epNumber.getSite() != null && epNumber.getSite().equals(getIdentifiable())) removedEPNumbers.add(epNumber);
		}
	}
}
