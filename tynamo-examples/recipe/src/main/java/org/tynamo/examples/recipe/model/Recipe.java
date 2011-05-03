/*
 * Created on Feb 6, 2005
 *
 * Copyright 2004 Chris Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.tynamo.examples.recipe.model;

import org.hibernate.validator.constraints.Length;
import org.tynamo.PageType;
import org.tynamo.blob.RenderType;
import org.tynamo.blob.TynamoBlobImpl;
import org.tynamo.descriptor.annotation.BlobDescriptor;
import org.tynamo.descriptor.annotation.CollectionDescriptor;
import org.tynamo.descriptor.annotation.PropertyDescriptor;
import org.tynamo.descriptor.annotation.beaneditor.BeanModel;
import org.tynamo.descriptor.annotation.beaneditor.BeanModels;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@BeanModels({
		@BeanModel(pageType = PageType.LIST, include = "title, description, instructions, date")
})
public class Recipe
{
	private Long id;

	private String title;

	private String description;

	private String instructions;

	private Date date;

	private TynamoBlobImpl photo = new TynamoBlobImpl();

	@PropertyDescriptor(nonVisual = true)
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}


	@NotNull(message = "{error.emptyMessage}")
	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	@PropertyDescriptor(format = "MM/dd/yyyy")
	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	private Category category;

	@ManyToOne
	public Category getCategory()
	{
		return category;
	}

	public void setCategory(Category category)
	{
		this.category = category;
	}

	@Length(max = 500)
	public String getInstructions()
	{
		return instructions;
	}

	public void setInstructions(String instructions)
	{
		this.instructions = instructions;
	}

	private Set<Ingredient> ingredients = new HashSet<Ingredient>();

	@OneToMany(mappedBy = "recipe")
	@CollectionDescriptor(addExpression = "addIngredient", removeExpression = "removeIngredient")
	// The standard EJB annotations don't have the delete orphan option.
	@org.hibernate.annotations.Cascade(
			{org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
	public Set<Ingredient> getIngredients()
	{
		return ingredients;
	}

	public void setIngredients(Set<Ingredient> ingredients)
	{
		this.ingredients = ingredients;
	}

	public void addIngredient(Ingredient ingredient)
	{
		ingredient.setRecipe(this);
		ingredients.add(ingredient);
	}

	public void removeIngredient(Ingredient ingredient)
	{
		ingredient.setRecipe(null);
		ingredients.remove(ingredient);
	}

	@Lob
	@BlobDescriptor(renderType = RenderType.IMAGE)
	public TynamoBlobImpl getPhoto()
	{
		return photo;
	}

	public void setPhoto(TynamoBlobImpl photo)
	{
		this.photo = photo;
	}
}
