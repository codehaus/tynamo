package org.tynamo.activiti.services;

import org.activiti.engine.ProcessEngine;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.internal.util.ClasspathResource;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.tynamo.activiti.ActivitiSymbols;
import org.tynamo.jpa.JPASymbols;

public class TestModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(ServiceA.class).withId("customServiceName");
	}

	@Contribute(SymbolProvider.class)
	@ApplicationDefaults
	public static void applicationSymbols(MappedConfiguration<String, String> symbols) {
		symbols.add(ActivitiSymbols.JOB_EXECUTOR_ACTIVATE, "false");
		symbols.add(ActivitiSymbols.DATABASE_SCHEMA_UPDATE, "create-drop"); // use create-drop for testing only!
		symbols.add(JPASymbols.PERSISTENCE_UNIT, "tapestry-activiti");
	}

	@Contribute(ProcessEngine.class)
	public void deployResources(Configuration<Resource> deploymentResources) {
		/**
		 * Remember: the name of the resource must end with "bpmn20.xml".
		 * @see: BpmnDeployer.BPMN_RESOURCE_SUFFIX
		 */
		deploymentResources.add(new ClasspathResource("SimpleTest.bpmn20.xml"));
		deploymentResources.add(new ClasspathResource("TapestryTest.bpmn20.xml"));
	}

}
