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
package org.tynamo.jdo;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

/**
 * Responsible for creating an PersistenceManager as needed. Internally, is
 * responsible for PersistenceUnit {@link Configuration}, resulting in a {@link PersistenceManagerFactory}.
 * <p/>
 * The service's configuration is a {@linkplain org.apache.tapestry5.ioc.services.ChainBuilder chain
 * of command} of configurator objects.
 */
public interface JDOPersistenceManagerSource {

    /**
     * Creates a new persistence manager using the {@link #getPersistenceManagerFactory()
     * created at service startup.
     */
    PersistenceManager create();

    /**
     * Returns the PersistenceManagerFactory from which JDO entity managers are
     * created.
     */
    PersistenceManagerFactory getPersistenceManagerFactory();
}
