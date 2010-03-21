package org.tynamo.examples.recipe.pages.list;

/**
 * Abstract parent class for customized List pages.
 *
 * @param <T>
 */
public abstract class CustomList<T> extends org.tynamo.pages.List {

	public abstract Class<T> getType();

	final protected void onActivate() throws Exception {
		super.onActivate(getType());
	}
}
