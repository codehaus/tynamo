package org.tynamo.exceptionpage.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.ioc.internal.OperationException;
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object[] formExceptionContext(Throwable exception) {
		if (exception instanceof ContextAwareException) return ((ContextAwareException) exception).getContext();

		Class exceptionClass = exception.getClass();
		// pick the first class in the hierarchy that's not anonymous, probably no reason check for array types
		while ("".equals(exceptionClass.getSimpleName()))
			exceptionClass = exceptionClass.getSuperclass();

		// check if exception type is plain runtimeException - yes, we really want the test to be this way
		if (exceptionClass.isAssignableFrom(RuntimeException.class))
			return exception.getMessage() == null ? new Object[0] : new Object[] { exception.getMessage().toLowerCase() };

		// otherwise, form the context from the exception type name
		String exceptionType = exceptionClass.getSimpleName();
		if (exceptionType.endsWith("Exception")) exceptionType = exceptionType.substring(0, exceptionType.length() - 9);
		return new Object[] { exceptionType.toLowerCase() };
	}

	public void handleRequestException(Throwable exception) throws IOException {
		Throwable cause = exception;

		// Depending on where the error was thrown, there could be several levels of wrappers..
		// For exceptions in component operations, it's OperationException -> ComponentEventException -> <Target>Exception

		// Throw away the wrapped exceptions first
		while (cause instanceof OperationException || cause instanceof ComponentEventException) {
			if (cause.getCause() == null) break;
			cause = cause.getCause();
		}

		Class<?> causeClass = cause.getClass();
		if (!exceptionHandler.getConfiguration().containsKey(causeClass)) {
			// try at most two level of superclasses before delegating back to the default exception handler
			causeClass = causeClass.getSuperclass();
			if (causeClass == null || !exceptionHandler.getConfiguration().containsKey(causeClass)) {
				causeClass = causeClass.getSuperclass();
				if (causeClass == null || !exceptionHandler.getConfiguration().containsKey(causeClass)) {
					defaultRequestExceptionHandler.handleRequestException(exception);
					return;
				}
			}
		}

		Object[] exceptionContext = formExceptionContext(cause);
		Object value = exceptionHandler.getConfiguration().get(causeClass);
		Object page = null;
		ExceptionHandlerAssistant assistant = null;
		if (value instanceof ExceptionHandlerAssistant) assistant = (ExceptionHandlerAssistant) value;
		else if (!(value instanceof Class)) {
			defaultRequestExceptionHandler.handleRequestException(exception);
			return;
		} else if (ExceptionHandlerAssistant.class.isAssignableFrom((Class) value)) {
			@SuppressWarnings("unchecked")
			Class<ExceptionHandlerAssistant> handlerType = (Class<ExceptionHandlerAssistant>) value;
			assistant = handlerAssistants.get(handlerType);
			if (assistant == null) {
				assistant = (ExceptionHandlerAssistant) serviceResources.autobuild(handlerType);
				handlerAssistants.put(handlerType, assistant);
			}
		} else page = value;

		// the assistant may handle the exception directly or return a page
		if (assistant != null) {
			// in case assistant changes the context
			List context = Arrays.asList(exceptionContext);
			page = assistant.handleRequestException(exception, context);
			exceptionContext = context.toArray();
		}
		if (page == null) return;

		exceptionContext = new Object[0];

		try {
			if (page instanceof Class) page = componentClassResolver.resolvePageClassNameToPageName(((Class) page).getName());
			Link link = page instanceof Link ? (Link) page : linkSource.createPageRenderLink(page.toString(), false, exceptionContext);
			if (request.isXHR()) {
				OutputStream os = response.getOutputStream("application/json;charset=UTF-8");
				os.write(("{\"redirectURL\":\"" + link.toAbsoluteURI() + "\"}").getBytes());
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
