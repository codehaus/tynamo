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
package org.tynamo.security.jpa.testapp.services;

import org.apache.shiro.realm.Realm;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ObjectProvider;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.MasterObjectProvider;
import org.tynamo.security.jpa.JpaSecurityModule;
import org.tynamo.security.jpa.internal.SecureEntityManagerObjectProvider;
import org.tynamo.shiro.extension.realm.text.ExtendedPropertiesRealm;

/**
 * This module is automatically included as part of the Tapestry IoC Registry, it's a good place to configure and extend Tapestry, or to
 * place your own service definitions.
 */
@SubModule(JpaSecurityModule.class)
public class AppModule {

	public static void bind(ServiceBinder binder) {
	}

	public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.APPLICATION_VERSION, "0.0.1-SNAPSHOT");
	}

	public static void contributeWebSecurityManager(Configuration<Realm> configuration) {
		ExtendedPropertiesRealm realm = new ExtendedPropertiesRealm("classpath:users.properties");
		configuration.add(realm);
	}

	@Contribute(MasterObjectProvider.class)
	public static void provideObjectProviders(final OrderedConfiguration<ObjectProvider> configuration) {
		configuration.overrideInstance("EntityManager", SecureEntityManagerObjectProvider.class,
			"before:AnnotationBasedContributions");
	}
}
