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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ResourceBundle;

public abstract class OverviewController<T extends Identifiable> implements Controller, Initializable {

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

	protected void preCreate(T newObject) {};

	public void create(ActionEvent actionEvent) {
		try {
			T obj = cls.newInstance();
			preCreate(obj);
			Method method = EditorDialog.class.getMethod("showAndWait", Identifiable.class, Stage.class);
			method.invoke(null, obj, stage);
			postCreate(obj);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			ExceptionDialog.show(e);
		}

		load();
	}

	protected void postCreate(T newObject) {};

	protected void preEdit(T object) {};

	public void edit(ActionEvent actionEvent) {
		editObj(null);
	}

	private void editObj(T obj) {
		try {
			obj = (obj == null) ? table.getSelectionModel().getSelectedItem() : obj;
			preEdit(obj);
			Method method = EditorDialog.class.getMethod("showAndWait",  Identifiable.class, Stage.class);
			method.invoke(null, obj, stage);
			//postCreate(object);
		} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			ExceptionDialog.show(e);
		}

		load();
	}

	protected void preRemove(T obsoleteEntity) {};

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
					preRemove(obsoleteEntity);
					em.remove(obsoleteEntity);
					em.getTransaction().commit();

					postRemove(obsoleteEntity);
				} catch (RollbackException e) {
					// TODO show graphical error message in better way
					handleException(e);
				}
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			ExceptionDialog.show(e);
		}

		load();
	}

	protected void postRemove(T obsoleteEntity) {};

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

				if(useFilter) {
					tableFilter.getBackingList().clear();
					if (result != null) tableFilter.getBackingList().addAll(result);
				} else {
					table.getItems().clear();
					if (result != null) table.getItems().addAll(result);
				}

				//table.getSelectionModel().select(previousSelected);
				masker.setVisible(false);
			} catch (RollbackException e) {
				handleException(e);
			}
		});
	}

	protected abstract Collection<T> preLoad(EntityManager em);

	protected void postLoad(Collection<T> loadedObjs) {};

	protected abstract String getLoadingText();

	protected abstract void handleException(RollbackException e);

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void onReopen() {
		load();
	}

	protected void preInit() {};

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		preInit();

		getTable().getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) -> {
			removeButton.setDisable(newVal == null);
			editButton.setDisable(newVal == null);
		});

		getTable().setRowFactory(tv -> {
			TableRow<T> row = new TableRow<>();

			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty()) && editOnDoubleclick) {
					editObj(row.getItem());
				}
			});

			return row;
		});

		if(useFilter) {
			TableFilter.Builder<T> tableFilterBuilder = TableFilter.forTableView(table);
			tableFilter = tableFilterBuilder.apply();
		}

		postInit();

		load();
	}

	protected void postInit() {};

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

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Annotation {
		String fxmlFile() default "";
		String objDesc() default "";
	}
}
