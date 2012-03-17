package org.tynamo.security.internal;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.util.WebUtils;
import org.apache.tapestry5.internal.services.PageResponseRenderer;
import org.apache.tapestry5.internal.services.RequestPageCache;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.Response;
import org.tynamo.exceptionpage.ExceptionHandlerAssistant;
import org.tynamo.security.services.PageService;
import org.tynamo.security.services.SecurityService;

public class SecurityExceptionHandlerAssistant implements ExceptionHandlerAssistant {
	private final SecurityService securityService;
	private final PageService pageService;
	private final HttpServletRequest servletRequest;
	private final Response response;
	private final PageResponseRenderer renderer;
	private final RequestPageCache pageCache;
	private final Cookies cookies;
	public SecurityExceptionHandlerAssistant(final SecurityService securityService, final PageService pageService, final RequestPageCache pageCache, final HttpServletRequest servletRequest, final Response response, final PageResponseRenderer renderer,final Cookies cookies) {
		this.securityService =securityService;
		this.pageService = pageService;
		this.pageCache = pageCache;
		this.servletRequest = servletRequest;
		this.response = response;
		this.renderer = renderer;
		this.cookies = cookies;
	}
	@Override
	public Object handleRequestException(Throwable exception, List<Object> exceptionContext) throws IOException {
		if (securityService.isAuthenticated()) {
			String unauthorizedPage = pageService.getUnauthorizedPage();
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			if (!StringUtils.hasText(unauthorizedPage)) return null;

			Page page = pageCache.get(unauthorizedPage);
			renderer.renderPageResponse(page);
			return null;
		}
//		Subject subject = securityService.getSubject();
//		if (subject != null) {
//			Session session = subject.getSession();
//			if (session != null) WebUtils.saveRequest(requestGlobals.getHTTPServletRequest());
//		}
  	String contextPath = servletRequest.getContextPath();
  	if ("".equals(contextPath)) contextPath = "/";
  	cookies.writeCookieValue(WebUtils.SAVED_REQUEST_KEY, WebUtils.getPathWithinApplication(servletRequest), contextPath);
		return pageService.getLoginPage();
	}
}
