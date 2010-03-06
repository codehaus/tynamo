package org.tynamo.jbpm.services;

import org.tynamo.jbpm.cfg.TapestryConfiguration;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.jbpm.api.NewDeployment;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;

public class JbpmModule {
	
	private static Logger log = LoggerFactory.getLogger(JbpmModule.class);

	public static ProcessEngine buildProcessEngine(ObjectLocator locator, Map<String, InputStream> processes) {
		ProcessEngine engine = new TapestryConfiguration(locator).buildProcessEngine();
		for (String name : processes.keySet()) {
			InputStream in = processes.get(name);
			NewDeployment deployment = engine.getRepositoryService().createDeployment();
			deployment.addResourceFromInputStream("/" + name + ".jpdl.xml", in);
			deployment.setName(name);
			String deploymentId = deployment.deploy();
			log.info("Deployed process with id: " + deploymentId);
		}

		for (ProcessDefinition pd : engine.getRepositoryService().createProcessDefinitionQuery().list()) {
			log.info("Deployed process: " + pd.getName());
		}
		return engine;
	}

}
