package org.tynamo.exceptionpage.services;

import java.util.Map;

public interface ExceptionHandler {
	public Map<Class<? extends Throwable>, Class<?>> getConfiguration();

}
