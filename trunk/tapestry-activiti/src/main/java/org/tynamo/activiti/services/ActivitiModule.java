package org.tynamo.activiti.services;

import org.activiti.engine.*;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.variable.EntityManagerSession;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.apache.tapestry5.ioc.*;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.EagerLoad;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tynamo.activiti.ActivitiSymbols;
import org.tynamo.activiti.TapestryEntityManagerSessionFactory;
import org.tynamo.activiti.TapestryExpressionManager;

import java.io.IOException;
import java.util.Collection;

/**
 * Defines the Activiti services {@link ProcessEngine}, {@link RepositoryService}, {@link RuntimeService}, {@link FormService},
 * {@link TaskService}, {@link HistoryService} and {@link ManagementService}.
 */
public class ActivitiModule {

	private static Logger log = LoggerFactory.getLogger(ActivitiModule.class);

	public static void bind(ServiceBinder binder) {
		binder.bind(SessionFactory.class, TapestryEntityManagerSessionFactory.class);
	}

	/**
	 * Configure the defaults for Activiti, most of these values were obtained
	 * from the User's Guide and are Activit's defaults.
	 */
	@Contribute(SymbolProvider.class)
	@FactoryDefaults
	public static void factoryDefaults(MappedConfiguration<String, Object> configuration) {

		configuration.add(ActivitiSymbols.USE_DEFAULT_SYMBOLS_BASED_CONFIGURER, true);

		configuration.add(ActivitiSymbols.HISTORY, ProcessEngineConfiguration.HISTORY_AUDIT);
		configuration.add(ActivitiSymbols.MAIL_SERVER_HOST, "localhost");
		configuration.add(ActivitiSymbols.MAIL_SERVER_PORT, 25);
		configuration.add(ActivitiSymbols.MAIL_SERVER_DEFAULT_FROM, "activiti@activiti.org");
		configuration.add(ActivitiSymbols.MAIL_SERVER_USERNAME, "");
		configuration.add(ActivitiSymbols.MAIL_SERVER_PASSWORD, "");
		configuration.add(ActivitiSymbols.DATABASE_TYPE, "h2");
		configuration.add(ActivitiSymbols.DATABASE_SCHEMA_UPDATE, ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		configuration.add(ActivitiSymbols.JDBC_DRIVER, "org.h2.Driver");
		configuration.add(ActivitiSymbols.JDBC_URL, "jdbc:h2:mem:activiti-jpa;DB_CLOSE_DELAY=1000");
		configuration.add(ActivitiSymbols.JDBC_USERNAME, "sa");
		configuration.add(ActivitiSymbols.JDBC_PASSWORD, "");
		configuration.add(ActivitiSymbols.JDBC_MAX_ACTIVE_CONNECTIONS, 10);
		configuration.add(ActivitiSymbols.JDBC_MAX_IDLE_CONNECTIONS, 10);
		configuration.add(ActivitiSymbols.JDBC_MAX_CHECKOUT_TIME, 20000);
		configuration.add(ActivitiSymbols.JDBC_MAX_WAIT_TIME, 20000);
		configuration.add(ActivitiSymbols.JPA_HANDLE_TRANSACTION, true);
		configuration.add(ActivitiSymbols.JPA_CLOSE_ENTITY_MANAGER, true);
		configuration.add(ActivitiSymbols.JPA_PERSISTENCE_UNIT_NAME, "");

		configuration.add(ActivitiSymbols.JOB_EXECUTOR_ACTIVATE, true);
	}

	/**
	 * Configure Tapestry service for the Activiti configuration
	 * {@link ProcessEngineConfiguration}.
	 */
	@Contribute(ProcessEngine.class)
	public static void defaultProcessEngineConfiguration(
			final Configuration<ProcessEngineConfigurer> configurers,
			final SymbolSource symbolSource,
			final TypeCoercer typeCoercer,
			@Symbol(ActivitiSymbols.USE_DEFAULT_SYMBOLS_BASED_CONFIGURER) final boolean useDefaultConfigurer) {

		if (useDefaultConfigurer) {
			configurers.add(new ProcessEngineConfigurer() {
				@Override
				public void configure(ProcessEngineConfiguration cfg) {

					// Configures Activiti's {@link ProcessEngine} based on the {@link ActivitiSymbols}.
					cfg.setHistory(symbolSource.valueForSymbol(ActivitiSymbols.HISTORY));
					cfg.setMailServerHost(symbolSource.valueForSymbol(ActivitiSymbols.MAIL_SERVER_HOST));
					cfg.setMailServerPort(typeCoercer.coerce(symbolSource.valueForSymbol(ActivitiSymbols.MAIL_SERVER_PORT), Integer.class));
					cfg.setMailServerDefaultFrom(symbolSource.valueForSymbol(ActivitiSymbols.MAIL_SERVER_DEFAULT_FROM));
					cfg.setMailServerUsername(symbolSource.valueForSymbol(ActivitiSymbols.MAIL_SERVER_USERNAME));
					cfg.setMailServerPassword(symbolSource.valueForSymbol(ActivitiSymbols.MAIL_SERVER_PASSWORD));
					cfg.setDatabaseType(symbolSource.valueForSymbol(ActivitiSymbols.DATABASE_TYPE));
					cfg.setDatabaseSchemaUpdate(symbolSource.valueForSymbol(ActivitiSymbols.DATABASE_SCHEMA_UPDATE));
					cfg.setJdbcDriver(symbolSource.valueForSymbol(ActivitiSymbols.JDBC_DRIVER));
					cfg.setJdbcUrl(symbolSource.valueForSymbol(ActivitiSymbols.JDBC_URL));
					cfg.setJdbcUsername(symbolSource.valueForSymbol(ActivitiSymbols.JDBC_USERNAME));
					cfg.setJdbcPassword(symbolSource.valueForSymbol(ActivitiSymbols.JDBC_PASSWORD));

					cfg.setJdbcMaxActiveConnections(typeCoercer.coerce(symbolSource.valueForSymbol(ActivitiSymbols.JDBC_MAX_ACTIVE_CONNECTIONS), Integer.class));
					cfg.setJdbcMaxIdleConnections(typeCoercer.coerce(symbolSource.valueForSymbol(ActivitiSymbols.JDBC_MAX_IDLE_CONNECTIONS), Integer.class));
					cfg.setJdbcMaxCheckoutTime(typeCoercer.coerce(symbolSource.valueForSymbol(ActivitiSymbols.JDBC_MAX_CHECKOUT_TIME), Integer.class));
					cfg.setJdbcMaxWaitTime(typeCoercer.coerce(symbolSource.valueForSymbol(ActivitiSymbols.JDBC_MAX_WAIT_TIME), Integer.class));
					cfg.setJobExecutorActivate(typeCoercer.coerce(symbolSource.valueForSymbol(ActivitiSymbols.JOB_EXECUTOR_ACTIVATE), Boolean.class));
					cfg.setJpaHandleTransaction(typeCoercer.coerce(symbolSource.valueForSymbol(ActivitiSymbols.JPA_HANDLE_TRANSACTION), Boolean.class));
					cfg.setJpaCloseEntityManager(typeCoercer.coerce(symbolSource.valueForSymbol(ActivitiSymbols.JPA_CLOSE_ENTITY_MANAGER), Boolean.class));

					String unitName = symbolSource.valueForSymbol(ActivitiSymbols.JPA_PERSISTENCE_UNIT_NAME);
					if (!"".equals(unitName)) {
						cfg.setJpaPersistenceUnitName(unitName);
					}

				}
			});
		}
	}

	/**
	 * Configure Tapestry service for {@link ProcessEngine}.
	 * <p/>
	 * <p>Resources may be contributed to this service, which will be deployed
	 * automatically in Activiti. Deploying resources this way will check if the
	 * resource changed and only then deploy to the Activiti database.</p>
	 */
	public static ProcessEngine buildProcessEngine(
			final Collection<ProcessEngineConfigurer> processEngineConfigurers,
			final SessionFactory sessionFactory,
			final ObjectLocator objectLocator) {

		StandaloneProcessEngineConfiguration processEngineConfiguration = new StandaloneProcessEngineConfiguration() {
			@Override
			protected void initJpa() {
				super.initJpa();
				if (jpaEntityManagerFactory != null) {
					sessionFactories.put(EntityManagerSession.class, sessionFactory);
				}
			}
		};

		for (ProcessEngineConfigurer configurer : processEngineConfigurers) {
			configurer.configure(processEngineConfiguration);
		}

		//Allows Tapestry Services to be looked up by service id
		processEngineConfiguration.setExpressionManager(new TapestryExpressionManager(objectLocator));

		return processEngineConfiguration.buildProcessEngine();
	}

	@EagerLoad
	public static Deployment buildDeployment(
			final RepositoryService repositoryService,
			final Collection<Resource> processes) throws IOException {

		DeploymentBuilder builder = repositoryService.createDeployment()
				//This option ensure that processes are deployed only if they are new or do not have any changes in comparison to the latest revision in the database.
				.enableDuplicateFiltering()
				.name("TapestryAutoDeployment");

		for (Resource resource : processes) {
			log.info("Auto deploying process " + resource.getFile());
			builder.addInputStream(resource.getFile(), resource.openStream());
		}

		return builder.deploy();
	}

	/**
	 * Configure Tapestry service for {@link RepositoryService}.
	 */
	public static RepositoryService buildRepositoryService(ProcessEngine processEngine,
	                                                       PropertyShadowBuilder propertyShadowBuilder) {
		return propertyShadowBuilder.build(processEngine, "repositoryService", RepositoryService.class);
	}

	/**
	 * Configure Tapestry service for {@link RuntimeService}.
	 */
	public static RuntimeService buildRuntimeService(ProcessEngine processEngine,
	                                                 PropertyShadowBuilder propertyShadowBuilder) {
		return propertyShadowBuilder.build(processEngine, "runtimeService", RuntimeService.class);
	}

	/**
	 * Configure Tapestry service for {@link FormService}.
	 */
	public static FormService buildFormService(ProcessEngine processEngine,
	                                           PropertyShadowBuilder propertyShadowBuilder) {
		return propertyShadowBuilder.build(processEngine, "formService", FormService.class);
	}

	/**
	 * Configure Tapestry service for {@link TaskService}.
	 */
	public static TaskService buildTaskService(ProcessEngine processEngine,
	                                           PropertyShadowBuilder propertyShadowBuilder) {
		return propertyShadowBuilder.build(processEngine, "taskService", TaskService.class);
	}

	/**
	 * Configure Tapestry service for {@link HistoryService}.
	 */
	public static HistoryService buildHistoryService(ProcessEngine processEngine,
	                                                 PropertyShadowBuilder propertyShadowBuilder) {
		return propertyShadowBuilder.build(processEngine, "historyService", HistoryService.class);
	}

	/**
	 * Configure Tapestry service for {@link ManagementService}.
	 */
	public static ManagementService buildManagementService(ProcessEngine processEngine,
	                                                       PropertyShadowBuilder propertyShadowBuilder) {
		return propertyShadowBuilder.build(processEngine, "managementService", ManagementService.class);
	}
}