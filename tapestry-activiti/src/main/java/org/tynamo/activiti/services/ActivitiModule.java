package org.tynamo.activiti.services;

import java.io.InputStream;
import java.util.Map;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tynamo.activiti.ActivitiSymbols;
import org.tynamo.activiti.cfg.TapestryConfiguration;
import org.tynamo.jpa.JPAEntityManagerSource;
import org.tynamo.jpa.JPATransactionManager;

/**
 * Defines the Activiti services {@link ProcessEngine}, {@link RepositoryService}, {@link RuntimeService}, {@link FormService},
 * {@link TaskService}, {@link HistoryService} and {@link ManagementService}.
 *
 */
public class ActivitiModule {
	private static Logger log = LoggerFactory.getLogger(ActivitiModule.class);

	/**
	 * Configure the defaults for Activiti, most of these values were obtained from the User's Guide and are Activit's defaults.
	 */
	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(ActivitiSymbols.HISTORY, ProcessEngineConfiguration.HISTORY_AUDIT);
		configuration.add(ActivitiSymbols.MAIL_SERVER_HOST, "localhost");
		configuration.add(ActivitiSymbols.MAIL_SERVER_PORT, "25");
		configuration.add(ActivitiSymbols.MAIL_SERVER_DEFAULT_FROM, "activiti@activiti.org");
		configuration.add(ActivitiSymbols.MAIL_SERVER_USERNAME, "");
		configuration.add(ActivitiSymbols.MAIL_SERVER_PASSWORD, "");
		configuration.add(ActivitiSymbols.DATABASE_TYPE, "h2");
		configuration.add(ActivitiSymbols.DATABASE_SCHEMA_UPDATE, "false");
		configuration.add(ActivitiSymbols.JDBC_DRIVER, "");
		configuration.add(ActivitiSymbols.JDBC_URL, "");
		configuration.add(ActivitiSymbols.JDBC_USERNAME, "");
		configuration.add(ActivitiSymbols.JDBC_PASSWORD, "");
		configuration.add(ActivitiSymbols.JDBC_MAX_ACTIVE_CONNECTIONS, "10");
		configuration.add(ActivitiSymbols.JDBC_MAX_IDLE_CONNECTIONS, "10");
		configuration.add(ActivitiSymbols.JDBC_MAX_CHECKOUT_TIME, "20000");
		configuration.add(ActivitiSymbols.JDBC_MAX_WAIT_TIME, "20000");
		configuration.add(ActivitiSymbols.JPA_HANDLE_TRANSACTION, "true");
		configuration.add(ActivitiSymbols.JPA_CLOSE_ENTITY_MANAGER, "true");
		configuration.add(ActivitiSymbols.JPA_PERSISTENCE_UNIT_NAME, "");
	}

	/**
	 * Configure Tapestry service for the Activiti configuration {@link ProcessEngineConfiguration}.
	 */
	public static ProcessEngineConfiguration buildProcessEngineConfiguration(
		JPATransactionManager transactionManager,
		JPAEntityManagerSource entityManagerSource,
		SymbolSource symbolSource,
		TypeCoercer typeCoercer,
		ObjectLocator objectLocator
	) {
		return new TapestryConfiguration(transactionManager, entityManagerSource, symbolSource, typeCoercer, objectLocator);
	}

	/**
	 * Configure Tapestry service for {@link ProcessEngine}.
	 *
	 * <p>Resources may be contributed to this service, which will be deployed automatically in Activiti.
	 * Deploying resources this way will check if the resource changed and only then deploy to the Activiti database.</p>
	 */
	public static ProcessEngine buildProcessEngine(
		ProcessEngineConfiguration processEngineConfiguration,

		Map<String, InputStream> processes
	) {
		ProcessEngine engine = processEngineConfiguration.buildProcessEngine();

		DeploymentBuilder deployment = engine.getRepositoryService().createDeployment()
		//This option ensure that processes are deployed only if they are new or do not have any changes in comparison to the latest revision in the database.
		.enableDuplicateFiltering()
		.name("TapestryAutoDeployment")
		;
		for (String name : processes.keySet()) {
			log.info("Auto deploying process " + name);
			deployment.addInputStream(name, processes.get(name));
		}
		deployment.deploy();

		return engine;
	}

	/**
	 * Configure Tapestry service for {@link RepositoryService}.
	 */
	public static RepositoryService buildRepositoryService(ProcessEngine processEngine) {
		return processEngine.getRepositoryService();
	}

	/**
	 * Configure Tapestry service for {@link RuntimeService}.
	 */
	public static RuntimeService buildRuntimeService(ProcessEngine processEngine) {
		return processEngine.getRuntimeService();
	}

	/**
	 * Configure Tapestry service for {@link FormService}.
	 */
	public static FormService buildFormService(ProcessEngine processEngine) {
		return processEngine.getFormService();
	}

	/**
	 * Configure Tapestry service for {@link TaskService}.
	 */
	public static TaskService buildTaskService(ProcessEngine processEngine) {
		return processEngine.getTaskService();
	}

	/**
	 * Configure Tapestry service for {@link HistoryService}.
	 */
	public static HistoryService buildHistoryService(ProcessEngine processEngine) {
		return processEngine.getHistoryService();
	}

	/**
	 * Configure Tapestry service for {@link ManagementService}.
	 */
	public static ManagementService buildManagementService(ProcessEngine processEngine) {
		return processEngine.getManagementService();
	}
}