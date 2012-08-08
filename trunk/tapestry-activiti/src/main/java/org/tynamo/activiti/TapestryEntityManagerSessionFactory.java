package org.tynamo.activiti;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.variable.EntityManagerSession;
import org.activiti.engine.impl.variable.EntityManagerSessionImpl;
import org.apache.tapestry5.ioc.annotations.Symbol;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Session Factory for {@link EntityManagerSession}.
 * <p/>
 * Must be used when the {@link EntityManagerFactory} is managed by Tapestry.
 * This implementation will retrieve the {@link EntityManager} bound to the
 * thread by Tapestry in case a transaction already started.
 */
public class TapestryEntityManagerSessionFactory implements SessionFactory {

	private EntityManager em;
	private EntityManagerFactory emf;
	private boolean handleTransactions;
	private boolean closeEntityManager;

	public TapestryEntityManagerSessionFactory(EntityManager em,
	                                           EntityManagerFactory emf,
	                                           @Symbol(ActivitiSymbols.JPA_HANDLE_TRANSACTION) boolean handleTransactions,
	                                           @Symbol(ActivitiSymbols.JPA_CLOSE_ENTITY_MANAGER) boolean closeEntityManager) {
		this.em = em;
		this.emf = emf;
		this.handleTransactions = handleTransactions;
		this.closeEntityManager = closeEntityManager;
	}

	public Class<?> getSessionType() {
		return EntityManagerSession.class;
	}

	public Session openSession() {
		return em != null ?
				new EntityManagerSessionImpl(emf, em, false, false) :
				new EntityManagerSessionImpl(emf, handleTransactions, closeEntityManager);
	}
}