package org.tynamo.examples.recipe.pages.show;


import org.tynamo.descriptor.CollectionDescriptor;
import org.tynamo.examples.recipe.model.Recipe;

public class RecipeShow extends CustomShow<Recipe> {

	@Override
	public Class<Recipe> getType() {
		return Recipe.class;
	}

	public Recipe getRecipe() {
		return (Recipe) getBean();
	}

	public CollectionDescriptor getCollectionDescriptor() {
		return (CollectionDescriptor) getClassDescriptor().getPropertyDescriptor("ingredients");
	}
}