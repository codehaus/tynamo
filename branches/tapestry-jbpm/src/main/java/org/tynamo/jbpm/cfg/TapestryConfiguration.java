package org.tynamo.jbpm.cfg;

import org.tynamo.jbpm.env.TapestryContext;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.jbpm.api.ProcessEngine;
import org.jbpm.pvm.internal.cfg.JbpmConfiguration;
import org.jbpm.pvm.internal.env.Environment;
import org.jbpm.pvm.internal.env.EnvironmentFactory;
import org.jbpm.pvm.internal.env.PvmEnvironment;
import org.jbpm.pvm.internal.env.WireObject;
import org.jbpm.pvm.internal.wire.WireContext;
import org.jbpm.pvm.internal.wire.WireDefinition;

import java.io.InputStream;
import java.util.List;

/*
 * Adapted from SpringConfiguration
 * author alfie
 */

public class TapestryConfiguration extends JbpmConfiguration implements EnvironmentFactory, ProcessEngine {

	private static final long serialVersionUID = 1L;

	private ObjectLocator locator;

	public TapestryConfiguration(ObjectLocator locator, InputStream jbpmConfiguration) {
		this(locator);
		super.setInputStream(jbpmConfiguration);
	}

	/**
	 * Instantiates a new tapestry configuration.
	 */
	public TapestryConfiguration(ObjectLocator locator) {
		this.locator = locator;
	}

	/**
	 * {@inheritDoc)
	 */
	@Override
	public Environment openEnvironment(List<WireObject> txWireObjects) {

		PvmEnvironment environment = new PvmEnvironment(this);

		// FIXME: All beneath should be a super call

		// set the classloader
		ClassLoader classLoader = processEngineWireContext.getClassLoader();
		if (classLoader != null) {
			environment.setClassLoader(classLoader);
		}

		// add the process-engine context
		environment.setContext(new TapestryContext(locator));
		environment.setContext(processEngineWireContext);

		// add the transaction context
		WireDefinition usedWireDefinition = transactionWireDefinition;
		if (txWireObjects != null) {
			usedWireDefinition = new WireDefinition(transactionWireDefinition, txWireObjects);
		}

		WireContext transactionContext = new WireContext(usedWireDefinition,
				org.jbpm.pvm.internal.env.Context.CONTEXTNAME_TRANSACTION, environment, true);
		// add the environment block context to the environment
		environment.setContext(transactionContext);

		Environment.pushEnvironment(environment);
		try {
			// finish the creation of the environment wire context
			transactionContext.create();

		} catch (RuntimeException e) {
			Environment.popEnvironment();
			throw e;
		}

		// if all went well, return the created environment
		return environment;
	}

	@Override
	public <T> T get(Class<T> type) {
		return locator.getService(type);
	}

	@Override
	public Object get(String key) {
		return locator.getService(key, Object.class);
	}
}
