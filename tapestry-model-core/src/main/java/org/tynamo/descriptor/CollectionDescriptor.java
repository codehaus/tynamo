package org.tynamo.descriptor;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;


public class CollectionDescriptor extends TynamoPropertyDescriptorImpl
{
	private Class elementType;

	private boolean childRelationship = false;

	private String inverseProperty = null;

	private boolean oneToMany = false;

	private String addExpression = null;

	private String removeExpression = null;

	private String swapExpression = null;

	private boolean allowRemove = true;

	public CollectionDescriptor(Class beanType, TynamoPropertyDescriptor descriptor)
	{
		super(beanType, descriptor);
	}

	public CollectionDescriptor(Class beanType, CollectionDescriptor collectionDescriptor)
	{
		super(beanType, collectionDescriptor.getBeanType());
		this.copyFrom(collectionDescriptor);
	}

	public CollectionDescriptor(Class beanType, String name, Class type)
	{
		super(beanType, type);
		this.setName(name);
	}


	/**
	 * (non-Javadoc)
	 *
	 * @see org.tynamo.descriptor.TynamoPropertyDescriptorImpl#isCollection()
	 */
	public boolean isCollection()
	{
		return true;
	}

	/**
	 * @return Returns the elementType.
	 */
	public Class getElementType()
	{
		return elementType;
	}

	/**
	 * @param elementType The elementType to set.
	 */
	public void setElementType(Class elementType)
	{
		this.elementType = elementType;
	}

	public String getInverseProperty()
	{
		return inverseProperty;
	}

	public void setInverseProperty(String inverseProperty)
	{
		this.inverseProperty = inverseProperty;
	}

	/**
	 * Is this a OneToMany collection? or a ManyToMany collection?
	 */
	public boolean isOneToMany()
	{
		return oneToMany;
	}

	public void setOneToMany(boolean oneToMany)
	{
		this.oneToMany = oneToMany;
	}


	/**
	 * @return Returns the childRelationship.
	 */
	public boolean isChildRelationship()
	{
		return childRelationship;
	}

	/**
	 * @param childRelationship The childRelationship to set.
	 */
	public void setChildRelationship(boolean childRelationship)
	{
		this.childRelationship = childRelationship;
		if (this.childRelationship)
		{
			setSearchable(false);
		}
	}

	public String getAddExpression()
	{
		return addExpression;
	}

	public void setAddExpression(String addExpression)
	{
		this.addExpression = addExpression;
	}

	public String getRemoveExpression()
	{
		return removeExpression;
	}

	public void setRemoveExpression(String removeExpression)
	{
		this.removeExpression = removeExpression;
	}

	public String getSwapExpression()
	{
		return swapExpression;
	}

	public void setSwapExpression(String swapExpression)
	{
		this.swapExpression = swapExpression;
	}

	public boolean isAllowRemove()
	{
		return allowRemove;
	}

	public void setAllowRemove(boolean allowRemove)
	{
		this.allowRemove = allowRemove;
	}

	public Object clone()
	{
		return new CollectionDescriptor(getBeanType(), this);
	}

	private void copyFrom(CollectionDescriptor collectionDescriptor)
	{
		LOGGER.debug("Cloning CollectionDescriptor");
		try
		{
			BeanUtils.copyProperties(this, collectionDescriptor);
		} catch (IllegalAccessException e)
		{
			LOGGER.error(e.getMessage());
		} catch (InvocationTargetException e)
		{
			LOGGER.error(e.getMessage());
		}
	}
}
