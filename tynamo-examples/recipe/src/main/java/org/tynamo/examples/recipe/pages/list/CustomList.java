package org.tynamo.examples.recipe.pages.list;

import org.tynamo.examples.recipe.pages.List;


/**
 * Abstract parent class for customized List pages. 
 *
 * @param <T>
 */
public abstract class CustomList<T> extends List
{
	public abstract Class<T> getType();

	protected void onActivate() throws Exception
	{
		super.onActivate(getType());
	}
}
