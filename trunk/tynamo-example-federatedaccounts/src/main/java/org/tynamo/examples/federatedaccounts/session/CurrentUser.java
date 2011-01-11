package org.tynamo.examples.federatedaccounts.session;

public interface CurrentUser {
	public abstract String getUsername();

	public abstract String getDisplayableName();

	public abstract void merge(Object remoteAccount);

}