package org.tynamo.exceptionpage.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ExceptionHandlerImpl implements ExceptionHandler {
	private Map<Class<? extends Throwable>, Object> configuration = new HashMap<Class<? extends Throwable>, Object>();

	@SuppressWarnings("unchecked")
	public ExceptionHandlerImpl(Map<Class, Object> configuration) {
		for (Entry<Class, Object> entry : configuration.entrySet())
			this.configuration.put(entry.getKey(), entry.getValue());
	}

	public Map<Class<? extends Throwable>, Object> getConfiguration() {
		return configuration;
	}

}
