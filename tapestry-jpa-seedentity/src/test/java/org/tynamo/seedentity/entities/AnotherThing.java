/*
 * Copyright © Sartini IT Solutions, 2010.
 */
package org.tynamo.seedentity.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class AnotherThing {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String name;

	@Column(length = 300)
	private String text;

	@OneToOne
	private Thing thing;

	public Integer getId() {
		return id;
	}

	public Thing getThing() {
		return thing;
	}

	public void setThing(Thing thing) {
		this.thing = thing;
	}

	/**
	 * @param id
	 *          The id to set.
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *          The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AnotherThing thing = (AnotherThing) o;

		return getId() != null ? getId().equals(thing.getId()) : thing.getId() == null;

	}

	@Override
	public int hashCode() {
		return (getId() != null ? getId().hashCode() : 0);
	}

	public String toString() {
		return getName();
	}

}