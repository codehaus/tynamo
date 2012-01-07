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

package org.tynamo.jdo.internal.test;

import org.apache.tapestry5.ioc.test.IOCTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.tynamo.jdo.JDOPersistenceManagerSource;
import org.tynamo.jdo.example.app0.entities.User;
import org.tynamo.jdo.internal.JDOPersistenceManagerSourceImpl;

import javax.jdo.PersistenceManager;
import javax.jdo.metadata.TypeMetadata;

public class JDOPersistenceManagerSourceImplTest extends IOCTestCase {
	private final Logger log = LoggerFactory.getLogger("tapestry.jdo.JDOPersistenceManagerSourceTest");

	@Test
	public void startup_without_packages() {

		replay();
		JDOPersistenceManagerSource source = new JDOPersistenceManagerSourceImpl(log, "tapestryjdotest");

		PersistenceManager persistenceManager = source.create();
		Assert.assertNotNull(persistenceManager);

		// make sure it found the entity in the package
		TypeMetadata typeMeta = persistenceManager.getPersistenceManagerFactory().getMetadata(User.class.getName()); 
		
		assertNotNull(typeMeta);
		Assert.assertEquals(typeMeta.getName(), "User");		
		verify();
	}

}
