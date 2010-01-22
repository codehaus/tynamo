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

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.ioc.test.IOCTestCase;
import org.slf4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tynamo.jpa2.internal.JPAEntityValueEncoder;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.Metamodel;

public class JPAEntityValueEncoderTest extends IOCTestCase {
	private Registry registry;

	private PropertyAccess access;

	private TypeCoercer typeCoercer;

	@BeforeClass
	public void setup() {
		registry = buildRegistry();

		access = registry.getService(PropertyAccess.class);
		typeCoercer = registry.getService(TypeCoercer.class);
	}

	@AfterClass
	public void cleanup() {
		registry.shutdown();

		registry = null;
		access = null;
		typeCoercer = null;
	}

	//@Test
	public void to_client_id_null() {
		EntityManager entityManager = mockEntityManager();
		Logger logger = mockLogger();

		replay();

		SampleEntity entity = new SampleEntity();

		JPAEntityValueEncoder<SampleEntity> encoder = new JPAEntityValueEncoder<SampleEntity>(SampleEntity.class,
																							  entityManager, access, typeCoercer, logger);

		try {
			encoder.toClient(entity);
			unreachable();
		}
		catch (IllegalStateException ex) {
			assertMessageContains(ex, "Entity org.apache.tapestry5.internal.hibernate.SampleEntity",
								  "has an id property of null");
		}

		verify();
	}

	@Test
	public void to_value_not_found() {
		EntityManager entityManager = mockEntityManager();
		Logger logger = mockLogger();

		Metamodel metamodel = newMock(Metamodel.class);

		EntityType<SampleEntity> type = newMock(EntityType.class);

		EntityManagerFactory emf = newMock(EntityManagerFactory.class);
		PersistenceUnitUtil puu = newMock(PersistenceUnitUtil.class);

		IdentifiableType idType = newMock(IdentifiableType.class);
		//Class idClass = newMock(Class.class);

		expect(entityManager.getEntityManagerFactory()).andReturn(emf);
		expect(emf.getPersistenceUnitUtil()).andReturn(puu);

		expect(entityManager.getMetamodel()).andReturn(metamodel);

		expect(metamodel.entity(SampleEntity.class)).andReturn(type);

		expect(type.getIdType()).andReturn(idType);
		expect(idType.getJavaType()).andReturn(Long.class);

		expect(type.getJavaType()).andReturn(SampleEntity.class);

		expect(entityManager.find(SampleEntity.class, new Long(12345))).andReturn(null);

		logger.error("Unable to convert client value '12345' into an entity instance.");

		replay();

		SampleEntity entity = new SampleEntity();

		JPAEntityValueEncoder<SampleEntity> encoder = new JPAEntityValueEncoder<SampleEntity>(SampleEntity.class,
																							  entityManager, access, typeCoercer, logger);

		assertNull(encoder.toValue("12345"));

		verify();

	}

	protected final EntityManager mockEntityManager() {
		return newMock(EntityManager.class);
	}
}
