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
package com.googlecode.jsecurity.extension.authz.annotations.utils.casters.method;

import org.jsecurity.authz.annotation.RequiresAuthentication;
import org.jsecurity.authz.annotation.RequiresGuest;
import org.jsecurity.authz.annotation.RequiresPermissions;
import org.jsecurity.authz.annotation.RequiresRoles;
import org.jsecurity.authz.annotation.RequiresUser;
import org.jsecurity.authz.aop.AuthenticatedAnnotationHandler;
import org.jsecurity.authz.aop.AuthorizingAnnotationHandler;
import org.jsecurity.authz.aop.GuestAnnotationHandler;
import org.jsecurity.authz.aop.PermissionAnnotationHandler;
import org.jsecurity.authz.aop.RoleAnnotationHandler;
import org.jsecurity.authz.aop.UserAnnotationHandler;


/**
 * Creates a handler based on the annotation type.  
 * 
 * @author Valentine Yerastov
 */
public class HandlerCreateVisitor implements MethodAnnotationCasterVisitor {

	private AuthorizingAnnotationHandler handler;
	
	@Override
	public void visitRequiresAuthentication(RequiresAuthentication annotation) {
		handler = new AuthenticatedAnnotationHandler();
	}

	@Override
	public void visitRequiresGuest(RequiresGuest annotation) {
		handler = new GuestAnnotationHandler();
	}

	@Override
	public void visitRequiresPermissions(RequiresPermissions annotation) {
		handler = new PermissionAnnotationHandler();
	}

	@Override
	public void visitRequiresRoles(RequiresRoles annotation) {
		handler = new RoleAnnotationHandler();
	}

	@Override
	public void visitRequiresUser(RequiresUser annotation) {
		handler = new UserAnnotationHandler();
	}

	@Override
	public void visitNotFund() {
		handler = null;
		
	}

	public AuthorizingAnnotationHandler getHandler() {
		return handler;
	}

}
