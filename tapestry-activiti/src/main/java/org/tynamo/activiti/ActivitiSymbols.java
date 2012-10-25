package org.tynamo.activiti;

/**
 * Configuration symbols.
 *
 * <p>The descriptions for these symbols come from the Activiti User's Guide, which can be found at: http://www.activiti.org/userguide/index.html</p>
 *
 */
public class ActivitiSymbols {

	public static final String USE_DEFAULT_SYMBOLS_BASED_CONFIGURER = "activiti.default-configurer";

	/**
	 * History is the component that captures what happened during process execution and stores it permanently.
	 * In contrast to the runtime data, the history data will remain present in the DB also after process instances have completed.
	 * The default is "audit".
	 *
	 * <p>List of options available at: http://www.activiti.org/userguide/index.html#historyConfig</p>
	 */
	public static final String HISTORY = "activiti.history";

	/**
	 * The hostname of your mail server (e.g. mail.mycorp.com). Default is localhost.
	 */
	public static final String MAIL_SERVER_HOST = "activiti.mail-server-host";

	/**
	 * The port for SMTP traffic on the mail server. The default is 25.
	 */
	public static final String MAIL_SERVER_PORT = "activiti.mail-server-port";

	/**
	 * The default e-mail address of the sender of e-mails, when none is provided by the user. By default this is activiti@activiti.org.
	 */
	public static final String MAIL_SERVER_DEFAULT_FROM = "activiti.mail-server-default-from";

	/**
	 * Some mail servers require credentials for sending e-mail. By default not set.
	 */
	public static final String MAIL_SERVER_USERNAME = "activiti.mail-server-username";

	/**
	 * Some mail servers require credentials for sending e-mail. By default not set.
	 */
	public static final String MAIL_SERVER_PASSWORD = "activiti.mail-server-password";

	/**
	 * Indicates the type of database. This property is required when not using the default H2.
	 *
	 * <p>List of supported databases at: http://www.activiti.org/userguide/index.html#supporteddatabases</p>
	 */
	public static final String DATABASE_TYPE = "activiti.database-type";

	/**
	 * Allows to set the strategy to handle the database schema on process engine boot and shutdown.
	 * Default is false.
	 *
	 * <p>List of options available at: http://www.activiti.org/userguide/index.html#databaseConfiguration</p>
	 */
	public static final String DATABASE_SCHEMA_UPDATE = "activiti.database-schema-update";

	/**
	 * Implementation of the driver for the specific database type.
	 */
	public static final String JDBC_DRIVER = "activiti.jdbc-driver";

	/**
	 * JDBC URL of the database.
	 */
	public static final String JDBC_URL = "activiti.jdbc-url";

	/**
	 * Username to connect to the database.
	 */
	public static final String JDBC_USERNAME = "activiti.jdbc-username";

	/**
	 * Password to connect to the database.
	 */
	public static final String JDBC_PASSWORD = "activiti.jdbc-password";

	/**
	 * The number of active connections that the connection pool at maximum at any time can contain. Default is 10.
	 */
	public static final String JDBC_MAX_ACTIVE_CONNECTIONS = "activiti.jdbc-max-active-connections";

	/**
	 * The number of idle connections that the connection pool at maximum at any time can contain.
	 */
	public static final String JDBC_MAX_IDLE_CONNECTIONS = "activiti.jdbc-max-idle-connections";

	/**
	 * The amount of time in milliseconds a connection can be 'checked out' from the connection pool before it is forcefully returned.
	 * Default is 20000 (20 seconds).
	 */
	public static final String JDBC_MAX_CHECKOUT_TIME = "activiti.jdbc-max-checkout-time";

	/**
	 * This is a low level setting that gives the pool a chance to print a log status and re-attempt the acquisition of a connection in
	 * the case that it's taking unusually long (to avoid failing silently forever if the pool is misconfigured)
	 * Default is 20000 (20 seconds).
	 */
	public static final String JDBC_MAX_WAIT_TIME = "activiti.jdbc-max-wait-time";

	/**
	 * The JobExecutor is a component that manages a couple of threads to fire timers (and later also asynchronous messages).
	 * For unit testing scenarios, it is cumbersome to work with multiple threads. Therefor the API allows to query for
	 * (ManagementService.createJobQuery) and execute jobs (ManagementService.executeJob) through the API so that job execution can be
	 * controlled from within a unit test. To avoid that the job executor interferes, it can be turned off.
	 */
	public static final String JOB_EXECUTOR_ACTIVATE = "activiti.job-executor-activate";

	/**
	 * Flag indicating that the engine should begin and commit/rollback the transaction on the used EntityManager instances.
	 * Set to false when Java Transaction API (JTA) is used or when using Tapestry's own start/stop transaction management using @CommitAfter. 
         * 
         * Note that if you want Tapestry to manage your transactions by setting this to false, you will need
         * to properly annotate your methods w/ @CommitAfter so that Tapestry's JPA handling can start/stop the transactions as needed. 
	 */
	public static final String JPA_HANDLE_TRANSACTION = "activiti.jpa-handle-transaction";

	/**
	 * Flag indicating that the engine should close the EntityManager instance that was obtained from the EntityManagerFactory.
	 * Set to false when the EntityManager is container-managed (e.g. when using an Extended Persistence Context which isn't scoped to a single transaction').
	 */
	public static final String JPA_CLOSE_ENTITY_MANAGER = "activiti.jpa-close-entity-manager";

	/**
	 * The name of the persistence-unit to use. (Make sure the persistence-unit is available on the classpath.
	 * According to the spec, the default location is /META-INF/persistence.xml).
	 */
	public static final String JPA_PERSISTENCE_UNIT_NAME = "activiti.jpa-persistence-unit-name";
}