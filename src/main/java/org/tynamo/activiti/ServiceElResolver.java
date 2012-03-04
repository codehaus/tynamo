package org.tynamo.activiti;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.javax.el.ELContext;
import org.activiti.engine.impl.javax.el.ELResolver;
import org.apache.tapestry5.ioc.ObjectLocator;

/**
 * Determines if key specified is a Tapestry Service, and if it is, returns the service instance.
 *
 */
public class ServiceElResolver extends ELResolver {
	private final ObjectLocator objectLocator;

	public ServiceElResolver(final ObjectLocator objectLocator) {
		this.objectLocator = objectLocator;
	}

	public Object getValue(ELContext context, Object base, Object property) {
		if (base == null) {
			//According to javadoc, can only be a String
			String key = (String) property;

			Object service = objectLocator.getService(key, Object.class);

			if (service != null) {
				context.setPropertyResolved(true);

				return service;
			}
		}

		return null;
	}

	public void setValue(ELContext context, Object base, Object property, Object value) {
		if (base == null) {
			String key = (String) property;
			Object service = objectLocator.getService(key, Object.class);

			if (service != null)
				throw new ActivitiException("Cannot set value of '" + property + "', it resolves to a service defined in the Tapestry.");
		}
	}

	public boolean isReadOnly(ELContext context, Object base, Object property) {
		return true;
	}

	public Class<?> getCommonPropertyType(ELContext context, Object arg) {
		return Object.class;
	}

	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object arg) {
		return null;
	}

	public Class<?> getType(ELContext context, Object arg1, Object arg2) {
		return Object.class;
	}
}