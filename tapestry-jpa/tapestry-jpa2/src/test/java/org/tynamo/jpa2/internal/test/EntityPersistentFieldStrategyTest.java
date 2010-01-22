// Copyright 2008 The Apache Software Foundation
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

package org.tynamo.jpa2.internal.test;

import org.apache.tapestry5.test.TapestryTestCase;
import org.testng.annotations.Test;
import org.tynamo.jpa2.internal.EntityPersistentFieldStrategy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnitUtil;

@Test
public class EntityPersistentFieldStrategyTest extends TapestryTestCase {
	public void not_an_entity() {
		String nonEntity = "foo";
		EntityManager entityManager = newMock(EntityManager.class);

		EntityPersistentFieldStrategy strategy = new EntityPersistentFieldStrategy(entityManager, null);
		EntityManagerFactory emf = newMock(EntityManagerFactory.class);
		PersistenceUnitUtil puu = newMock(PersistenceUnitUtil.class);

		expect(entityManager.getEntityManagerFactory()).andReturn(emf);
		expect(emf.getPersistenceUnitUtil()).andReturn(puu);
		expect(puu.getIdentifier(nonEntity)).andThrow(new PersistenceException("error"));

		replay();

		try {
			strategy.postChange("pageName", "", "fieldName", nonEntity);

			unreachable();
		}
		catch (Exception ex) {
			assertEquals(
					ex.getMessage(),
					"Failed persisting an entity in the session. Only entities attached to a JPA EntityManager can be persisted. entity: foo");
		}

		verify();
	}
}
