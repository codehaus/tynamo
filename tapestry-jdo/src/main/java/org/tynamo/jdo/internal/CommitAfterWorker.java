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

import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.MethodAdvice;
import org.apache.tapestry5.plastic.MethodInvocation;
import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticMethod;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.services.transform.TransformationSupport;
import org.tynamo.jdo.JDOTransactionManager;
import org.tynamo.jdo.annotations.CommitAfter;

/**
 * Searches for methods that have the {@link CommitAfter} annotation and adds
 * logic around the method to commit or abort the transaction.
 */
public class CommitAfterWorker implements ComponentClassTransformWorker2 {

    private final JDOTransactionManager manager;
    
    private final MethodAdvice advice = new MethodAdvice() {

        public void advise(MethodInvocation invocation) {
            try {
                invocation.proceed();
                
                manager.commit();
            } catch (RuntimeException ex) {
                manager.abort();

                throw ex;
            }
        }
    };

    public CommitAfterWorker(JDOTransactionManager manager) {
        this.manager = manager;
    }

    public void transform(PlasticClass pc, TransformationSupport ts, MutableComponentModel mcm) {
        for (PlasticMethod pm : pc.getMethodsWithAnnotation(CommitAfter.class)) {
            pm.addAdvice(advice);
        }
    }
}
