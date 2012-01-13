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
package org.tynamo.security.shiro.authc;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.internal.services.LinkSource;
import org.tynamo.security.services.PageService;

/**
 * An <code>AuthenticationFilter</code> that is capable of automatically performing an authentication attempt
 * based on the incoming request.
 *
 * @since 0.4.0
 */
public abstract class AuthenticatingFilter extends AuthenticationFilter {
	public AuthenticatingFilter(LinkSource linkSource, PageService pageService) {
		super(linkSource, pageService);
	}

    //TODO - complete JavaDoc

    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        AuthenticationToken token = createToken(request, response);
        if (token == null) {
            String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken " +
                    "must be created in order to execute a login attempt.";
            throw new IllegalStateException(msg);
        }
        try {
            Subject subject = getSubject(request, response);
            subject.login(token);
            return onLoginSuccess(token, subject, request, response);
        } catch (AuthenticationException e) {
            return onLoginFailure(token, e, request, response);
        }
    }

    protected abstract AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception;

    protected AuthenticationToken createToken(String username, String password,
                                              ServletRequest request, ServletResponse response) {
        boolean rememberMe = isRememberMe(request);
        String host = getHost(request);
        return createToken(username, password, rememberMe, host);
    }

    protected AuthenticationToken createToken(String username, String password,
                                              boolean rememberMe, String host) {
        return new UsernamePasswordToken(username, password, rememberMe, host);
    }

    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
                                     ServletRequest request, ServletResponse response) throws Exception {
        return true;
    }

    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e,
                                     ServletRequest request, ServletResponse response) {
        return false;
    }

    /**
     * Returns the host name or IP associated with the current subject.  This method is primarily provided for use
     * during construction of an <code>AuthenticationToken</code>.
     * <p/>
     * The default implementation merely returns {@link ServletRequest#getRemoteHost()}.
     *
     * @param request the incoming ServletRequest
     * @return the <code>InetAddress</code> to associate with the login attempt.
     */
    protected String getHost(ServletRequest request) {
        return request.getRemoteHost();
    }

    /**
     * Returns <code>true</code> if &quot;rememberMe&quot; should be enabled for the login attempt associated with the
     * current <code>request</code>, <code>false</code> otherwise.
     * <p/>
     * This implementation always returns <code>false</code> and is provided as a template hook to subclasses that
     * support <code>rememberMe</code> logins and wish to determine <code>rememberMe</code> in a custom mannner
     * based on the current <code>request</code>.
     *
     * @param request the incoming ServletRequest
     * @return <code>true</code> if &quot;rememberMe&quot; should be enabled for the login attempt associated with the
     *         current <code>request</code>, <code>false</code> otherwise.
     */
    protected boolean isRememberMe(ServletRequest request) {
        return false;
    }
}
