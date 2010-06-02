package org.tynamo.security.services;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.apache.tapestry5.ioc.Invocation;
import org.apache.tapestry5.ioc.MethodAdvice;
import org.apache.tapestry5.ioc.services.AspectDecorator;

public class HttpServletRequestDecoratorImpl implements HttpServletRequestDecorator {
	public static enum SecurityOperation {
		getRemoteUser, getUserPrincipal, isUserInRole, OtherOperation;

		public static SecurityOperation toOperation(String methodName) {
			try {
				return valueOf(methodName);
			} catch (Exception e) {
				return OtherOperation;
			}
		}

	}

	private final AspectDecorator aspectDecorator;

	public HttpServletRequestDecoratorImpl(AspectDecorator aspectDecorator) {
		this.aspectDecorator = aspectDecorator;
	}

	public <T> T build(Class<T> serviceInterface, T delegate) {

		MethodAdvice advice = new MethodAdvice() {
			public void advise(Invocation invocation) {
				// It's necessary to check securityManager is bound - bad things will happen if request is not bound yet,
				// which will happen if any of the services use Request shadow when subject is still being built
				// A better way to do this would be to bind the securityManager, requests etc. here
				if (ThreadContext.getSecurityManager() == null) {
					invocation.proceed();
					return;
				}
				Subject subject = SecurityUtils.getSubject();
				Object principal = null;
				// FIXME Should there always be a subject?
				if (subject != null) principal = subject.getPrincipal();

				switch (SecurityOperation.toOperation(invocation.getMethodName())) {
				case getRemoteUser:
					invocation.overrideResult(principal == null ? null : principal.toString());
					break;
				case getUserPrincipal:
					invocation.overrideResult(principal);
					break;
				case isUserInRole:
					invocation.overrideResult(subject == null ? false : subject.hasRole(invocation.getParameter(0).toString()));
					break;
				default:
					invocation.proceed();
				}
			}
		};

		return aspectDecorator.build(serviceInterface, delegate, advice, String.format("<Security request interceptor for %s>",
				serviceInterface.getName()));
	}
}