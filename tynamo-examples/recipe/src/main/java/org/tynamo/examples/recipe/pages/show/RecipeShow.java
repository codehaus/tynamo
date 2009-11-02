package org.tynamo.examples.recipe.pages.show;


import org.tynamo.descriptor.CollectionDescriptor;
import org.tynamo.examples.recipe.model.Recipe;
import org.tynamo.examples.recipe.pages.Show;

public class RecipeShow extends Show
{

	protected void onActivate(Recipe object) throws Exception
	{
		activate(object, getDescriptorService().getClassDescriptor(Recipe.class), createBeanModel(Recipe.class));
	}

	@Override
	protected Object[] onPassivate()
	{
		return new Object[]{getBean()};
	}

	public Recipe getRecipe()
	{
		return (Recipe) getBean();
	}

	public CollectionDescriptor getCollectionDescriptor()
	{
		return (CollectionDescriptor) getClassDescriptor().getPropertyDescriptor("ingredients");
	}
}