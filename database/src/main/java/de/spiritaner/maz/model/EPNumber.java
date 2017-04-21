package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.yearabroad.EPNumberEditorDialogController;
import javafx.beans.property.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = EPNumberEditorDialogController.class, identifiableName = "EP-Nummer")
@NamedQueries({
		  @NamedQuery(name = "EPNumber.findAll", query = "SELECT epn FROM EPNumber epn"),
		  @NamedQuery(name = "EPNumber.findAllWithoutSite", query = "SELECT epn FROM EPNumber epn WHERE epn.site IS NULL")
})
public class EPNumber implements Identifiable {

	private LongProperty id;
	private IntegerProperty number;
	private StringProperty description;
	private ObjectProperty<Site> site;

	public EPNumber() {
		id = new SimpleLongProperty();
		number = new SimpleIntegerProperty();
		description = new SimpleStringProperty();
		site = new SimpleObjectProperty<>();
	}

	@Override
	@Id
	@GeneratedValue
	public Long getId() {
		return id.getValue();
	}

	public LongProperty idProperty() {
		return id;
	}

	public void setId(long id) {
		this.id.set(id);
	}

	@Column(nullable = false, unique = true)
	public Integer getNumber() {
		return number.getValue();
	}

	public IntegerProperty numberProperty() {
		return number;
	}

	public void setNumber(int number) {
		this.number.set(number);
	}

	@Column(nullable = false)
	public String getDescription() {
		return description.get();
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "siteId")
	public Site getSite() {
		return site.get();
	}

	public void setSite(Site site) {
		this.site.set(site);
	}

	public ObjectProperty<Site> siteProperty() {
		return site;
	}

	@Transient
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof EPNumber) && ((EPNumber) obj).getId().equals(this.getId());
	}

	@Transient
	public String toString() {
		return /*getId() + " - " + */getNumber() + " - " + getDescription();
	}
}
