// Copyright 2007, 2008 The Apache Software Foundation
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

package org.tynamo.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Responsible for creating an EntityManager as needed. Internally, is responsible for
 * PersistenceUnit {@link Configuration}, resulting in a {@link EntityManagerFactory}.
 * <p/>
 * The service's configuration is a {@linkplain org.apache.tapestry5.ioc.services.ChainBuilder chain
 * of command} of configurator objects.
 */
// @UsesOrderedConfiguration(org.apache.tapestry5.hibernate.HibernateConfigurer.class)
public interface JPAEntityManagerSource
{
	/**
	 * Creates a new session using the {@link #getEntityManagerFactory() EntityManagerFactory}
	 * created at service startup.
	 */
	EntityManager create();

	/**
	 * Returns the EntityManagerFactory from which JPA entity managers are created.
	 */
	EntityManagerFactory getEntityManagerFactory();
}
