package org.tynamo.exceptionpage.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ExceptionHandlerImpl implements ExceptionHandler {
	private Map<Class<? extends Throwable>, Class<?>> configuration = new HashMap<Class<? extends Throwable>, Class<?>>();

	@SuppressWarnings("unchecked")
	public ExceptionHandlerImpl(Map<Class, Class> configuration) {
		for (Entry<Class, Class> entry : configuration.entrySet())
			this.configuration.put(entry.getKey(), entry.getValue());
	}

	public Map<Class<? extends Throwable>, Class<?>> getConfiguration() {
		return configuration;
	}

}
