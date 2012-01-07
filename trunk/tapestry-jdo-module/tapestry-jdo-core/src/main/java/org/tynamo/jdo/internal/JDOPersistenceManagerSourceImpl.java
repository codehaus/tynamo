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

package org.tynamo.jdo.internal;


import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;
import org.slf4j.Logger;
import org.tynamo.jdo.JDOPersistenceManagerSource;

public class JDOPersistenceManagerSourceImpl implements JDOPersistenceManagerSource, RegistryShutdownListener {
	private final PersistenceManagerFactory persistenceManagerFactory;

	public JDOPersistenceManagerSourceImpl(Logger logger, String pmfName) {
		long startTime = System.currentTimeMillis();

		long configurationComplete = System.currentTimeMillis();

		persistenceManagerFactory = JDOHelper.getPersistenceManagerFactory(pmfName);				

		long factoryCreated = System.currentTimeMillis();

		logger.info(JDOCoreMessages.startupTiming(configurationComplete - startTime, factoryCreated - startTime));
	}

	public PersistenceManager create() {
		return persistenceManagerFactory.getPersistenceManager();
	}

	public PersistenceManagerFactory getPersistenceManagerFactory() {
		return persistenceManagerFactory;
	}

	public void registryDidShutdown() {
		persistenceManagerFactory.close();
	}

}
