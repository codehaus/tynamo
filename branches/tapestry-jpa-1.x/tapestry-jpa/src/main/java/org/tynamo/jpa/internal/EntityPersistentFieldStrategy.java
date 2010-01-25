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

package org.tynamo.jpa.internal;

import org.apache.tapestry5.internal.services.AbstractSessionPersistentFieldStrategy;
import org.apache.tapestry5.services.Request;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.io.Serializable;

/**
 * Persists Hibernate entities by storing their id in the session.
 *
 * @see PersistedEntity
 */
public class EntityPersistentFieldStrategy extends AbstractSessionPersistentFieldStrategy {
    private final EntityManager session;

    public EntityPersistentFieldStrategy(EntityManager session, Request request) {
        super("entity:", request);

        this.session = session;
    }

    @Override
    protected Object convertApplicationValueToPersisted(Object newValue) {
        try {
            Class entityName = newValue.getClass();
            Serializable id = 1L;//session.get
            // TODO
            return new PersistedEntity(entityName, id);
        }
        catch (PersistenceException ex) {
            throw new IllegalArgumentException(HibernateMessages.entityNotAttached(newValue), ex);
        }
    }

    @Override
    protected Object convertPersistedToApplicationValue(Object persistedValue) {
        PersistedEntity persisted = (PersistedEntity) persistedValue;

        return persisted.restore(session);
    }
}
