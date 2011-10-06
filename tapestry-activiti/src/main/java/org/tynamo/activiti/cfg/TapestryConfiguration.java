package org.tynamo.activiti.cfg;

import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.variable.EntityManagerSession;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.tynamo.activiti.ActivitiSymbols;
import org.tynamo.activiti.TapestryEntityManagerSessionFactory;
import org.tynamo.activiti.TapestryExpressionManager;
import org.tynamo.jpa.JPAEntityManagerSource;
import org.tynamo.jpa.JPATransactionManager;

/**
 * Configures Activiti's {@link ProcessEngine} based on the {@link ActivitiSymbols}.
 *
 */
public class TapestryConfiguration extends StandaloneProcessEngineConfiguration {
	protected JPATransactionManager transactionManager;

	public TapestryConfiguration(
		final JPATransactionManager transactionManager,
		final JPAEntityManagerSource entityManagerSource,
		final SymbolSource symbolSource,
		final TypeCoercer typeCoercer,
		final ObjectLocator objectLocator
	) {
        this.transactionManager = transactionManager;
        this.jpaEntityManagerFactory = entityManagerSource.getEntityManagerFactory();

        setHistory(symbolSource.valueForSymbol(ActivitiSymbols.HISTORY));
		setMailServerHost(symbolSource.valueForSymbol(ActivitiSymbols.MAIL_SERVER_HOST));
		setMailServerPort(typeCoercer.coerce(symbolSource.valueForSymbol(ActivitiSymbols.MAIL_SERVER_PORT), Integer.class));
		setMailServerDefaultFrom(symbolSource.valueForSymbol(ActivitiSymbols.MAIL_SERVER_DEFAULT_FROM));
		setMailServerUsername(symbolSource.valueForSymbol(ActivitiSymbols.MAIL_SERVER_USERNAME));
		setMailServerPassword(symbolSource.valueForSymbol(ActivitiSymbols.MAIL_SERVER_PASSWORD));
		setDatabaseType(symbolSource.valueForSymbol(ActivitiSymbols.DATABASE_TYPE));
		setDatabaseSchemaUpdate(symbolSource.valueForSymbol(ActivitiSymbols.DATABASE_SCHEMA_UPDATE));
		setJdbcDriver(symbolSource.valueForSymbol(ActivitiSymbols.JDBC_DRIVER));
		setJdbcUrl(symbolSource.valueForSymbol(ActivitiSymbols.JDBC_URL));
		setJdbcUsername(symbolSource.valueForSymbol(ActivitiSymbols.JDBC_USERNAME));
		setJdbcPassword(symbolSource.valueForSymbol(ActivitiSymbols.JDBC_PASSWORD));
		setJdbcMaxActiveConnections(typeCoercer.coerce(symbolSource.valueForSymbol(ActivitiSymbols.JDBC_MAX_ACTIVE_CONNECTIONS), Integer.class));
		setJdbcMaxIdleConnections(typeCoercer.coerce(symbolSource.valueForSymbol(ActivitiSymbols.JDBC_MAX_IDLE_CONNECTIONS), Integer.class));
		setJdbcMaxCheckoutTime(typeCoercer.coerce(symbolSource.valueForSymbol(ActivitiSymbols.JDBC_MAX_CHECKOUT_TIME), Integer.class));
		setJdbcMaxWaitTime(typeCoercer.coerce(symbolSource.valueForSymbol(ActivitiSymbols.JDBC_MAX_WAIT_TIME), Integer.class));
		setJobExecutorActivate(typeCoercer.coerce(symbolSource.valueForSymbol(ActivitiSymbols.JOB_EXECUTOR_ACTIVATE), Boolean.class));
		setJpaHandleTransaction(typeCoercer.coerce(symbolSource.valueForSymbol(ActivitiSymbols.JPA_HANDLE_TRANSACTION), Boolean.class));
		setJpaCloseEntityManager(typeCoercer.coerce(symbolSource.valueForSymbol(ActivitiSymbols.JPA_CLOSE_ENTITY_MANAGER), Boolean.class));

		//Allows Tapestry Services to be looked up by service id
		setExpressionManager(new TapestryExpressionManager(objectLocator));

		String unitName = symbolSource.valueForSymbol(ActivitiSymbols.JPA_PERSISTENCE_UNIT_NAME);
		if (!"".equals(unitName))
			setJpaPersistenceUnitName(unitName);
	}

	@Override
	protected void initJpa() {
		super.initJpa();

		if (jpaEntityManagerFactory != null)
			sessionFactories.put(EntityManagerSession.class, new TapestryEntityManagerSessionFactory(transactionManager, jpaEntityManagerFactory, jpaHandleTransaction, jpaCloseEntityManager));
	}
}