package org.tynamo.examples.simple.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.eclipse.persistence.annotations.Index;


@Entity
public class Category
{
	private Integer id;
	private String description;
	private String name;
	private List<Product> products = new ArrayList<Product>();

	@NotNull
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

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

	public String toString()
	{
		return getDescription();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Category category = (Category) o;

		return getId() != null ? getId().equals(category.getId()) : category.getId() == null;

	}

	@Override
	public int hashCode()
	{
		return (getId() != null ? getId().hashCode() : 0);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@OneToMany
	@JoinColumn(name = "CATEGORY_ID")
	@Index(name = "PRODUCT_INDEX")
	public List<Product> getProducts()
	{
		return products;
	}

	/**
	 * @param products The products to set.
	 */
	public void setProducts(List<Product> products)
	{
		this.products = products;
	}
}
