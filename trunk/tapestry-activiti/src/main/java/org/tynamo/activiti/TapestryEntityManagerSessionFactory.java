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
	private boolean handleTransactions;
	private boolean closeEntityManager;

	public TapestryEntityManagerSessionFactory(EntityManager em,
	                                           @Symbol(ActivitiSymbols.JPA_HANDLE_TRANSACTION) boolean handleTransactions,
	                                           @Symbol(ActivitiSymbols.JPA_CLOSE_ENTITY_MANAGER) boolean closeEntityManager) {
		this.em = em;
		this.handleTransactions = handleTransactions;
		this.closeEntityManager = closeEntityManager;
	}

	public Class<?> getSessionType() {
		return EntityManagerSession.class;
	}

	public Session openSession() {
		if (handleTransactions) {    
                    return new EntityManagerSessionImpl(em.getEntityManagerFactory(), handleTransactions, closeEntityManager);                    
                } else {
                    return new EntityManagerSessionImpl(em.getEntityManagerFactory(), em, false, false) ;
                }
	}
}