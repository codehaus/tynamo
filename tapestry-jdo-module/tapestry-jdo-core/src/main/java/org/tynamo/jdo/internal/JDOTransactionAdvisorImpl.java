// Copyright 2009 The Apache Software Foundation
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

import java.lang.reflect.Method;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.plastic.MethodAdvice;
import org.apache.tapestry5.plastic.MethodInvocation;
import org.tynamo.jdo.JDOTransactionAdvisor;
import org.tynamo.jdo.JDOTransactionManager;
import org.tynamo.jdo.annotations.CommitAfter;

public class JDOTransactionAdvisorImpl implements JDOTransactionAdvisor {
    private final JDOTransactionManager manager;

    /**
     * The rules for advice are the same for any method: commit on success or checked exception, abort on thrown
     * exception ... so we can use a single shared advice object.
     */
    private final MethodAdvice advice = new MethodAdvice() {
        public void advise(MethodInvocation invocation) {
            try {
                invocation.proceed();
            }
            catch (RuntimeException ex) {
                manager.abort();

                throw ex;
            }

            // For success or checked exception, commit the transaction.
            manager.commit();
        }
    };

    public JDOTransactionAdvisorImpl(JDOTransactionManager manager) {
        this.manager = manager;
    }

    public void addTransactionCommitAdvice(MethodAdviceReceiver receiver) {
        for (Method m : receiver.getInterface().getMethods()) {
            if (m.getAnnotation(CommitAfter.class) != null) {
                receiver.adviseMethod(m, advice);
            }
        }
    }
}
