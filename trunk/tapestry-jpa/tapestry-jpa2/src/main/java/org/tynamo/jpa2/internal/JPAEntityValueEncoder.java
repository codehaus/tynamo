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

package org.tynamo.jpa2.internal;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.metamodel.EntityType;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.ioc.internal.util.Defense;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.slf4j.Logger;

public final class JPAEntityValueEncoder<E> implements ValueEncoder<E>
{
	private final EntityType<E> entityType;

	private final EntityManager em;

	private final TypeCoercer typeCoercer;

	private final Logger logger;

	private final Class idClass;

	private final PersistenceUnitUtil puu;

	public JPAEntityValueEncoder(Class<E> entityClass, EntityManager em, PropertyAccess propertyAccess,
	        TypeCoercer typeCoercer, Logger logger)
	{
		this.em = em;
		this.entityType = em.getMetamodel().entity(entityClass);
		this.typeCoercer = typeCoercer;
		this.logger = logger;
		puu = em.getEntityManagerFactory().getPersistenceUnitUtil();

		idClass = entityType.getIdType().getJavaType();
	}

	public String toClient(E value)
	{
		if (value == null)
		{
			return null;
		}

		Object id = puu.getIdentifier(value);

		if (id == null)
		{
			throw new IllegalStateException(String.format(
			        "Entity %s has an id of null; this probably means that it has not been persisted yet.", value));
		}
		// this.idClass=id.getClass();

		return this.typeCoercer.coerce(id, String.class);
	}

	@SuppressWarnings("unchecked")
	public E toValue(String clientValue)
	{
		if (InternalUtils.isBlank(clientValue))
		{
			return null;
		}

		Object id = this.typeCoercer.coerce(clientValue, this.idClass);

		Serializable ser = Defense.cast(id, Serializable.class, "id");

		E result = this.em.find(this.entityType.getJavaType(), ser);

		if (result == null)
		{
			// We don't identify the entity type in the message because the logger is based on the
			// entity type.
			this.logger.error(String
			        .format("Unable to convert client value '%s' into an entity instance.", clientValue));
		}

		return result;
	}

}
