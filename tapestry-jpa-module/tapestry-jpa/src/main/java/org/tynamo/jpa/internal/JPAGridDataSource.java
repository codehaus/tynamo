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

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;


/**
 * A simple implementation of {@link org.apache.tapestry5.grid.GridDataSource} based on a
 * EntityManager Session and a known entity class. This implementation does support multiple {@link
 * org.apache.tapestry5.grid.SortConstraint sort constraints}; however it assumes a direct mapping
 * from sort constraint property to Hibernate property.
 * <p/>
 * <p/>This class is <em>not</em> thread-safe; it maintains internal state.</p>
 * <p/>
 * <p>Typically, an instance of this object is created fresh as needed (that is, it is not stored
 * between requests).</p>
 *
 * @param <E>
 */
public class JPAGridDataSource<E> implements GridDataSource
{

	//~ Instance fields ----------------------------------------------------------------------------

	/**
	 * EntityManager we're doing work for
	 */
	protected final EntityManager entityManager;

	/**
	 * EntityType we're fetching
	 */
	private final Class<E> entityType;

	/**
	 * starting index
	 */
	private int startIndex;

	/**
	 * The prepared results
	 */
	private List preparedResults;

	//~ Constructors -------------------------------------------------------------------------------

	/**
	 * Creates a new JPAGridDataSource object.
	 *
	 * @param em         EntityManager to use
	 * @param entityType Entity to query for
	 */
	public JPAGridDataSource(EntityManager em, Class<E> entityType)
	{
		assert em != null;
		assert entityType != null;

		this.entityManager = em;
		this.entityType = entityType;
	}

	//~ Methods ------------------------------------------------------------------------------------

	/**
	 * Returns the total number of rows for the configured entity type.
	 *
	 * @return total number of rows
	 */
	@Override
	public int getAvailableRows()
	{
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> resultQuery = cb.createQuery(Long.class);
		Root<E> all = resultQuery.from(entityType);
		Expression<Boolean> query;

		query = additionalConstraints(cb, all);

		if (query != null)
		{
			resultQuery.where(query).select(cb.count(all));
		} else
		{
			resultQuery.select(cb.count(all));
		}

		TypedQuery<Long> finalQ = entityManager.createQuery(resultQuery);
		Long result = finalQ.getSingleResult();

		return result.intValue();
	}

	/**
	 * Turn a list with things to follow into a Path object for the order by clause
	 *
	 * @param all  root to start from
	 * @param path property name/path
	 * @return a JPA Criteria API Path object
	 */
	public Path buildPath(Root<E> all, String path)
	{
		path = path.replaceAll("\\?\\.", ".");

		Path start = all;

		for (String word : path.split("\\."))
		{
			start = start.get(word);
		}

		return start;
	}

	/**
	 * Prepares the results, performing a query (applying the sort results, and the provided start
	 * and end index). The results can later be obtained from {@link #getRowValue(int)} .
	 *
	 * @param startIndex      index, from zero, of the first item to be retrieved
	 * @param endIndex        index, from zero, of the last item to be retrieved
	 * @param sortConstraints zero or more constraints used to set the order of the returned
	 *                        values
	 */
	@Override
	public void prepare(int startIndex, int endIndex, List<SortConstraint> sortConstraints)
	{
		assert sortConstraints != null;

		// We just assume that the property names in the SortContraint match the Hibernate
		// properties.

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> resultQuery = cb.createQuery(entityType);
		Root<E> all = resultQuery.from(entityType);
		Expression<Boolean> query;

		query = additionalConstraints(cb, all);

		if (query != null)
		{
			resultQuery.where(query).select(all);
		} else
		{
			resultQuery.select(all);
		}

		for (SortConstraint constraint : sortConstraints)
		{

			String propertyName = constraint.getPropertyModel().getPropertyName();

			switch (constraint.getColumnSort())
			{

				case ASCENDING:

					resultQuery.orderBy(cb.asc(buildPath(all, propertyName)));

					break;

				case DESCENDING:
					resultQuery.orderBy(cb.desc(buildPath(all, propertyName)));

					break;

				default:
			}
		}

		TypedQuery<E> finalQ = entityManager.createQuery(resultQuery);

		finalQ.setFirstResult(startIndex);
		finalQ.setMaxResults(endIndex - startIndex + 1);

		this.startIndex = startIndex;

		preparedResults = finalQ.getResultList();
	} // end method prepare

	/**
	 * Invoked after the main criteria has been set up (firstResult, maxResults and any sort
	 * contraints). This gives subclasses a chance to apply additional constraints before the list
	 * of results is obtained from the criteria. This implementation does nothing and may be
	 * overridden.
	 *
	 * @param cb   CriteriaBuilder to use for building constraints
	 * @param root Root object to use
	 * @return A boolean expression to use for filtering
	 */
	protected Expression<Boolean> additionalConstraints(CriteriaBuilder cb, Root<E> root)
	{
		return null;
	}

	/**
	 * Returns a row value at the given index (which must be within the range defined by the call to
	 * {@link #prepare(int, int, java.util.List)} ).
	 *
	 * @param index of object
	 * @return object at that index
	 */
	@Override
	public Object getRowValue(int index)
	{
		return preparedResults.get(index - startIndex);
	}

	/**
	 * Returns the entity type, as provided via the constructor.
	 *
	 * @return entity type
	 */
	@Override
	public Class getRowType()
	{
		return entityType;
	}
} // end class JPAGridDataSource
