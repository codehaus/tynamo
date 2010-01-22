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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.apache.tapestry5.ioc.internal.util.Defense;

/**
 * A simple implementation of {@link org.apache.tapestry5.grid.GridDataSource} based on a Hibernate
 * Session and a known entity class. This implementation does support multiple
 * {@link org.apache.tapestry5.grid.SortConstraint sort constraints}; however it assumes a direct
 * mapping from sort constraint property to Hibernate property.
 * <p/>
 * This class is <em>not</em> thread-safe; it maintains internal state.
 * <p/>
 * Typically, an instance of this object is created fresh as needed (that is, it is not stored
 * between requests).
 * 
 * @param <E>
 */
public class JPAGridDataSource<E> implements GridDataSource
{
	private final EntityManager entityManager;

	private final Class<E> entityType;

	private int startIndex;

	private List preparedResults;

	public JPAGridDataSource(EntityManager em, Class<E> entityType)
	{
		Defense.notNull(em, "entityManager");
		Defense.notNull(entityType, "entityType");

		this.entityManager = em;
		this.entityType = entityType;
	}

	/**
	 * Returns the total number of rows for the configured entity type.
	 */
	public int getAvailableRows()
	{
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> resultQuery = cb.createQuery(Long.class);
		Root<E> all = resultQuery.from(entityType);
		Expression<Boolean> query;

		query = additionalConstraints(all);

		if (query != null)
		{
			resultQuery.where(query).select(cb.count(all));
		}
		else
		{
			resultQuery.select(cb.count(all));
		}

		TypedQuery<Long> finalQ = entityManager.createQuery(resultQuery);
		Long result = finalQ.getSingleResult();

		return result.intValue();
	}

	/**
	 * Prepares the results, performing a query (applying the sort results, and the provided start
	 * and end index). The results can later be obtained from {@link #getRowValue(int)} .
	 * 
	 * @param startIndex
	 *            index, from zero, of the first item to be retrieved
	 * @param endIndex
	 *            index, from zero, of the last item to be retrieved
	 * @param sortConstraints
	 *            zero or more constraints used to set the order of the returned values
	 */
	public void prepare(int startIndex, int endIndex, List<SortConstraint> sortConstraints)
	{
		Defense.notNull(sortConstraints, "sortConstraints");

		// We just assume that the property names in the SortContraint match the Hibernate
		// properties.

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> resultQuery = cb.createQuery(entityType);
		Root<E> all = resultQuery.from(entityType);
		Expression<Boolean> query;

		query = additionalConstraints(all);

		if (query != null)
		{
			resultQuery.where(query).select(all);
		}
		else
		{
			resultQuery.select(all);
		}

		for (SortConstraint constraint : sortConstraints)
		{

			String propertyName = constraint.getPropertyModel().getPropertyName();

			switch (constraint.getColumnSort())
			{

				case ASCENDING:

					resultQuery.orderBy(cb.asc(all.get(propertyName)));
					break;

				case DESCENDING:
					resultQuery.orderBy(cb.desc(all.get(propertyName)));
					break;

				default:
			}
		}

		TypedQuery<E> finalQ = entityManager.createQuery(resultQuery);

		finalQ.setFirstResult(startIndex);
		finalQ.setMaxResults(endIndex - startIndex + 1);

		this.startIndex = startIndex;

		preparedResults = finalQ.getResultList();
	}

	/**
	 * Invoked after the main criteria has been set up (firstResult, maxResults and any sort
	 * contraints). This gives subclasses a chance to apply additional constraints before the list
	 * of results is obtained from the criteria. This implementation does nothing and may be
	 * overridden.
	 */
	protected Expression<Boolean> additionalConstraints(Root<E> root)
	{
		return null;
	}

	/**
	 * Returns a row value at the given index (which must be within the range defined by the call to
	 * {@link #prepare(int, int, java.util.List)} ).
	 * 
	 * @param index
	 *            of object
	 * @return object at that index
	 */
	public Object getRowValue(int index)
	{
		return preparedResults.get(index - startIndex);
	}

	/**
	 * Returns the entity type, as provided via the constructor.
	 */
	public Class getRowType()
	{
		return entityType;
	}
}
