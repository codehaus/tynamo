package org.tynamo.jbpm;

import org.tynamo.jbpm.services.JbpmModule;
import org.tynamo.jbpm.services.TestService;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.TapestryIOCModule;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.InputStream;

public class JpbmModuleTests extends Assert {

	@SubModule({JbpmModule.class})
	public static class TestModule {
		public static void bind(ServiceBinder binder) {
			binder.bind(TestService.class);
		}

		public static void contributeProcessEngine(MappedConfiguration<String, InputStream> config) {
			InputStream in = TestModule.class.getResourceAsStream("/test.jpdl.xml");
			assertNotNull(in);
			config.add("test", in);
		}
	}

	private Registry registry;

	@BeforeMethod
	public void setup() {
		RegistryBuilder builder = new RegistryBuilder();
		builder.add(TestModule.class, TapestryIOCModule.class);
		registry = builder.build();
		registry.performRegistryStartup();
	}

	@AfterMethod(alwaysRun = true)
	public void teardown() {
		registry.shutdown();
	}

	@Test
	public void testBasicConfiguration() {
		ProcessEngine engine = registry.getService(ProcessEngine.class);
		ProcessInstance processInstance = engine.getExecutionService().startProcessInstanceByKey("test");
		assertTrue(processInstance.isEnded());
	}
}
