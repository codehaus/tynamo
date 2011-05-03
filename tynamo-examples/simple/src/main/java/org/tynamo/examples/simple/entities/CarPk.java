package org.tynamo.examples.simple.entities;

import org.tynamo.PageType;
import org.tynamo.descriptor.annotation.beaneditor.BeanModel;
import org.tynamo.descriptor.annotation.beaneditor.BeanModels;
import org.tynamo.descriptor.annotation.extension.InitialValue;
import org.tynamo.descriptor.annotation.extension.PossibleValues;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Embeddable
@BeanModels({
		@BeanModel(pageType = PageType.LIST, exclude = "make")
})
public class CarPk implements Serializable
{

	private String name;

	@NotNull
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	private Make make;

	private Model model;

	@Transient
	@InitialValue("model.make")
	public Make getMake()
	{
		return make;
	}

	public void setMake(Make make)
	{
		this.make = make;
	}

	@NotNull
	@ManyToOne(optional = false)
	@PossibleValues("make.models")
	public Model getModel()
	{
		return model;
	}

	public void setModel(Model model)
	{
		this.model = model;
	}

	public String toString()
	{
		return getModel() == null ? null : getModel().toString() + ", " + getModel().getMake().toString() + ", " + name;
	}

}
