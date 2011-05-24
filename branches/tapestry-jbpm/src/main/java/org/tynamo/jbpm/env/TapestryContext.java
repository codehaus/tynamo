package org.tynamo.jbpm.env;

import org.apache.tapestry5.ioc.ObjectLocator;
import org.jbpm.pvm.internal.env.Context;

import java.util.Set;

public class TapestryContext implements Context {

	private ObjectLocator locator;

	public TapestryContext(ObjectLocator locator) {
		this.locator = locator;
	}

	public Object get(String key) {
		try {
			return locator.getService(key, Object.class);
		} catch (RuntimeException e) {
			return null;
		}
	}

	public <T> T get(Class<T> type) {
		try {
			return locator.getService(type);
		} catch (RuntimeException e) {
			return null;
		}
	}

	public String getName() {
		return "tapestry";
	}

	public boolean has(String key) {
		try {
			locator.getService(key, Object.class);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	public Set<String> keys() {
		throw new UnsupportedOperationException("Tapestry does not provide services names from the registry!");
	}

	public Object set(String key, Object value) {
		throw new UnsupportedOperationException("Cannot set a service in the tapestry registry!");
	}

}
