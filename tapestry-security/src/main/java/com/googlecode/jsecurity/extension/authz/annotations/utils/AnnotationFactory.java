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
package com.googlecode.jsecurity.extension.authz.annotations.utils;

import java.lang.annotation.Annotation;

import org.jsecurity.authz.annotation.RequiresAuthentication;
import org.jsecurity.authz.annotation.RequiresGuest;
import org.jsecurity.authz.annotation.RequiresPermissions;
import org.jsecurity.authz.annotation.RequiresRoles;
import org.jsecurity.authz.annotation.RequiresUser;

import com.googlecode.jsecurity.extension.authz.annotations.RequiresPermissionsAll;
import com.googlecode.jsecurity.extension.authz.annotations.RequiresRolesAll;
import com.googlecode.jsecurity.extension.authz.annotations.utils.casters.clazz.ClassAnnotationCaster;
import com.googlecode.jsecurity.extension.authz.annotations.utils.casters.clazz.CreateMethodAnnotationFromClassVisitor;

/**
 * Contains methods to create method annotation on the 
 * basis of class annotation.
 * <p>
 * It is necessary for the annotation handlers, because they can only work with 
 * method annotations.  
 * 
 * @author Valentine Yerastov
 */
public class AnnotationFactory {

	private AnnotationFactory() {}
	
	private static final AnnotationFactory instance = new AnnotationFactory(); 
	
	
	/**
	 * Create method annotation from class annotations. Copy all data
	 * from class annotation to method annotation. 
	 */
	public Annotation createAuthzMethodAnnotation(Annotation classAnnotation) {
		CreateMethodAnnotationFromClassVisitor visitor = new CreateMethodAnnotationFromClassVisitor(this);
		ClassAnnotationCaster.getInstance().accept(visitor, classAnnotation);
		return visitor.getResultAnnotation();
	}
	
	public RequiresAuthentication createRequiresAuthentication() {
		return new RequiresAuthentication() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return RequiresAuthentication.class;
			}
		};
	}
	
	public RequiresUser createRequiresUser() {
		return new RequiresUser() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return RequiresUser.class;
			}
		};
	}

	public RequiresGuest createRequiresGuest() {
		return new RequiresGuest() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return RequiresGuest.class;
			}
		};
	}

	public RequiresPermissions createRequiresPermissions(final RequiresPermissionsAll a) {
		return new RequiresPermissions() {
			@Override
			public String value() {
				return a.value();
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return RequiresPermissions.class;
			}
		};
	}

	public RequiresRoles createRequiresRoles(final RequiresRolesAll a) {
		return new RequiresRoles() {

			@Override
			public String value() {
				return a.value();
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return RequiresRoles.class;
			}

		};
	}

	public static AnnotationFactory getInstance() {
		return instance;
	}
	
	
}
