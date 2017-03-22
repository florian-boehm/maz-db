package de.spiritaner.maz.util.envers;

import de.spiritaner.maz.model.Identifiable;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.meta.Diocese;
import de.spiritaner.maz.model.meta.Gender;
import de.spiritaner.maz.util.DataDatabase;
import javafx.scene.control.ListCell;

import javax.persistence.EntityManager;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RevisionEntity<T extends Identifiable> {

	private T entity;
	private Number revision;
	private Date revisionDate;

	public T getEntity() {
		return entity;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}

	public Number getRevision() {
		return revision;
	}

	public void setRevision(Number revision) {
		this.revision = revision;
	}

	public Date getRevisionDate() {
		return revisionDate;
	}

	public void setRevisionDate(Date revisionDate) {
		this.revisionDate = revisionDate;
	}

	@Override
	public String toString() {
		return "Version " + getRevision() + "(" + new SimpleDateFormat("HH:mm dd.MM.yyyy").format(revisionDate) + ")";
	}

	public void initialize() {
		if(this.entity instanceof Person) {
			Person person = (Person) entity;
			EntityManager em = DataDatabase.getFactory().createEntityManager();

			if(person.getGender() != null) person.setGender(em.find(Gender.class, person.getGender().getId()));
			if(person.getDiocese() != null) person.setDiocese(em.find(Diocese.class, person.getDiocese().getId()));
		}
	}

	public String getRevisionDate(String pattern) {
		return new SimpleDateFormat(pattern).format(getRevisionDate());
	}
}
