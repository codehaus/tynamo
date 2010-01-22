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

package org.tynamo.jpa.internal.test;

import org.apache.tapestry5.ioc.test.IOCTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.tynamo.jpa.JPAEntityManagerSource;
import org.tynamo.jpa.example.app0.entities.User;
import org.tynamo.jpa.internal.JPAEntityManagerSourceImpl;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;

public class JPAEntityManagerSourceImplTest extends IOCTestCase {
	private final Logger log = LoggerFactory.getLogger("tapestry.jpa.JPAEntityManagerSourceTest");

	@Test
	public void startup_without_packages() {

		replay();
		JPAEntityManagerSource source = new JPAEntityManagerSourceImpl(log, "tapestryjpatest");

		EntityManager entityManager = source.create();
		Assert.assertNotNull(entityManager);

		// make sure it found the entity in the package
		EntityType<User> etype = entityManager.getEntityManagerFactory().getMetamodel().entity(User.class);
		Assert.assertEquals(etype.getName(), "org.tynamo.jpa.example.app0.entities.User");

		verify();
	}

}
