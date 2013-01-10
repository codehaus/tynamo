package org.tynamo.examples.simple.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

//@Indexed
@Entity
public class Car implements Serializable
{

	private Integer id;

	private Person owner;

	private String notes;

//	@DocumentId
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId()
	{
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(Integer id)
	{
		this.id = id;
	}


	public String getNotes()
	{
		return notes;
	}

	public void setNotes(String notes)
	{
		this.notes = notes;
	}

	@OneToOne
	public Person getOwner()
	{
		return owner;
	}

	public void setOwner(Person owner)
	{
		this.owner = owner;
	}
}
