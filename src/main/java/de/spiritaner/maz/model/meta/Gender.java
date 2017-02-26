package de.spiritaner.maz.model.meta;

import javax.persistence.*;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "Gender.findAll", query = "SELECT g FROM Gender g"),
})
public class Gender extends MetaClass {

}
