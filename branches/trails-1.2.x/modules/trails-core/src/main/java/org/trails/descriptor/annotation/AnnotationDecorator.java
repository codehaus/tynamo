package org.trails.descriptor.annotation;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import ognl.Ognl;
import org.trails.descriptor.*;

/**
 * This class uses the annotations on a given class or property to modify its
 * descriptor
 *
 * @author Chris Nelson
 */
public class AnnotationDecorator implements DescriptorDecorator
{

	public IClassDescriptor decorate(IClassDescriptor descriptor)
	{

		Annotation[] classAnnotations = descriptor.getType().getAnnotations();
		IClassDescriptor decoratedDescriptor = (IClassDescriptor) decorateFromAnnotations(descriptor, classAnnotations);

		decoratedDescriptor.setPropertyDescriptors(decoratePropertyDescriptors(descriptor));
		sortDescriptors(decoratedDescriptor.getPropertyDescriptors());

		decoratedDescriptor.setMethodDescriptors(decorateMethodDescriptors(descriptor));

		return decoratedDescriptor;
	}

	private List<IPropertyDescriptor> decoratePropertyDescriptors(IClassDescriptor descriptor)
	{
		List<IPropertyDescriptor> decoratedPropertyDescriptors = new ArrayList<IPropertyDescriptor>();
		for (IPropertyDescriptor propertyDescriptor : descriptor.getPropertyDescriptors())
		{
			IPropertyDescriptor clonedDescriptor = decoratePropertyDescriptor(propertyDescriptor);
			// recursively decorate components
			if (clonedDescriptor.isEmbedded())
			{
				clonedDescriptor = (EmbeddedDescriptor) decorate((EmbeddedDescriptor) clonedDescriptor);
			}
			decoratedPropertyDescriptors.add(clonedDescriptor);
		}
		return decoratedPropertyDescriptors;
	}

	private List<IMethodDescriptor> decorateMethodDescriptors(IClassDescriptor descriptor)
	{
		List<IMethodDescriptor> decoratedMethodDescriptors = new ArrayList<IMethodDescriptor>();
		for (IMethodDescriptor methodDescriptor : descriptor.getMethodDescriptors())
		{
			IMethodDescriptor clonedDescriptor = decorateMethodDescriptor(methodDescriptor);
			decoratedMethodDescriptors.add(clonedDescriptor);
		}
		return decoratedMethodDescriptors;
	}

	protected IPropertyDescriptor decoratePropertyDescriptor(IPropertyDescriptor propertyDescriptor)
	{
		IPropertyDescriptor clonedDescriptor = (IPropertyDescriptor) propertyDescriptor.clone();
		try
		{
			Field propertyField = clonedDescriptor.getBeanType().getDeclaredField(propertyDescriptor.getName());
			clonedDescriptor = (IPropertyDescriptor) decorateFromAnnotations(clonedDescriptor, propertyField.getAnnotations());

		} catch (Exception ex)
		{
			// don't care
		}
		try
		{
			PropertyDescriptor beanPropDescriptor = (PropertyDescriptor) Ognl.getValue("propertyDescriptors.{? name == '" + propertyDescriptor.getName() + "'}[0]",
				Introspector.getBeanInfo(clonedDescriptor.getBeanType()));

			Method readMethod = beanPropDescriptor.getReadMethod();
			clonedDescriptor = (IPropertyDescriptor) decorateFromAnnotations(clonedDescriptor, readMethod.getAnnotations());
		}
		catch (Exception ex)
		{
			//System.out.println(propertyDescriptor.getName());
			//ex.printStackTrace();
			// don't care
		}
		return clonedDescriptor;
	}

	protected IMethodDescriptor decorateMethodDescriptor(IMethodDescriptor methodDescriptor) {
		try
		{

			return (IMethodDescriptor) decorateFromAnnotations(methodDescriptor, methodDescriptor.getMethod().getAnnotations());

		} catch (NoSuchMethodException e)
		{

		}
		return methodDescriptor;
	}

	/**
	 * Rearrange the property descriptors by their index
	 *
	 * @param propertyDescriptors
	 */
	private void sortDescriptors(List<IPropertyDescriptor> propertyDescriptors)
	{
		Collections.sort(propertyDescriptors, new PropertyDescriptorIndexComparator());
	}

	private IDescriptor decorateFromAnnotations(IDescriptor descriptor, Annotation[] annotations)
	{
		IDescriptor clonedDescriptor = (IDescriptor) descriptor.clone();
		for (Annotation annotation : annotations)
		{
			// If the annotation type itself has a DescriptorAnnotation, it's one of ours
			DescriptorAnnotation handlerAnnotation =
					annotation.annotationType().getAnnotation(DescriptorAnnotation.class);
			if (handlerAnnotation != null)
			{
				try
				{
					DescriptorAnnotationHandler handler = handlerAnnotation.value().newInstance();
					clonedDescriptor = handler.decorateFromAnnotation(annotation, clonedDescriptor);
				}
				catch (Exception ex)
				{
					//ex.printStackTrace();
				}
			}
		}
		return clonedDescriptor;
	}

}

class PropertyDescriptorIndexComparator implements Comparator<IPropertyDescriptor>
{
	public int compare(IPropertyDescriptor pd1, IPropertyDescriptor pd2)
	{
		int inverter = pd1.getIndex() == IPropertyDescriptor.UNDEFINED_INDEX || pd2.getIndex() == IPropertyDescriptor.UNDEFINED_INDEX ? -1 : 1;
		return inverter * (pd1.getIndex() < pd2.getIndex() ? -1 : (pd1.getIndex() == pd2.getIndex() ? 0 : 1));
	}
}
