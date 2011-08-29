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

package org.tynamo.jdo.internal.test;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.ioc.test.IOCTestCase;
import org.slf4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tynamo.jdo.internal.JDOEntityValueEncoder;


/**
 * Testing for the JDO entity value encoder. Based on tests from tapestry-hibernate
 *
 * @author Piero Sartini, Pierce T. Wetter, but mostly cribbed from tapestry-hibernate
 */
public class JDOEntityValueEncoderTest extends IOCTestCase
{

	//~ Instance fields ----------------------------------------------------------------------------

	/**
	 * Registry
	 */
	private Registry registry;

	/**
	 * PropertyAccess from tapestry
	 */
	private PropertyAccess access;

	/**
	 * TypeCoercer from tapestry
	 */
	private TypeCoercer typeCoercer;

	//~ Methods ------------------------------------------------------------------------------------

	/**
	 * Run before any test in the class
	 */
	@BeforeClass
	public void setup()
	{
		registry = buildRegistry();

		access = registry.getService(PropertyAccess.class);
		typeCoercer = registry.getService(TypeCoercer.class);
	}

	/**
	 * Run after all tests in the class
	 */
	@AfterClass
	public void cleanup()
	{
		registry.shutdown();

		registry = null;
		access = null;
		typeCoercer = null;
	}

	/**
	 * Check for null primary key
	 *
	 * @Test
	 */
	public void to_client_id_null()
	{
		PersistenceManager entityManager = mockPersistenceManager();
		Logger logger = mockLogger();

		replay();

		SampleEntity entity = new SampleEntity();

		JDOEntityValueEncoder<SampleEntity> encoder = new JDOEntityValueEncoder<SampleEntity>(
				SampleEntity.class,
				entityManager, access, typeCoercer, logger
		);

		try
		{
			encoder.toClient(entity);
			unreachable();
		}
		catch (IllegalStateException ex)
		{
			assertMessageContains(
					ex, "Entity org.apache.tapestry5.internal.hibernate.SampleEntity",
					"has an id property of null"
			);
		}

		verify();
	}

	/**
	 * Check for not-found value
	 */
//	@Test
	public void to_value_not_found()
	{
		PersistenceManager entityManager = mockPersistenceManager();
		Logger logger = mockLogger();

//		Metamodel metamodel = newMock(Metamodel.class);
//
//		EntityType<SampleEntity> type = newMock(EntityType.class);
//
		PersistenceManagerFactory emf = newMock(PersistenceManagerFactory.class);
//
//		IdentifiableType idType = newMock(IdentifiableType.class);
//
//		SingularAttribute idAttribute = newMock(SingularAttribute.class);
//		//Class idClass = newMock(Class.class);
//
//		// expect(entityManager.getEntityManagerFactory()).andReturn(emf);
//
//		expect(entityManager.getMetamodel()).andReturn(metamodel);
//
//		expect(metamodel.entity(SampleEntity.class)).andReturn(type);
//
//		expect(type.getId(Long.class)).andReturn(idAttribute);
//
//		expect(idAttribute.getName()).andReturn("id");
//
//		expect(type.getIdType()).andReturn(idType);
//		expect(idType.getJavaType()).andReturn(Long.class);
//
//		expect(type.getJavaType()).andReturn(SampleEntity.class);
//
		expect(entityManager.getObjectById(SampleEntity.class, new Long(12345))).andReturn(null);

		logger.error("Unable to convert client value '12345' into an entity instance.");

		replay();

		SampleEntity entity = new SampleEntity();

		JDOEntityValueEncoder<SampleEntity> encoder = new JDOEntityValueEncoder<SampleEntity>(
				SampleEntity.class,
				entityManager, access, typeCoercer, logger
		);

		assertNull(encoder.toValue("12345"));

		verify();

	}

	/**
	 * Return a mock entity manager
	 *
	 * @return mock entity manager
	 */
	protected final PersistenceManager mockPersistenceManager()
	{
		return newMock(PersistenceManager.class);
	}
} // end class JDOEntityValueEncoderTest
