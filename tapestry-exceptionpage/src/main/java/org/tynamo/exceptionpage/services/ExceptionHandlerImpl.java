package org.tynamo.exceptionpage.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tapestry5.services.ExceptionReporter;

public class ExceptionHandlerImpl implements ExceptionHandler {
	private Map<Class<? extends RuntimeException>, Class<? extends ExceptionReporter>> configuration = new HashMap<Class<? extends RuntimeException>, Class<? extends ExceptionReporter>>();

	@SuppressWarnings("unchecked")
	public ExceptionHandlerImpl(Map<Class, Class> configuration) {
		for (Entry<Class, Class> entry : configuration.entrySet())
			this.configuration.put(entry.getKey(), entry.getValue());
	}

	public Map<Class<? extends RuntimeException>, Class<? extends ExceptionReporter>> getConfiguration() {
		return configuration;
	}

}
