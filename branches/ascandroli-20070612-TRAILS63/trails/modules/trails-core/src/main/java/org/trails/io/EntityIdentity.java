package org.trails.io;

import java.io.Serializable;

public class EntityIdentity implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final String entityName;

	private final Serializable id;

	private final Object version;

	public EntityIdentity(String entityName, Serializable id)
	{
		this.entityName = entityName;
		this.id = id;
		this.version = null;
	}

	public EntityIdentity(String entityName, Serializable id, Object version)
	{
		this.entityName = entityName;
		this.id = id;
		this.version = version;
	}

	public String getEntityName()
	{
		return entityName;
	}

	public Serializable getId()
	{
		return id;
	}

	public Object getVersion()
	{
		return version;
	}
}
