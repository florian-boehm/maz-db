package de.spiritaner.maz.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
public class RoleType {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
