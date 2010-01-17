package org.tynamo.examples.recipe.pages.add;

import org.tynamo.examples.recipe.pages.Add;

/**
 * Abstract parent class for customized Add pages. 
 *
 * @param <T>
 */
public abstract class CustomAdd<T> extends Add
{
	public abstract Class<T> getType();

	protected void onActivate() throws Exception
	{
		super.onActivate(getType());
	}
}