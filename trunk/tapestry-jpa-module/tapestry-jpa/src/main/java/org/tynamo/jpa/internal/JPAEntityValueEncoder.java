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

package org.tynamo.jpa.internal;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.ioc.services.PropertyAdapter;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.slf4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.io.Serializable;


/**
 * Value encoder for going to/from entities via their primary key. Currently only works if the
 * primary key is single valued.
 *
 * @author Pierce T. Wetter III, but really just cribbed from the tapestry-hibernate version
 */
public final class JPAEntityValueEncoder<E> implements ValueEncoder<E>
{

	//~ Instance fields ----------------------------------------------------------------------------

	/**
	 * JPA MetaModel entityType
	 */
	private final EntityType<E> entityType;

	/**
	 * JPA EntityManager to use
	 */
	private final EntityManager em;

	/**
	 * Tapestry TypeCoercer
	 */
	private final TypeCoercer typeCoercer;

	/**
	 * A logger
	 */
	private final Logger logger;

	/**
	 * Class of the primary key
	 */
	private final Class idClass;

	/**
	 * Class of the entity
	 */
	private final Class entityClass;

	/**
	 * Property Adaptor
	 */
	private final PropertyAdapter propertyAdapter;

	/**
	 * Property name of primary key
	 */
	private final String idPropertyName;

	//~ Constructors -------------------------------------------------------------------------------

	/**
	 * Creates a new JPAEntityValueEncoder object.
	 *
	 * @param entityClass    Class for entity
	 * @param em             EntityManager to use
	 * @param propertyAccess PropertyAccess from tapestry
	 * @param typeCoercer    typeCoercer from tapestry
	 * @param logger         Logger to use
	 */
	public JPAEntityValueEncoder(
			Class<E> entityClass, EntityManager em, PropertyAccess propertyAccess,
			TypeCoercer typeCoercer, Logger logger
	)
	{
		this.em = em;
		this.entityType = em.getMetamodel().entity(entityClass);
		this.typeCoercer = typeCoercer;
		this.logger = logger;
		this.entityClass = entityClass;

		idClass = entityType.getIdType().getJavaType();

		this.idPropertyName = this.entityType.getId(idClass).getName();

		propertyAdapter = propertyAccess.getAdapter(entityClass).getPropertyAdapter(idPropertyName);
	}

	//~ Methods ------------------------------------------------------------------------------------

	@Override
	public String toClient(E value)
	{

		if (value == null)
		{
			return null;
		}

		Object id = propertyAdapter.get(value);

		if (id == null)
		{
			throw new IllegalStateException(
					String.format(
							"Entity %s has an id of null; this probably means that it has not been persisted yet.",
							value
					)
			);
		}
		// this.idClass=id.getClass();

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

			id = typeCoercer.coerce(clientValue, propertyAdapter.getType());
		}
		catch (Exception ex)
		{
			throw new RuntimeException(
					String.format(
							"Exception converting '%s' to instance of %s (id type for entity %s): %s",
							clientValue,
							propertyAdapter.getType().getName(), entityClass.getName(),
							InternalUtils.toMessage(ex)
					), ex
			);
		}

		Serializable ser = (Serializable) id;

		E result = this.em.find(this.entityType.getJavaType(), ser);

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

} // end class JPAEntityValueEncoder
