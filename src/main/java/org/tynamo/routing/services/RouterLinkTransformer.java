package org.tynamo.routing.services;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.TapestryConstants;
import org.apache.tapestry5.internal.services.LinkImpl;
import org.apache.tapestry5.internal.services.RequestSecurityManager;
import org.apache.tapestry5.services.*;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;
import org.tynamo.routing.Route;

import java.text.MessageFormat;

public class RouterLinkTransformer implements PageRenderLinkTransformer {

	private RouterDispatcher routerDispatcher;
	private final Request request;
	private final RequestSecurityManager requestSecurityManager;
	private final Response response;
	private final ContextPathEncoder contextPathEncoder;
	private final BaseURLSource baseURLSource;

	private static final int BUFFER_SIZE = 100;

	public RouterLinkTransformer(RouterDispatcher routerDispatcher, Request request,
	                             RequestSecurityManager requestSecurityManager, Response response,
	                             ContextPathEncoder contextPathEncoder, BaseURLSource baseURLSource) {
		this.routerDispatcher = routerDispatcher;
		this.request = request;
		this.requestSecurityManager = requestSecurityManager;
		this.response = response;
		this.contextPathEncoder = contextPathEncoder;
		this.baseURLSource = baseURLSource;
	}

	public PageRenderRequestParameters decodePageRenderRequest(Request request) {
		return null;
	}

	public Link transformPageRenderLink(Link defaultLink, PageRenderRequestParameters parameters) {

		String activePageName = parameters.getLogicalPageName();

		Route route = routerDispatcher.getRouteMap().get(activePageName);

		if (route != null) {
			StringBuilder builder = new StringBuilder(BUFFER_SIZE);

			if (!"".equals(request.getContextPath())) {
				// Build up the absolute URI.
				builder.append(request.getContextPath());
			}

			builder.append(MessageFormat.format(route.getPathExpression(), encode(parameters.getActivationContext())));

			Link link = new LinkImpl(builder.toString(), false, requestSecurityManager.checkPageSecurity(activePageName), response, contextPathEncoder, baseURLSource);

			if (parameters.isLoopback())
				link.addParameter(TapestryConstants.PAGE_LOOPBACK_PARAMETER_NAME, "t");

			return link;
		}

		return null;
	}

	private String[] encode(EventContext context) {

		assert context != null;
		int count = context.getCount();

		String output[] = new String[count];

		for (int i = 0; i < count; i++) {
			Object raw = context.get(Object.class, i);
			output[i] = contextPathEncoder.encodeValue(raw);
		}

		return output;
	}
}
