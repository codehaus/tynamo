package org.tynamo.exceptionpage.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ExceptionHandlerImpl implements ExceptionHandler {
	private Map<Class<? extends Throwable>, Object> configuration = new HashMap<Class<? extends Throwable>, Object>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ExceptionHandlerImpl(Map<Class, Object> configuration) {
		for (Entry<Class, Object> entry : configuration.entrySet()) {
			if (!Throwable.class.isAssignableFrom(entry.getKey()))
				throw new IllegalArgumentException(Throwable.class.getName() + " is the only allowable key type but " + entry.getKey().getName()
						+ " was contributed");
			// some what pointless to create a new map, but it's not allowed to cast the raw type map
			this.configuration.put(entry.getKey(), entry.getValue());
		}
	}

	public Map<Class<? extends Throwable>, Object> getConfiguration() {
		return configuration;
	}

}
