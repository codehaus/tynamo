/*
 * Copyright 2004 Chris Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
/*
 * Created on Jun 17, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tynamo.hibernate;

import java.io.Serializable;

import org.hibernate.type.Type;


public interface Interceptable
{
	public boolean onLoad(Serializable id, Object[] state, String[] propertyNames, Type[] types);

	public boolean onInsert(Serializable id, Object[] state, String[] propertyNames, Type[] types);

	public boolean onUpdate(Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types);
}
