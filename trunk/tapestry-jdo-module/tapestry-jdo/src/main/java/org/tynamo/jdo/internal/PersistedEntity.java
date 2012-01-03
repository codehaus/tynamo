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
import javax.jdo.PersistenceManager;


import org.apache.tapestry5.annotations.ImmutableSessionPersistedObject;

/**
 * Encapsulates a JDO entity class with an entity id.
 */
@ImmutableSessionPersistedObject
public class PersistedEntity implements Serializable
{
	private final Class persistentClass;

	private final Serializable id;

	public PersistedEntity(Class pcClass, Serializable id)
	{
		this.persistentClass = pcClass;
		this.id = id;
	}

	Object restore(PersistenceManager pm)
	{
		try
		{            
			return pm.getObjectById(this.persistentClass, id);
		}
		catch (Exception ex)
		{
			throw new RuntimeException(JDOMessages.sessionPersistedEntityLoadFailure(persistentClass, id, ex));
		}
	}

	@Override
	public String toString()
	{
		return String.format("<PersistenceCapable: %s(%s)>", persistentClass, id);
	}
}
