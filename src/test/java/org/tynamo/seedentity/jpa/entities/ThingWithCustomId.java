package org.tynamo.seedentity.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ThingWithCustomId
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "my_id")
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
