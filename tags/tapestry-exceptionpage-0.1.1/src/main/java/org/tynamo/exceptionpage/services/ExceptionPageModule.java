package org.tynamo.exceptionpage.services;

import java.io.IOException;
import java.util.Properties;

import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestExceptionHandler;
import org.apache.tapestry5.services.Response;

public class ExceptionPageModule {
	private static final String version;
	static {
		Properties moduleProperties = new Properties();
		String aVersion = "unversioned";
		try {
			moduleProperties.load(ExceptionPageModule.class.getResourceAsStream("module.properties"));
			aVersion = moduleProperties.getProperty("module.version");
		} catch (IOException e) {
			// ignore
		}
		version = aVersion;
	}

	public static void bind(ServiceBinder binder) {
		binder.bind(ExceptionHandler.class, ExceptionHandlerImpl.class);
	}

	public RequestExceptionHandler decorateRequestExceptionHandler(ServiceResources serviceResources,
			ComponentClassResolver componentClassResolver, LinkSource linkSource, Request request, Response response,
			ExceptionHandler exceptionHandler, Object service) {
		return new ConfigurableRequestExceptionHandler((RequestExceptionHandler) service, serviceResources, componentClassResolver, linkSource,
				request, response, exceptionHandler);
	}
}