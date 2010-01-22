// Copyright 2008, 2009 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.tynamo.jpa2;

import javax.persistence.EntityManager;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Scope;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.apache.tapestry5.ioc.services.PropertyShadowBuilder;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.slf4j.Logger;

import org.tynamo.jpa2.internal.JPAEntityManagerSourceImpl;
import org.tynamo.jpa2.internal.JPATransactionAdvisorImpl;
import org.tynamo.jpa2.internal.JPATransactionDecoratorImpl;
import org.tynamo.jpa2.internal.JPATransactionManagerImpl;

/**
 * Defines core services that support initialization of Hibernate and access to the Hibernate
 * {@link javax.persistence.EntityManager}.
 */
@SuppressWarnings( { "JavaDoc" })
@Marker(JPACore.class)
public class JPACoreModule
{
	public static void bind(ServiceBinder binder)
	{
		binder.bind(JPATransactionDecorator.class, JPATransactionDecoratorImpl.class);
		binder.bind(JPATransactionAdvisor.class, JPATransactionAdvisorImpl.class);
		// binder.bind(HibernateConfigurer.class,
		// JPAHibernateConfigurer.class).withId("JPAHibernateConfigurer");
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration)
	{
		configuration.add(JPASymbols.DEFAULT_CONFIGURATION, "true");
		configuration.add(JPASymbols.EARLY_START_UP, "false");
	}

	public static void contributeRegistryStartup(OrderedConfiguration<Runnable> configuration,

	@Symbol(JPASymbols.EARLY_START_UP) final boolean earlyStartup,

	final JPAEntityManagerSource entityManagerSource)
	{
		configuration.add("JPAStartup", new Runnable()
		{
			public void run()
			{
				if (earlyStartup)
					entityManagerSource.create();
			}
		});
	}

	/**
	 * The transaction manager manages transaction on a per-thread/per-request basis. Any active
	 * transaction will be rolled back at
	 * {@linkplain org.apache.tapestry5.ioc.Registry#cleanupThread() thread cleanup time}. The
	 * thread is cleaned up automatically in a Tapestry web application.
	 */
	@Scope(ScopeConstants.PERTHREAD)
	public static JPATransactionManager buildJPATransactionManager(JPAEntityManagerSource sessionSource,
	        PerthreadManager perthreadManager)
	{
		JPATransactionManagerImpl service = new JPATransactionManagerImpl(sessionSource);

		perthreadManager.addThreadCleanupListener(service);

		return service;
	}

	public static EntityManager buildEntityManager(JPATransactionManager transactionManager,
	        PropertyShadowBuilder propertyShadowBuilder)
	{
		// Here's the thing: the tapestry.hibernate.Session class doesn't have to be per-thread,
		// since
		// it will invoke getSession() on the JPASessionManager service (which is per-thread).
		// On
		// first invocation per request,
		// this forces the HSM into existence (which creates the session and begins the
		// transaction).
		// Thus we don't actually create
		// a session until we first try to access it, then the session continues to exist for the
		// rest
		// of the request.

		return propertyShadowBuilder.build(transactionManager, "entityManager", EntityManager.class);
	}

	public static JPAEntityManagerSource buildJPAEntityManagerSource(Logger logger,
	        @Inject @Symbol(JPASymbols.PERSISTENCE_UNIT) String persistenceUnit, RegistryShutdownHub hub)
	{
		JPAEntityManagerSourceImpl hss = new JPAEntityManagerSourceImpl(logger, persistenceUnit);

		hub.addRegistryShutdownListener(hss);

		return hss;
	}
}
