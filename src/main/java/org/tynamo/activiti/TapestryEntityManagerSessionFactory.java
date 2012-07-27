package org.tynamo.activiti;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.variable.EntityManagerSession;
import org.activiti.engine.impl.variable.EntityManagerSessionImpl;
import org.tynamo.jpa.JPATransactionManager;

/**
 * Session Factory for {@link EntityManagerSession}.
 *
 * Must be used when the {@link EntityManagerFactory} is managed by Tapestry.
 * This implementation will retrieve the {@link EntityManager} bound to the
 * thread by Tapestry in case a transaction already started.
 *
 */
public class TapestryEntityManagerSessionFactory implements SessionFactory {
	private final JPATransactionManager transactionManager;
	protected EntityManagerFactory entityManagerFactory;
	protected boolean handleTransactions;
	protected boolean closeEntityManager;

	public TapestryEntityManagerSessionFactory(JPATransactionManager transactionManager, Object entityManagerFactory, boolean handleTransactions, boolean closeEntityManager) {
		this.transactionManager = transactionManager;
		this.entityManagerFactory = (EntityManagerFactory) entityManagerFactory;
		this.handleTransactions = handleTransactions;
		this.closeEntityManager = closeEntityManager;
	}

	public Class<?> getSessionType() {
		return EntityManagerSession.class;
	}

	public Session openSession() {
		EntityManager entityManager = transactionManager.getEntityManager();

		if (entityManager == null)
			return new EntityManagerSessionImpl(entityManagerFactory, handleTransactions, closeEntityManager);

		return new EntityManagerSessionImpl(entityManagerFactory, entityManager, false, false);
	}
}