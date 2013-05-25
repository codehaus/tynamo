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
package org.tynamo.security.testapp.services.impl;

import org.apache.commons.lang.StringUtils;

public class Invoker {
	
	public static final String SUCCESS_SUFFIX = "SUCCESS";

	public static String invoke(Class<?> clazz) {
		return clazz.getSimpleName()+"."+SUCCESS_SUFFIX;
	}
	
	public static String invoke(Class<?> clazz, Object... arguments) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(clazz.getSimpleName()).append("(");
		
		if (arguments != null)
		{
			builder.append(StringUtils.join(arguments, ", "));
		}
		
		builder.append(").").append(SUCCESS_SUFFIX);
		
		return builder.toString();
	}
	
}