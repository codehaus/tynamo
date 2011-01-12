/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.tynamo.examples.federatedaccounts.pages;

import org.apache.shiro.SecurityUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.ExceptionReporter;
import org.apache.tapestry5.services.Request;
import org.tynamo.examples.federatedaccounts.session.CurrentUser;
import org.tynamo.security.services.SecurityService;

public class Index implements ExceptionReporter {

	@SuppressWarnings("unused")
	@SessionState(create = false)
	@Property
	private CurrentUser currentUser;

	private Throwable exception;

	@Override
	public void reportException(Throwable exception) {
		this.exception = exception;
	}

	public Throwable getException() {
		return exception;
	}

	public String getMessage() {
		if (exception != null) {
			return exception.getMessage() + " Try login.";
		} else {
			return "";
		}
	}

	@Inject
	private ApplicationStateManager applicationStateManager;

	@Inject
	private SecurityService securityService;

	void onActivate() {
		if (securityService.getSubject().isAuthenticated() && !applicationStateManager.exists(CurrentUser.class)) {
			CurrentUser currentUser = applicationStateManager.get(CurrentUser.class);
			currentUser.merge(securityService.getSubject().getPrincipal());
		}

	}

	@Inject
	private Request request;

	Object onActionFromLogout() {
		// Need to call this explicitly to invoke onlogout handlers (for remember me etc.)
		SecurityUtils.getSubject().logout();
		try {
			// the session is already invalidated, but need to cause an exception since tapestry doesn't know about it
			// and you'll get a container exception message instead without this. Unfortunately, there's no way of
			// configuring Shiro to not invalidate sessions right now. See DefaultSecurityManager.logout()
			// There's a similar issues in Tapestry - Howard has fixed, but no in T5.2.x releases yet
			request.getSession(false).invalidate();
		} catch (Exception e) {
		}

		return this;
	}
}
