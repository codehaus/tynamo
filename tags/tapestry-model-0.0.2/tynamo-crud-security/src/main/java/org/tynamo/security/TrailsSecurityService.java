package org.tynamo.security;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ognl.Ognl;
import org.tynamo.descriptor.IClassDescriptor;
import org.tynamo.descriptor.IPropertyDescriptor;
import org.tynamo.security.annotation.SecurityAnnotationHandler;

public class TrailsSecurityService implements SecurityService
{

	public TrailsSecurityService()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public List buildRestrictions(IClassDescriptor classDescriptor)
	{
		ArrayList restrictions = new ArrayList();
		SecurityAnnotationHandler annotationHandler = new SecurityAnnotationHandler();
		restrictions.addAll(annotationHandler.buildClassRestrictions(classDescriptor.getType()));
		for (Iterator iter = classDescriptor.getPropertyDescriptors().iterator(); iter.hasNext();)
		{
			IPropertyDescriptor propertyDescriptor = (IPropertyDescriptor) iter.next();
			try
			{
				Field field = classDescriptor.getType().getDeclaredField(
					propertyDescriptor.getName());
				restrictions.addAll(annotationHandler.buildPropertyRestrictions(
					field, propertyDescriptor.getName()) );
			}
			catch (Exception ex)
			{
				// that's fine
			}
			try
			{
				PropertyDescriptor beanPropDescriptor = (PropertyDescriptor) Ognl.getValue("propertyDescriptors.{? name == '" + propertyDescriptor.getName() + "'}[0]",
				Introspector.getBeanInfo(classDescriptor.getType()));
				Method readMethod = beanPropDescriptor.getReadMethod();
				restrictions.addAll(annotationHandler.buildPropertyRestrictions(
					readMethod, propertyDescriptor.getName()) );
			}
			catch (Exception ex)
			{
				// this is fine too
			}
		}
		return restrictions;
	}

	public List findRestrictions(IClassDescriptor classDescriptor)
	{
		// TODO use cached version.
		return buildRestrictions(classDescriptor);
	}

}
