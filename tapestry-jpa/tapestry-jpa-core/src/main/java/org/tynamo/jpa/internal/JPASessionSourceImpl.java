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

package org.tynamo.jpa.internal;

import org.tynamo.jpa.JPASessionSource;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;
import org.slf4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPASessionSourceImpl implements JPASessionSource, RegistryShutdownListener {
    private final EntityManagerFactory sessionFactory;

    public JPASessionSourceImpl(Logger logger, String persistenceUnit) {
        long startTime = System.currentTimeMillis();

        long configurationComplete = System.currentTimeMillis();

        sessionFactory = Persistence.createEntityManagerFactory(persistenceUnit);

        long factoryCreated = System.currentTimeMillis();

        logger.info(JPACoreMessages.startupTiming(
                configurationComplete - startTime,
                factoryCreated - startTime));

        //logger.info(JPACoreMessages.entityCatalog(sessionFactory.getAllClassMetadata().keySet()));
    }

    public EntityManager create() {
        return sessionFactory.createEntityManager();
    }

    public EntityManagerFactory getSessionFactory() {
        return sessionFactory;
    }

    public void registryDidShutdown() {
        sessionFactory.close();
    }
}
