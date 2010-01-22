// Copyright 2007, 2008 The Apache Software Foundation
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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.tapestry5.ioc.services.ThreadCleanupListener;

import org.tynamo.jpa2.JPAEntityManagerSource;
import org.tynamo.jpa2.JPATransactionManager;

public class JPATransactionManagerImpl implements JPATransactionManager, ThreadCleanupListener
{
	private final EntityManager entityManager;

	private EntityTransaction transaction;

	public JPATransactionManagerImpl(JPAEntityManagerSource source)
	{
		entityManager = source.create();

		startNewTransaction();
	}

	private void startNewTransaction()
	{
		// transaction = session.beginTransaction();
		transaction = entityManager.getTransaction();
		transaction.begin();
	}

	public void abort()
	{
		transaction.rollback();
		startNewTransaction();
	}

	public void commit()
	{
		transaction.commit();
		startNewTransaction();
	}

	public EntityManager getEntityManager()
	{
		return entityManager;
	}

	/**
	 * Rollsback the transaction at the end of the request, then closes the session. This means that
	 * any uncommitted changes are lost; code should inject the HSM and invoke {@link #commit()}
	 * after making any changes, if they should persist.
	 */
	public void threadDidCleanup()
	{
		transaction.rollback();

		entityManager.close();
	}
}
