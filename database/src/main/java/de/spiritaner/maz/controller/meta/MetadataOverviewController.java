package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.Identifiable;
import de.spiritaner.maz.model.meta.MetaClass;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.view.dialog.EditorDialog;
import de.spiritaner.maz.view.dialog.ExceptionDialog;
import de.spiritaner.maz.view.dialog.MetadataEditorDialog;
import de.spiritaner.maz.view.dialog.RemoveDialog;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public abstract class MetadataOverviewController<T extends MetaClass> extends BorderPane implements Initializable {

	final static Logger logger = Logger.getLogger(MetadataOverviewController.class);

	@FXML private TableView<T> metaClassTable;
	@FXML private TableColumn<T, Long> metaClassIdColumn;
	@FXML private TableColumn<T, String> metaClassDescriptionColumn;
	@FXML private Button removeMetadata;
	@FXML private Button editMetadata;

	protected EntityManager em = CoreDatabase.getFactory().createEntityManager();
	private Class<T> cls;

	public MetadataOverviewController(Class<T> cls) {
		this.cls = cls;

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/meta/metadata_editor.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			System.out.println();
		}

		metaClassTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		metaClassTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
			removeMetadata.setDisable(newValue == null || !newValue.isRemovable());
			editMetadata.setDisable(newValue == null || !newValue.isEditable());
		});
	}

	@Override
	public void initialize(final URL url, final ResourceBundle resourceBundle) {
		metaClassIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
		metaClassDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		load();
	}

	public void load() {
		metaClassTable.setItems(FXCollections.observableArrayList(em.createNamedQuery(cls.getSimpleName() + ".findAll", cls).getResultList()));
	}

	public void create(final ActionEvent actionEvent) {
		Identifiable.Annotation annotation = cls.getAnnotation(Identifiable.Annotation.class);

		if (annotation != null) {
			try {
				T obj = cls.newInstance();
				Method method = EditorDialog.class.getMethod("showAndWait", Identifiable.class, Stage.class);
				method.invoke(null, obj, new Stage());
				load();
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
				ExceptionDialog.show(e);
			}
		} else {
			final Optional<String> result = MetadataEditorDialog.showAndWait(null, getMetaName());

			result.ifPresent((value) -> {
				try {
					T newMetaClass = cls.newInstance();
					newMetaClass.setDescription(value);
					em.getTransaction().begin();
					em.persist(newMetaClass);
					em.getTransaction().commit();

					load();
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			});
		}
	}

	public void edit(final ActionEvent actionEvent) {
		final MetaClass metaClassObj = metaClassTable.getSelectionModel().getSelectedItem();

		Identifiable.Annotation annotation = cls.getAnnotation(Identifiable.Annotation.class);

		if (annotation != null) {
			try {
				Method method = EditorDialog.class.getMethod("showAndWait", Identifiable.class, Stage.class);
				method.invoke(null, metaClassObj, new Stage());
				load();
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				ExceptionDialog.show(e);
			}
		} else {
			final Optional<String> result = MetadataEditorDialog.showAndWait(metaClassObj, getMetaName());

			result.ifPresent((value) -> {
				em.getTransaction().begin();
				T existingGender = em.find(cls, metaClassObj.getId());
				existingGender.setDescription(value);
				em.getTransaction().commit();

				load();
			});
		}
	}

	public void remove(final ActionEvent actionEvent) {
		final MetaClass metaClassObj = metaClassTable.getSelectionModel().getSelectedItem();
		final Optional<ButtonType> result = RemoveDialog.showAndWait(metaClassObj, getMetaName());

		if (result.get() == ButtonType.OK) {
			try {
				em.getTransaction().begin();
				T obsoleteGender = em.find(cls, metaClassObj.getId());
				em.remove(obsoleteGender);
				em.getTransaction().commit();

				load();
			} catch (RollbackException e) {
				RemoveDialog.showFailureAndWait(metaClassObj, getMetaName(), e);
			}
		}
	}

	public abstract String getMetaName();
}
