package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.participation.ParticipationEditorDialogController;
import de.spiritaner.maz.controller.role.RoleEditorDialogController;
import de.spiritaner.maz.model.meta.RoleType;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = RoleEditorDialogController.class, identifiableName = "Funktion")
public class Role implements Identifiable {

	private LongProperty id;
	private ObjectProperty<Person> person;
	private ObjectProperty<RoleType> roleType;

	public Role() {
		id = new SimpleLongProperty();
		person = new SimpleObjectProperty<>();
		roleType = new SimpleObjectProperty<>();
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id.get();
	}
	public void setId(Long id) {
		this.id.set(id);
	}
	public LongProperty idProperty() { return id; }

	/**
	 * The person that owns this role.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="personId", nullable = false)
	public Person getPerson() {
		return person.get();
	}
	public void setPerson(Person person) {
		this.person.set(person);
	}

	public ObjectProperty<Person> personProperty() {
		return person;
	}

	/**
	 * The role type that this role is using.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "roleTypeId", nullable = false)
	public RoleType getRoleType() {
		return roleType.get();
	}
	public void setRoleType(RoleType roleType) {
		this.roleType.set(roleType);
	}

	public ObjectProperty<RoleType> roleTypeProperty() {
		return roleType;
	}

	@Override
	@Transient
	public boolean equals(Object obj) {
		return (obj instanceof Role) && (this.getId().equals(((Role) obj).getId()));
	}
}
