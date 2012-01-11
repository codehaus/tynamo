package org.tynamo.exceptionpage.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.runtime.ComponentEventException;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestExceptionHandler;
import org.apache.tapestry5.services.Response;
import org.tynamo.exceptionpage.ContextAwareException;
import org.tynamo.exceptionpage.ExceptionHandlerAssistant;

public class ConfigurableRequestExceptionHandler implements RequestExceptionHandler {
	private final ExceptionHandler exceptionHandler;
	private final RequestExceptionHandler defaultRequestExceptionHandler;
	private final LinkSource linkSource;
	private final Response response;
	private final ComponentClassResolver componentClassResolver;
	private final Request request;
	private final Map<Class<ExceptionHandlerAssistant>, ExceptionHandlerAssistant> handlerAssistants = Collections
			.synchronizedMap(new HashMap<Class<ExceptionHandlerAssistant>, ExceptionHandlerAssistant>());
	private final ServiceResources serviceResources;

	public ConfigurableRequestExceptionHandler(RequestExceptionHandler requestExceptionHandler, ServiceResources serviceResources,
			ComponentClassResolver componentClassResolver, LinkSource linkSource, Request request, Response response,
			ExceptionHandler exceptionHandler) {
		defaultRequestExceptionHandler = requestExceptionHandler;
		this.serviceResources = serviceResources;
		this.componentClassResolver = componentClassResolver;
		this.linkSource = linkSource;
		this.request = request;
		this.response = response;
		this.exceptionHandler = exceptionHandler;
	}

	protected Object[] formExceptionContext(Throwable exception) {
		if (exception instanceof ContextAwareException) return ((ContextAwareException) exception).getContext();
		if (exception.getMessage() == null) return new Object[0];
		return new Object[] { exception.getMessage() };
	}

	public void handleRequestException(Throwable exception) throws IOException {
		Throwable cause = exception;
		// Throw away the wrapped exceptions first
		while (cause instanceof ComponentEventException) {
			if (cause.getCause() == null) break;
			cause = cause.getCause();
		}

		Class<?> causeClass = cause.getClass();
		if (!exceptionHandler.getConfiguration().containsKey(causeClass)) {
			// try a superclass before delegating back to the default exception handler
			causeClass = causeClass.getSuperclass();
			if (causeClass == null || !exceptionHandler.getConfiguration().containsKey(causeClass)) {
				defaultRequestExceptionHandler.handleRequestException(exception);
				return;
			}
		}

		Class<?> pageClass = exceptionHandler.getConfiguration().get(cause.getClass());

		Object[] exceptionContext = formExceptionContext(cause);

		if (ExceptionHandlerAssistant.class.isAssignableFrom(pageClass)) {
			@SuppressWarnings("unchecked")
			Class<ExceptionHandlerAssistant> handlerType = (Class<ExceptionHandlerAssistant>) pageClass;
			ExceptionHandlerAssistant assistant = handlerAssistants.get(handlerType);
			if (assistant == null) {
				assistant = (ExceptionHandlerAssistant) serviceResources.autobuild(handlerType);
				handlerAssistants.put(handlerType, assistant);
			}
			// the assistant may handle the exception directly or return a page class
			pageClass = assistant.handleRequestException(exception, Arrays.asList(exceptionContext));
			if (pageClass == null) return;
		}

		// TODO properly handle page class name not resolved
		Link link = linkSource.createPageRenderLink(componentClassResolver.resolvePageClassNameToPageName(pageClass.getName()), false,
				exceptionContext);
		try {
			if (request.isXHR()) {
				OutputStream os = response.getOutputStream("application/json;charset=UTF-8");
				os.write(("{\"script\":\"window.location.replace('" + link.toAbsoluteURI() + "');\"}").getBytes());
				os.close();
			} else response.sendRedirect(link);
		}
		// This could throw exceptions if this is already a render request, but it's
		// user's responsibility not to abuse the mechanism
		catch (Exception e) {
			// Nothing to do but delegate
			defaultRequestExceptionHandler.handleRequestException(exception);
		}
	}
}
