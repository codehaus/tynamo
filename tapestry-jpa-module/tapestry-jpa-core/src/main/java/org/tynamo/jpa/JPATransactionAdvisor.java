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

package org.tynamo.jpa;

import org.apache.tapestry5.ioc.MethodAdviceReceiver;

/**
 * A replacement for {@link org.apache.tapestry5.hibernate.JPATransactionDecorator}.
 * 
 * @since 5.1.0.0
 */
public interface JPATransactionAdvisor
{
	/**
	 * Identifies any methods with the {@link org.apache.tapestry5.jpa.annotations.CommitAfter}
	 * annotation and applies the transaction logic to those methods.
	 * 
	 * @param receiver
	 *            advice receiver
	 */
	void addTransactionCommitAdvice(MethodAdviceReceiver receiver);
}
