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

package org.tynamo.jdo.example.app0.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.tynamo.jdo.JDOModule;
import org.tynamo.jdo.JDOSymbols;
import org.tynamo.jdo.JDOTransactionAdvisor;

@SubModule(JDOModule.class)
public class AppModule
{
	public static void bind(ServiceBinder binder)
	{
		binder.bind(UserDAO.class, UserDAOImpl.class);
	}
	
	public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration)
	{
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(JDOSymbols.PMF_NAME, "tapestryjdotest");
	}

	@Match("*DAO")
	public static void adviseTransactions(JDOTransactionAdvisor advisor, MethodAdviceReceiver receiver)
	{
		advisor.addTransactionCommitAdvice(receiver);
	}
}
