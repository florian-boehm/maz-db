package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.model.EPNumber;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Site;
import de.spiritaner.maz.model.YearAbroad;
import de.spiritaner.maz.view.dialog.RemoveDialog;
import de.spiritaner.maz.view.renderer.DateAsStringListCell;
import de.spiritaner.maz.view.renderer.EPNumberCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.time.LocalDate;
import java.util.Collection;

public class YearAbroadOverviewController extends OverviewController<YearAbroad> {

	public TableColumn<YearAbroad, String> personColumn;
	public TableColumn<YearAbroad, String> siteColumn;
	public TableColumn<YearAbroad, String> jobDescriptionColumn;
	public TableColumn<YearAbroad, LocalDate> departureDateColumn;
	public TableColumn<YearAbroad, LocalDate> arrivalDateColumn;
	public TableColumn<YearAbroad, LocalDate> abortionDateColumn;
	public TableColumn<YearAbroad, Boolean> wwPromotedColumn;
	public TableColumn<YearAbroad, EPNumber> epNumberColumn;

	public ObjectProperty<Site> site = new SimpleObjectProperty<>();
	public ObjectProperty<Person> person = new SimpleObjectProperty<>();

	public YearAbroadOverviewController() {
		super(YearAbroad.class, true);
	}

	@Override
	protected Collection<YearAbroad> preLoad(EntityManager em) {
		if(site.get() != null) {
			Hibernate.initialize(site.get().getYearsAbroad());
			return site.get().getYearsAbroad();
		} else if(person.get() != null) {
			Hibernate.initialize(person.get().getYearsAbroad());
			return person.get().getYearsAbroad();
		} else {
			return null;
		}
	}

	@Override
	protected String getLoadingText() {
		return guiText.getString("loading") + " " + guiText.getString("years_abroad") + " ...";
	}

	@Override
	protected void handleException(RollbackException e, YearAbroad yearAbroad) {
		// TODO choose better text here
		String objName = guiText.getString("year_abroad");
		RemoveDialog.showFailureAndWait(objName, objName, e);
	}

	@Override
	protected void postInit() {
		personColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPerson().getFullName()));
		siteColumn.setCellValueFactory(cellData -> cellData.getValue().getSite().nameProperty());
		departureDateColumn.setCellValueFactory(cellData -> cellData.getValue().departureDateProperty());
		arrivalDateColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalDateProperty());
		abortionDateColumn.setCellValueFactory(cellData -> cellData.getValue().abortionDateProperty());
		wwPromotedColumn.setCellValueFactory(cellData -> cellData.getValue().wwPromotedProperty());
		jobDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().jobDescriptionProperty());
		epNumberColumn.setCellValueFactory(cellData -> cellData.getValue().epNumberProperty());

		epNumberColumn.setCellFactory(column -> EPNumberCell.epNumberTableCell());
		//wwPromotedColumn.setCellFactory(column -> new BooleanTableCell<>());
		wwPromotedColumn.setCellFactory(column -> new TableCell<YearAbroad, Boolean>() {
			@Override
			protected void updateItem ( final Boolean item, final boolean empty){
				super.updateItem(item, empty);
				Object rowObj = super.getTableRow().getItem();
				String additional = "";

				if(rowObj instanceof YearAbroad) {
					additional = " ("+ ((YearAbroad) rowObj).getWwMonths() + " Monate)";
				}

				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					setText(((Boolean) item) ? "Ja" + additional : "Nein");
				}
			}
		});
		departureDateColumn.setCellFactory(column -> DateAsStringListCell.localDateTableCell());
		arrivalDateColumn.setCellFactory(column -> DateAsStringListCell.localDateTableCell());
		abortionDateColumn.setCellFactory(column -> DateAsStringListCell.localDateTableCell());

		siteColumn.visibleProperty().bind(site.isNull());
		personColumn.visibleProperty().bind(person.isNull());
	}

	@Override
	protected void preCreate(YearAbroad yearAbroad) {
		if(site != null) yearAbroad.setSite(site.get());
		if(person != null) yearAbroad.setPerson(person.get());
	}
}
