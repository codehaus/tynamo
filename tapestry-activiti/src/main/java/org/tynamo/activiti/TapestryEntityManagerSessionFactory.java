package org.tynamo.activiti;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.variable.EntityManagerSession;
import org.activiti.engine.impl.variable.EntityManagerSessionImpl;

/**
 * Session Factory for {@link EntityManagerSession}.
 *
 * Must be used when the {@link EntityManagerFactory} is managed by Tapestry.
 * This implementation will retrieve the {@link EntityManager} bound to the
 * thread by Tapestry in case a transaction already started.
 *
 */
public class TapestryEntityManagerSessionFactory implements SessionFactory {
	protected EntityManager entityManager;
	protected EntityManagerFactory entityManagerFactory;
	protected boolean handleTransactions;
	protected boolean closeEntityManager;

	public TapestryEntityManagerSessionFactory(EntityManager entityManager, Object entityManagerFactory, boolean handleTransactions, boolean closeEntityManager) {
		this.entityManager = entityManager;
		this.entityManagerFactory = (EntityManagerFactory) entityManagerFactory;
		this.handleTransactions = handleTransactions;
		this.closeEntityManager = closeEntityManager;
	}

	public Class<?> getSessionType() {
		return EntityManagerSession.class;
	}

	public Session openSession() {
		if (entityManager == null)
			return new EntityManagerSessionImpl(entityManagerFactory, handleTransactions, closeEntityManager);

		return new EntityManagerSessionImpl(entityManagerFactory, entityManager, false, false);
	}
}