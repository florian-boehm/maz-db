package de.spiritaner.maz.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Florian on 7/28/2016.
 */

public class Gender {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String description;
}
