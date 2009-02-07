package org.trails.builder;

/**
 * Fulfils the "Director" role in the Trails implementation of
 * GOF's <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder pattern</a>
 * <p/>
 * Constructs an object using the Builder interface
 */
public interface BuilderDirector
{
	/**
	 * Create a new instance of an object of class 'type' using a Builder.
	 *
	 * @param type is a class whose instance should be created
	 * @return a newly created object
	 */
	<T> T createNewInstance(Class<T> type);
}
