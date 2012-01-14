//  Copyright 2008 The Apache Software Foundation
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
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import org.apache.tapestry5.annotations.ImmutableSessionPersistedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates a JDO entity class with an entity id.
 *
 */
@ImmutableSessionPersistedObject
public class PersistedEntity implements Serializable {

    private final static Logger logger = LoggerFactory.getLogger(PersistedEntity.class);
    /**
     * The JDO class that this stands for
     */
    private final Class persistentClass;
    /**
     * The JDO object ID
     */
    private final Serializable id;

    /**
     * Creates a new persisted entity
     *
     * @param pcClass the JDO persistent type
     * @param id the JDO Object ID of the persistent class. Note that this value
     * is not just the value of the "primary key" property, but the actual
     * Object ID retrieved by {@link JDOHelper#getObjectId(java.lang.Object) }
     * or {@link PersistenceManager#getObjectId}
     */
    public PersistedEntity(Class pcClass, Serializable id) {
        this.persistentClass = pcClass;
        this.id = id;
    }

    /**
     * Recreates the persistent JDO object from the given persistence manager
     *
     * @param pm
     * @return
     */
    public Object restore(PersistenceManager pm) {
        Object result = null;
        try {
            result = pm.getObjectById(id);
        } catch (Exception ex) {
            logger.info(JDOMessages.sessionPersistedEntityLoadFailure(persistentClass, id, ex));
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("<PersistenceCapable: %s(%s)>", persistentClass, id);
    }
}
