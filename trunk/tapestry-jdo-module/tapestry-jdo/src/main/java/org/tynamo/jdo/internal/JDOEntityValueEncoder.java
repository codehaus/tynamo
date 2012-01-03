// Copyright 2008 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.tynamo.jdo.internal;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.ioc.services.PropertyAdapter;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.slf4j.Logger;


import javax.jdo.PersistenceManager;

import java.io.Serializable;
import javax.jdo.JDOHelper;
import javax.jdo.metadata.TypeMetadata;


/**
 * Value encoder for going to/from entities via their primary key. Currently only works if the
 * primary key is single valued.
 *
 */
public final class JDOEntityValueEncoder<E> implements ValueEncoder<E>
{

	//~ Instance fields ----------------------------------------------------------------------------

	/**
	 * JDO PersistenceManager to use
	 */
	private final PersistenceManager pm;

	/**
	 * Tapestry TypeCoercer
	 */
	private final TypeCoercer typeCoercer;

	/**
	 * A logger
	 */
	private final Logger logger;


	/**
	 * Class of the entity
	 */
	private final Class persistenceCapableClass;
    
    /**
     * The Object ID class 
     */
    private final Class objectIdClass;
	

	//~ Constructors -------------------------------------------------------------------------------

	/**
	 * Creates a new JDOEntityValueEncoder object.
	 *
	 * @param pcClass    Class for entity
	 * @param pm             EntityManager to use
	 * @param propertyAccess PropertyAccess from tapestry
	 * @param typeCoercer    typeCoercer from tapestry
	 * @param logger         Logger to use
	 */
	public JDOEntityValueEncoder(
			Class<E> pcClass, PersistenceManager pm, PropertyAccess propertyAccess,
			TypeCoercer typeCoercer, Logger logger
	)
	{
		this.pm = pm;
        
		this.typeCoercer = typeCoercer;
		this.logger = logger;
		this.persistenceCapableClass = pcClass;
        this.objectIdClass = pm.getObjectIdClass(pcClass);
        
	}

	//~ Methods ------------------------------------------------------------------------------------

	@Override
	public String toClient(E value)
	{

		if (value == null)
		{
			return null;
		}

		Object id = JDOHelper.getObjectId(value);

		if (id == null)
		{
			throw new IllegalStateException(
					String.format(
							"PersistenceCapable %s has an id of null; this probably means that it has not been persisted yet.",
							value
					)
			);
		}		

		return this.typeCoercer.coerce(id, String.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public E toValue(String clientValue)
	{

		if (InternalUtils.isBlank(clientValue))
		{
			return null;
		}

		Object id = null;

		try
		{                        
			id = typeCoercer.coerce(clientValue, objectIdClass);
		}
		catch (Exception ex)
		{
			throw new RuntimeException(
					String.format(
							"Exception converting '%s' to instance of %s (id type for entity %s): %s",
							clientValue,
							objectIdClass.getName(), persistenceCapableClass.getName(),
							InternalUtils.toMessage(ex)
					), ex
			);
		}

		Serializable ser = (Serializable) id;

		E result = (E)this.pm.getObjectById(this.persistenceCapableClass, ser);

		if (result == null)
		{
			// We don't identify the entity type in the message because the logger is based on the
			// entity type.
			this.logger.error(
					String.format(
							"Unable to convert client value '%s' into an entity instance.", clientValue
					)
			);
		}

		return result;
	}

} // end class JDOEntityValueEncoder
