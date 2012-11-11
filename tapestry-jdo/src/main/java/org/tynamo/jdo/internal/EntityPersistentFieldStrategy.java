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
package org.tynamo.jdo.internal;

import java.io.Serializable;
import javax.jdo.PersistenceManager;
import org.apache.tapestry5.internal.services.AbstractSessionPersistentFieldStrategy;
import org.apache.tapestry5.services.Request;

/**
 * Persists JDO entities by storing their object ID in the session.
 *
 * <p> By definition, the object ID of a JDO object is serializable; thus, there
 * is no problem with using the actual object for persisting it in the session
 * (that is roughly the reason why JDO requires the object ID to be serializable
 * in the first place). </p>
 *
 * @see PersistedEntity
 */
public class EntityPersistentFieldStrategy extends AbstractSessionPersistentFieldStrategy {

    private final PersistenceManager persistenceManager;

    public EntityPersistentFieldStrategy(PersistenceManager pm, Request request) {
        super("entity:", request);

        this.persistenceManager = pm;
    }

    /**
     * Converts the persistent entity into a serializable object that can be
     * safely stored into the session.
     *
     * <p> Technically, the PersistedEntity is unnecessary as the JDO object ID
     * already represents the class and the id value (thus, it's redundant to
     * create the PersistedEntity </p>
     *
     * @param persistentObject the JDO object to be persisted in the session
     * @return a representation of the persisted entity
     */
    @Override
    protected Object convertApplicationValueToPersisted(Object persistentObject) {
        Class entityName = persistentObject.getClass();
        Serializable id = (Serializable) persistenceManager.getObjectId(persistentObject);
        if (id == null) {
            throw new IllegalArgumentException(JDOMessages.entityNotAttached(persistentObject));
        }
        return new PersistedEntity(entityName, id);
    }

    /**
     * Converts the persisted value from the session into a full blown
     * persistent JDO object.
     *
     * @param persistedValue the value representing the persistent object.
     *
     * @return a persistent entity retrieved from JDO
     */
    @Override
    protected Object convertPersistedToApplicationValue(Object persistedValue) {
        PersistedEntity persisted = (PersistedEntity) persistedValue;

        return persisted.restore(persistenceManager);
    }
}
