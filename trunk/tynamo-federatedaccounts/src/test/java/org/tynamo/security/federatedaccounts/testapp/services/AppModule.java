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
package org.tynamo.security.federatedaccounts.testapp.services;

import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.tynamo.security.FilterChainDefinition;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.federatedaccounts.pages.FacebookOauth;
import org.tynamo.security.federatedaccounts.services.DefaultHibernateFederatedAccountServiceImpl;
import org.tynamo.security.federatedaccounts.services.FederatedAccountService;
import org.tynamo.security.federatedaccounts.services.FederatedAccountsModule;
import org.tynamo.security.federatedaccounts.testapp.entities.User;
import org.tynamo.security.services.SecurityModule;
import org.tynamo.seedentity.hibernate.services.SeedEntity;

/**
 * This module is automatically included as part of the Tapestry IoC Registry, it's a good place to configure and extend
 * Tapestry, or to place your own service definitions.
 */
@SubModule(value = { SecurityModule.class, SeedEntity.class, FederatedAccountsModule.class })
public class AppModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(FederatedAccountService.class, DefaultHibernateFederatedAccountServiceImpl.class);
		binder.bind(AuthorizingRealm.class, UserRealm.class).withId(UserRealm.class.getSimpleName());
	}

	public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {
		// Contributions to ApplicationDefaults will override any contributions to
		// FactoryDefaults (with the same key). Here we're restricting the supported
		// locales to just "en" (English). As you add localised message catalogs and other assets,
		// you can extend this list of locales (it's a comma separated series of locale names;
		// the first locale name is the default when there's no reasonable match).

		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");

		// The factory default is true but during the early stages of an application
		// overriding to false is a good idea. In addition, this is often overridden
		// on the command line as -Dtapestry.production-mode=false
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");

		// The application version number is incorprated into URLs for some
		// assets. Web browsers will cache assets because of the far future expires
		// header. If existing assets are changed, the version number should also
		// change, to force the browser to download new versions.
		configuration.add(SymbolConstants.APPLICATION_VERSION, "0.0.1-SNAPSHOT");

		configuration.add(SecuritySymbols.SHOULD_LOAD_INI_FROM_CONFIG_PATH, "true");

		configuration.add(FacebookOauth.FACEBOOK_CLIENTID, "someclientid");
		configuration.add(FacebookOauth.FACEBOOK_CLIENTSECRET, "someclientsecret");

	}

	public static void contributeWebSecurityManager(Configuration<Realm> configuration, @InjectService("UserRealm") AuthorizingRealm userRealm) {
		// FacebookRealm is automatically contributed as long as federatedsecurity is on the classpath
		configuration.add(userRealm);
	}

	public static void contributeSeedEntity(OrderedConfiguration<Object> configuration) {
		User localUser = new User();
		localUser.setUsername("user");
		localUser.setFirstName("Local");
		localUser.setLastName("User");
		localUser.setPassword("user");
		configuration.add("localuser", localUser);
		User fakeFederatedUser = new User();
		fakeFederatedUser.setUsername("fbuser");
		fakeFederatedUser.setFirstName("Facebook");
		fakeFederatedUser.setLastName("User");
		fakeFederatedUser.setFacebookUserId(0L);
		configuration.add("fakeuser", fakeFederatedUser);
	}

	public static void contributeSecurityRequestFilter(OrderedConfiguration<FilterChainDefinition> configuration) {
		// commented out because they are loaded from shiro.ini
		/*
		 * configuration.add("authc-signup-anon", new FilterChainDefinition("/authc/signup", "anon"));
		 * configuration.add("authc-authc", new FilterChainDefinition("/authc/**", "authc"));
		 * configuration.add("user-signup-anon", new FilterChainDefinition("/user/signup", "anon"));
		 * configuration.add("user-user", new FilterChainDefinition("/user/**", "user"));
		 * configuration.add("roles-user-roles-user", new FilterChainDefinition("/roles/user/**", "roles[user]"));
		 * configuration.add("roles-manager-roles-manager", new FilterChainDefinition("/roles/manager/**",
		 * "roles[manager]")); configuration.add("perms-view-perms-news-view", new FilterChainDefinition("/perms/view/**",
		 * "perms[news:view]")); configuration.add("perms-edit-perms-news-edit", new FilterChainDefinition("/perms/edit/**",
		 * "perms[news:edit]"));
		 */
	}
}
