package org.tynamo.activiti.services;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.tynamo.activiti.ActivitiSymbols;
import org.tynamo.jpa.JPASymbols;

public class TestModule {

	public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(ActivitiSymbols.JOB_EXECUTOR_ACTIVATE, "false");
		configuration.add(JPASymbols.PERSISTENCE_UNIT, "tapestry-activiti");
	}

	@Startup
	public void autoDeployResources(RepositoryService repositoryService) {

		DeploymentBuilder deploymentBuilder = repositoryService
				.createDeployment()
				.enableDuplicateFiltering()
				.name("TapestryActivitiAutoDeployment");

		deploymentBuilder.addClasspathResource("SimpleTest.bpmn20.xml");

		deploymentBuilder.deploy();
	}

}
