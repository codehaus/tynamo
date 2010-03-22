package org.tynamo.exceptionpage.services;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.runtime.ComponentEventException;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestExceptionHandler;
import org.apache.tapestry5.services.Response;

public class ConfigurableRequestExceptionHandler implements RequestExceptionHandler {
	private ExceptionHandler exceptionHandler;
	private RequestExceptionHandler defaultRequestExceptionHandler;
	private LinkSource linkSource;
	private Response response;
	private ComponentClassResolver componentClassResolver;
	private Request request;

	public ConfigurableRequestExceptionHandler(RequestExceptionHandler requestExceptionHandler,
			ComponentClassResolver componentClassResolver, LinkSource linkSource, Request request, Response response,
			ExceptionHandler exceptionHandler) {
		defaultRequestExceptionHandler = requestExceptionHandler;
		this.componentClassResolver = componentClassResolver;
		this.linkSource = linkSource;
		this.request = request;
		this.response = response;
		this.exceptionHandler = exceptionHandler;
	}

	public void handleRequestException(Throwable exception) throws IOException {
		Throwable cause = exception;
		while (cause instanceof ComponentEventException) {
			if (cause.getCause() == null) break;
			cause = cause.getCause();
		}
		if (!exceptionHandler.getConfiguration().containsKey(cause.getClass())) {
			defaultRequestExceptionHandler.handleRequestException(exception);
			return;
		}

		Link link = linkSource.createPageRenderLink(componentClassResolver.resolvePageClassNameToPageName(exceptionHandler.getConfiguration()
				.get(cause.getClass()).getName()), false, new Object[0]);
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
