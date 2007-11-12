package org.trails.engine.encoders.abbreviator;

/**
 * @author James Carman
 */
public interface EntityNameAbbreviator
{

	public String getAbbreviation(Class clazz);

	public Class getEntityName(String abbreviation);
}
