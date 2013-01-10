package org.tynamo.examples.simple.entities;

import javax.persistence.Entity;

import org.tynamo.descriptor.annotation.PropertyDescriptor;
import org.tynamo.model.elasticsearch.annotations.ElasticSearchField;
import org.tynamo.model.elasticsearch.annotations.ElasticSearchable;

@ElasticSearchable
@Entity
public class Apple extends Fruit
{

	private String color;

	private String history;

	@ElasticSearchField
	public String getColor()
	{
		return color;
	}

	public void setColor(String color)
	{
		this.color = color;
	}

	@PropertyDescriptor(richText = true)
	@ElasticSearchField
	public String getHistory()
	{
		return history;
	}

	public void setHistory(String history)
	{
		this.history = history;
	}
}
