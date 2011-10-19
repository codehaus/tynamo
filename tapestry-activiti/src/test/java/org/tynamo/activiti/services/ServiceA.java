package org.tynamo.activiti.services;

import org.activiti.engine.RuntimeService;

public class ServiceA
{
	private RuntimeService runtimeService;

	public ServiceA(RuntimeService runtimeService)
	{
		this.runtimeService = runtimeService;
	}

	public void doTransactionalStuff(boolean error)
	{
		runtimeService.startProcessInstanceByKey("TapestryTest");
	}
}
