package org.tynamo.jbpm.services;

import java.util.Map;

import org.jbpm.api.activity.ActivityExecution;
import org.jbpm.api.activity.ExternalActivityBehaviour;

public class TestService implements ExternalActivityBehaviour {

	public void signal(ActivityExecution execution, String signal, Map<String, ?> config) throws Exception {
		execution.take(signal);
	}

	public void execute(ActivityExecution execution) throws Exception {
		execution.takeDefaultTransition();
	}

}
