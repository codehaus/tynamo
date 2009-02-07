package org.trails.demo;

import org.trails.builder.Builder;


public class RecipeBuilder implements Builder<Recipe>
{

	public Recipe build()
	{
		Recipe recipe = new Recipe();
		recipe.setDescription("Add your description here...");
		return recipe;
	}
}
