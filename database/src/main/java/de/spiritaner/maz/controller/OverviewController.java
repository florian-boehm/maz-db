package de.spiritaner.maz.controller;

import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.dialog.RemoveDialog;
import de.spiritaner.maz.model.Identifiable;
import de.spiritaner.maz.util.DataDatabase;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.table.TableFilter;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import java.util.ResourceBundle;

public abstract class OverviewController<T extends Identifiable> implements Controller, Initializable {

	final static Logger logger = Logger.getLogger(OverviewController.class);

	@FXML private MaskerPane masker;
	@FXML private TableView<T> table;
	@FXML private Button removeButton;
	@FXML private Button editButton;
	@FXML private Button createButton;
	@FXML private ToolBar toolbar;

	private TableFilter<T> tableFilter;
	private boolean useFilter;
	private Stage stage;
	private Class<T> cls;
	private boolean editOnDoubleclick;

	public OverviewController(Class<T> cls, boolean useFilter) {
		this.cls = cls;
		this.useFilter = useFilter;
		this.editOnDoubleclick = true;
	}

	public TableView<T> getTable() {
		return table;
	}

	public void setTable(TableView<T> table) {
		this.table = table;
	}

	public Button getRemoveButton() {
		return removeButton;
	}

	public void setRemoveButton(Button removeButton) {
		this.removeButton = removeButton;
	}

	public Button getEditButton() {
		return editButton;
	}

	public void setEditButton(Button editButton) {
		this.editButton = editButton;
	}

	public Button getCreateButton() {
		return createButton;
	}

	public void setCreateButton(Button createButton) {
		this.createButton = createButton;
	}

	protected void preCreate(T newObject) {
	}

	@SuppressWarnings("unchecked")
	public void create(ActionEvent actionEvent) {
		try {
			T obj = cls.newInstance();
			preCreate(obj);
			Method method = EditorDialog.class.getMethod("showAndWait", Identifiable.class, Stage.class);
			// Suppress warning needed for the following cast, because it will always be a correct cast
			Optional<T> result = (Optional<T>) method.invoke(null, obj, stage);

			result.ifPresent(managedObject -> {
				postCreate(managedObject);
				addItem(managedObject);
			});
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			ExceptionDialog.show(e);
		}
	}

	protected void postCreate(T newObject) {
	}

	protected void preEdit(T object) {
	}


	protected void postEdit(T previouslySelected, T edited) {
	}

	public void edit(ActionEvent actionEvent) {
		editObj(null);
	}

	@SuppressWarnings("unchecked")
	private void editObj(T obj) {
		try {
			obj = (obj == null) ? table.getSelectionModel().getSelectedItem() : obj;
			preEdit(obj);
			Method method = EditorDialog.class.getMethod("showAndWait", Identifiable.class, Stage.class);
			Optional<T> result = (Optional<T>) method.invoke(null, obj, stage);
			final T previousObject = obj;

			result.ifPresent(managedObject -> {
				postEdit(previousObject, managedObject);
			});

			if(!result.isPresent()) load();
		} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			ExceptionDialog.show(e);
		}
	}

	protected void preRemove(T obsoleteEntity, EntityManager em) {
	}

	public void remove(final ActionEvent actionEvent) {
		final T selectedObj = getTable().getSelectionModel().getSelectedItem();

		try {
			final Method method = RemoveDialog.class.getMethod("create", cls, Stage.class);
			final Alert removeDialog = (Alert) method.invoke(null, selectedObj, stage);
			final Optional<ButtonType> result = removeDialog.showAndWait();

			if (result.get() == ButtonType.OK) {
				try {
					EntityManager em = DataDatabase.getFactory().createEntityManager();
					em.getTransaction().begin();
					final T obsoleteEntity = em.find(cls, selectedObj.getId());
					preRemove(obsoleteEntity, em);
					em.remove(obsoleteEntity);
					em.getTransaction().commit();

					removeItem(obsoleteEntity);

					postRemove(obsoleteEntity);
				} catch (RollbackException e) {
					handleException(e, selectedObj);
				}
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			ExceptionDialog.show(e);
		}

		//load();
	}

	protected void postRemove(T obsoleteEntity) {
	}

	public void load() {
		Platform.runLater(() -> {
			try {
				masker.setProgressVisible(true);
				masker.setText(getLoadingText());
				masker.setVisible(true);

				//T previousSelected = table.getSelectionModel().getSelectedItem();
				//table.getSelectionModel().clearSelection();

				EntityManager em = DataDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();
				Collection<T> result = preLoad(em);
				em.getTransaction().commit();

				postLoad(result);

				logger.debug("OverviewController "+this.getClass()+" is clearing now!");

				if (useFilter) {
					tableFilter.getBackingList().clear();
					if (result != null) tableFilter.getBackingList().addAll(result);
				} else {
					table.getItems().clear();
					if (result != null) table.getItems().addAll(result);
				}

				//table.getSelectionModel().select(previousSelected);
				masker.setVisible(false);
			} catch (RollbackException e) {
				logger.error(e);
			}
		});
	}

	protected abstract Collection<T> preLoad(EntityManager em);

	protected void postLoad(Collection<T> loadedObjs) { }

	protected abstract String getLoadingText();

	protected abstract void handleException(RollbackException e, T selectedObj);

	@Override
	public void onReopen() {
		load();
	}

	protected void preInit() {
	}

	protected boolean isRemoveButtonDisabled(T oldVal, T newVal) {
		return newVal == null;
	}

	protected boolean isEditButtonDisabled(T oldVal, T newVal) {
		return newVal == null;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		preInit();

		getTable().getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) -> {
			removeButton.setDisable(isRemoveButtonDisabled(oldVal, newVal));
			editButton.setDisable(isEditButtonDisabled(oldVal, newVal));
		});

		getTable().setRowFactory(tv -> {
			TableRow<T> row = new TableRow<>();

			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty()) && editOnDoubleclick) {
					if(!isEditButtonDisabled(null, row.getItem())) editObj(row.getItem());
				}
			});

			return row;
		});

		if (useFilter) {
			TableFilter.Builder<T> tableFilterBuilder = TableFilter.forTableView(table);
			tableFilter = tableFilterBuilder.apply();
		}

		postInit();

		load();
	}

	protected void postInit() {
	}

	public MaskerPane getMasker() {
		return masker;
	}

	public void setToolbarVisible(boolean visibility) {
		toolbar.setVisible(visibility);
		toolbar.setManaged(false);
	}

	public void setEditOnDoubleclick(boolean editOnDoubleclick) {
		this.editOnDoubleclick = editOnDoubleclick;
	}

	public Stage getStage() {
		return stage;
	}

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void removeItem(T item) {
		if (useFilter) {
			if(tableFilter.getBackingList().contains(item)) tableFilter.getBackingList().remove(item);
		} else {
			if(table.getItems().contains(item)) table.getItems().remove(item);
		}
	}

	public void addItem(T item) {
		logger.debug("Adding item " + item.getClass() + " to "+this.getClass());

		if (useFilter) {
			tableFilter.getBackingList().add(item);
		} else {
			table.getItems().add(item);
		}
	}

	public boolean isUseFilter() {
		return useFilter;
	}

	public void setUseFilter(boolean useFilter) {
		this.useFilter = useFilter;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Annotation {
		String fxmlFile() default "";

		String objDesc() default "";
	}
}
