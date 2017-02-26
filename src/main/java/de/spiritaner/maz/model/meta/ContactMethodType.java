package de.spiritaner.maz.model.meta;


import de.spiritaner.maz.model.ContactMethod;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;
import java.util.List;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
public class ContactMethodType extends MetaClass {

	private List<ContactMethod> contactMethods;

	/**
	 * All contact methods that use this contact method type.
	 */
	@OneToMany(mappedBy = "contactMethodType", fetch = FetchType.LAZY)
	public List<ContactMethod> getContactMethods() { return contactMethods; }
	public void setContactMethods(List<ContactMethod> contactMethods) { this.contactMethods = contactMethods; }
}
