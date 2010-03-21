package org.tynamo.examples.simple.pages.add;

/**
 * Abstract parent class for customized Add pages.
 *
 * @param <T>
 */
public abstract class CustomAdd<T> extends org.tynamo.pages.Add {

	public abstract Class<T> getType();

	final protected void onActivate() throws Exception {
		super.onActivate(getType());
	}
}