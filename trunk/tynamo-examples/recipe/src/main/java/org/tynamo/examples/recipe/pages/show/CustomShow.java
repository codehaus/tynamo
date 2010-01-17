package org.tynamo.examples.recipe.pages.show;

import org.tynamo.examples.recipe.pages.Show;


/**
 * Abstract parent class for customized Show pages. 
 *
 * @param <T>
 */
public abstract class CustomShow<T> extends Show
{

	public abstract Class<T> getType();

	/**
	 * NOTE:
	 * <p/>
	 * Tapestry 5 does not support generics in method parameters, just fields.
	 * So, it's not possible to add something like:
	 * <pre>
	 * protected abstract void onActivate(T object) throws Exception;
	 * </pre>
	 * <p/>
	 * For Tapestry, you would not be overriding onActivate, you simply would have two different versions of it.
	 * http://n2.nabble.com/onActivate-called-twice-when-overriding-from-abstract-generic-superclass-td586590.html
	 *
	 */
/*
	protected void onActivate(T object) throws Exception
	{
		activate(object);
	}
*/
	protected void activate(T bean) throws Exception
	{
		activate(bean, getDescriptorService().getClassDescriptor(getType()), createBeanModel(getType()));
	}

	@Override
	protected Object[] onPassivate()
	{
		return new Object[]{getBean()};
	}
}