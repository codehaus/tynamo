package org.tynamo.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tynamo.activiti.services.ActivitiModule;
import org.tynamo.activiti.services.TestModule;
import org.tynamo.jpa.JPACoreModule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleTest
{

	private ProcessEngine processEngine;
	private RuntimeService runtimeService;
	private TaskService taskService;

	private Registry registry;

	@BeforeClass
	public void setup()
	{
		setup_registry(JPACoreModule.class, ActivitiModule.class, TestModule.class);
		processEngine = registry.getService(ProcessEngine.class);
		runtimeService = registry.getService(RuntimeService.class);
		taskService = registry.getService(TaskService.class);
	}

	public void setup_registry(Class<?>... moduleClasses)
	{
		RegistryBuilder builder = new RegistryBuilder();
		builder.add(moduleClasses);
		registry = builder.build();
		registry.performRegistryStartup();
	}

	@AfterClass
	public final void shutdown_registry()
	{
		registry.cleanupThread();
		registry.shutdown();
		registry = null;
	}

	@AfterClass
	public void close_process_engine()
	{
		processEngine.close();
	}

	@Test
	public void testBasicActivitiIntegration()
	{
		List<ProcessDefinition> processDefinitions = processEngine.getRepositoryService()
				.createProcessDefinitionQuery()
				.list();

		Set<String> processDefinitionKeys = new HashSet<String>();
		for (ProcessDefinition processDefinition : processDefinitions)
		{
			processDefinitionKeys.add(processDefinition.getKey());
		}

		Set<String> expectedProcessDefinitionKeys = new HashSet<String>();
		expectedProcessDefinitionKeys.add("simpleProcess");

		Assert.assertEquals(processDefinitionKeys, expectedProcessDefinitionKeys);
	}


	@Test
//	@Deployment(resources = "SimpleTest.bpmn20.xml")
	public void simpleProcessTest()
	{
		runtimeService.startProcessInstanceByKey("simpleProcess");
		Task task = taskService.createTaskQuery().singleResult();
		Assert.assertEquals(task.getName(), "My Task");

		taskService.complete(task.getId());
		Assert.assertEquals(runtimeService.createProcessInstanceQuery().count(), 0);
	}
}
