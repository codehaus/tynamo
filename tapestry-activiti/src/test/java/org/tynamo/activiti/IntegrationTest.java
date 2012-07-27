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
import org.tynamo.activiti.services.ServiceA;
import org.tynamo.activiti.services.TestModule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IntegrationTest {

	private ProcessEngine processEngine;
	private RuntimeService runtimeService;
	private TaskService taskService;

	private Registry registry;

	@BeforeClass
	public void setup() {
		setup_registry( ActivitiModule.class, TestModule.class);
		processEngine = registry.getService(ProcessEngine.class);
		runtimeService = registry.getService(RuntimeService.class);
		taskService = registry.getService(TaskService.class);
	}

	public void setup_registry(Class<?>... moduleClasses) {
		RegistryBuilder builder = new RegistryBuilder();
		builder.add(moduleClasses);
		registry = builder.build();
		registry.performRegistryStartup();
	}

	@AfterClass
	public final void shutdown_registry() {
		registry.cleanupThread();
		registry.shutdown();
		registry = null;
	}

	@AfterClass
	public void close_process_engine() {
		processEngine.close();
	}

	@Test
	public void test_basic_activiti_integration() {
		List<ProcessDefinition> processDefinitions = processEngine.getRepositoryService()
				.createProcessDefinitionQuery()
				.list();

		Set<String> processDefinitionKeys = new HashSet<String>();
		for (ProcessDefinition processDefinition : processDefinitions) {
			processDefinitionKeys.add(processDefinition.getKey());
		}

		Set<String> expectedProcessDefinitionKeys = new HashSet<String>();
		expectedProcessDefinitionKeys.add("SimpleProcess");
		expectedProcessDefinitionKeys.add("TapestryTest");


		Assert.assertEquals(processDefinitionKeys, expectedProcessDefinitionKeys);
	}

	@Test
//	@Deployment(resources = "SimpleTest.bpmn20.xml")
	public void test_simple_process() {
		runtimeService.startProcessInstanceByKey("SimpleProcess");
		Task task = taskService.createTaskQuery().singleResult();
		Assert.assertEquals(task.getName(), "My Task");

		taskService.complete(task.getId());
		Assert.assertEquals(runtimeService.createProcessInstanceQuery().count(), 0);
	}

	@Test
//	@Deployment(resources = "TapestryTest.bpmn20.xml")
	public void test_el_resolver() {

		runtimeService.startProcessInstanceByKey("TapestryTest");
		Task task = taskService.createTaskQuery().singleResult();
		Assert.assertEquals(task.getName(), "Human Task");
		taskService.complete(task.getId());
		Assert.assertEquals(runtimeService.createProcessInstanceQuery().count(), 0);

		ServiceA service = registry.getService(ServiceA.class);
		Assert.assertTrue(service.isDone());

	}

}
