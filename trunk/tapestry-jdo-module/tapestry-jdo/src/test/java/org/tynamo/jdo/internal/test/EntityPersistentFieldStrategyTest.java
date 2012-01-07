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

import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.apache.tapestry5.test.TapestryTestCase;
import org.easymock.EasyMock;
import org.testng.annotations.Test;
import org.tynamo.jdo.example.app0.entities.User;
import org.tynamo.jdo.internal.EntityPersistentFieldStrategy;
import org.tynamo.jdo.internal.PersistedEntity;

@Test
public class EntityPersistentFieldStrategyTest extends TapestryTestCase {

    public void testExceptionThrownOnNonEntity() {
        String nonEntity = "foo";
        PersistenceManager pm = newMock(PersistenceManager.class);

        EntityPersistentFieldStrategy strategy = new EntityPersistentFieldStrategy(pm, null);        

        expect(pm.getObjectId(nonEntity)).andReturn(null);

        replay();

        try {
            strategy.postChange("pageName", "", "fieldName", nonEntity);

            unreachable();
        } catch (Exception ex) {
            assertEquals(
                    ex.getMessage(),
                    "Failed persisting an entity in the PersistenceManager. "
                    + "Only entities attached to a JDO PersistenceManager can be persisted. entity: foo");
        }

        verify();
    }
    
    public void testNoExceptionWithPersistentEntityWithPersistenceStrategy() {
        User userEntity = new User();
        PersistenceManager pm = newMock(PersistenceManager.class);
        
        Request mockRequest = super.mockRequest();
        Session mockSession = mockSession();
        expect(mockRequest.getSession(true)).andReturn(mockSession);
        mockSession.setAttribute(
                EasyMock.<String>anyObject(),
                EasyMock.isA(PersistedEntity.class)
        );
        EasyMock.expectLastCall();
        
                
        EntityPersistentFieldStrategy strategy = new EntityPersistentFieldStrategy(pm, mockRequest);        
        
        expect(pm.getObjectId(userEntity)).andReturn(new org.datanucleus.identity.LongIdentity(User.class,1L));

        replay();
        
        strategy.postChange("pageName", "", "fieldName", userEntity);


        verify();
    }
}
