package org.tynamo.examples.simple.entities;

import org.hibernate.validator.constraints.Length;
import org.tynamo.descriptor.annotation.beaneditor.ListPageBeanModel;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Entity
@ListPageBeanModel
public class Car implements Serializable
{

	private CarPk id = new CarPk();

	private Person owner;

	private String notes;

	@EmbeddedId
	public CarPk getId()
	{
		return id;
	}

	public void setId(CarPk id)
	{
		this.id = id;
	}

	@Length(max = 350)
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
