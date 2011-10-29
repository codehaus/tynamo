package org.tynamo.activiti.services;


public class ServiceA {

	private boolean done = false;

	public void doWork() {
		done = true;
	}

	public boolean isDone() {
		return done;
	}
}
