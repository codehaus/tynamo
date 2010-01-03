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

import java.lang.annotation.Annotation;

import org.jsecurity.authz.annotation.RequiresAuthentication;
import org.jsecurity.authz.annotation.RequiresGuest;
import org.jsecurity.authz.annotation.RequiresPermissions;
import org.jsecurity.authz.annotation.RequiresRoles;
import org.jsecurity.authz.annotation.RequiresUser;


/**
 * Class for accepting
 * {@link com.google.code.jsecurity.extension.authz.annotations.utils.casters.method.MethodAnnotationCasterVisitor} 
 * visitors. 
 * <p>
 * Provides call the desired method of the visitor, depending on the annotation type.
 * 
 * @see com.google.code.jsecurity.extension.authz.annotations.utils.casters.method.MethodAnnotationCasterVisitor
 * @author Valentine Yerastov
 */
public class MethodAnnotationCaster {

	private static final MethodAnnotationCaster instance = new MethodAnnotationCaster(); 
	
	private MethodAnnotationCaster() {}
	
	public void accept(MethodAnnotationCasterVisitor visitor, Annotation annotation) {
		if (annotation instanceof RequiresPermissions) {

			visitor.visitRequiresPermissions((RequiresPermissions) annotation);

		} else if (annotation instanceof RequiresRoles) {

			visitor.visitRequiresRoles((RequiresRoles) annotation);

		} else if (annotation instanceof RequiresUser) {

			visitor.visitRequiresUser((RequiresUser) annotation);

		} else if (annotation instanceof RequiresGuest) {

			visitor.visitRequiresGuest((RequiresGuest) annotation);

		} else if (annotation instanceof RequiresAuthentication) {

			visitor.visitRequiresAuthentication((RequiresAuthentication) annotation);

		} else {

			visitor.visitNotFund();

		}
	}

	public static MethodAnnotationCaster getInstance() {
		return instance;
	}
}
