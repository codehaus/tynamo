package org.tynamo.pages;

import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry5.*;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.internal.BeanValidationContext;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanEditContext;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyEditContext;
import org.apache.tapestry5.services.ValueEncoderSource;
import org.apache.tapestry5.util.EnumSelectModel;
import org.tynamo.components.EditComposition;
import org.tynamo.descriptor.CollectionDescriptor;
import org.tynamo.descriptor.IdentifierDescriptor;
import org.tynamo.descriptor.TynamoPropertyDescriptor;
import org.tynamo.services.DescriptorService;
import org.tynamo.services.PersistenceService;
import org.tynamo.util.GenericSelectionModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class PropertyEditBlocks
{

	@Environmental
	@Property(write = false)
	private BeanEditContext beanEditContext;

	@Environmental
	@Property(write = false)
	private PropertyEditContext propertyEditContext;

	@Environmental
	@Property(write = false)
	private BeanValidationContext tynamoBeanContext;

	@Inject
	private PersistenceService persistenceService;

	@Inject
	private DescriptorService descriptorService;

	@Inject
	private ValueEncoderSource valueEncoderSource;

	@Inject
	private ComponentResources resources;

	@Inject
	private BeanModelSource beanModelSource;

	@Component(parameters = {"value=propertyEditContext.propertyValue", "label=prop:propertyEditContext.label",
			"translate=prop:textFieldTranslator", "validate=prop:textFieldValidator",
			"clientId=prop:propertyEditContext.propertyId", "annotationProvider=propertyEditContext"})
	private TextField textField;

	@Component(parameters = {"collection=propertyEditContext.propertyValue", "label=prop:propertyEditContext.label",
			"clientId=prop:propertyEditContext.propertyId", "collectionDescriptor=propertyDescriptor",
			"owner=tynamoBeanContext.beanInstance"})
	private EditComposition editComposition;

	@Component(parameters = {"model=tynamoBeanContext.beanInstance", "propertyDescriptor=propertyDescriptor"})
	private org.tynamo.components.Blob blob;

	public TynamoPropertyDescriptor getPropertyDescriptor()
	{
		return descriptorService.getClassDescriptor(beanEditContext.getBeanClass()).getPropertyDescriptor(propertyEditContext.getPropertyId());
	}

	@SuppressWarnings({"unchecked"})
	public SelectModel getSelectModel()
	{
		TynamoPropertyDescriptor propertyDescriptor = getPropertyDescriptor();

		Class type = propertyDescriptor.isCollection() ? ((CollectionDescriptor) propertyDescriptor).getElementType() : propertyDescriptor.getPropertyType();
		if (type.isEnum()) return new EnumSelectModel(type, resources.getMessages());

		if (propertyDescriptor.isCollection() && ((CollectionDescriptor) propertyDescriptor).isOneToMany())
		{
			return new GenericSelectionModel(persistenceService.getOrphanInstances((CollectionDescriptor) propertyDescriptor, tynamoBeanContext.getBeanInstance()));
		}

		return new GenericSelectionModel(persistenceService.getInstances(type));
	}

	/**
	 * Provide a value encoder for a type.
	 *
	 * @return
	 */
	public ValueEncoder getValueEncoderForProperty()
	{
		TynamoPropertyDescriptor propertyDescriptor = getPropertyDescriptor();
		if (propertyDescriptor.isCollection())
		{
			CollectionDescriptor collectionDescriptor = (CollectionDescriptor) propertyDescriptor;
			return valueEncoderSource.getValueEncoder(collectionDescriptor.getElementType());
		} else
		{
			return valueEncoderSource.getValueEncoder(propertyEditContext.getPropertyType());
		}
	}

	public FieldTranslator getTextFieldTranslator()
	{
		return propertyEditContext.getTranslator(textField);
	}

	public FieldValidator getTextFieldValidator()
	{
		return propertyEditContext.getValidator(textField);
	}


/*	public List<Boolean> buildSelectedList()
	{
		ArrayList<Boolean> selected = new ArrayList<Boolean>();
		if (collection != null)
		{
			selected = new ArrayList<Boolean>(collection.size());
			for (Object o : getCollection())
			{
				selected.add(false);
			}
		}
		return selected;
	}
*/

	public boolean isPropertyValueInstanceOfList()
	{
		return propertyEditContext.getPropertyValue() instanceof List;
	}

	/**
	 * Palette's parameter "selected" only accepts java.util.List If the collection is a java.util.Set it needs to get
	 * converted
	 */
	public List getSelected()
	{
		if (isPropertyValueInstanceOfList())
		{
			return (List) propertyEditContext.getPropertyValue();
		} else
		{
			ArrayList selectedList = new ArrayList();
			selectedList.addAll((Collection) propertyEditContext.getPropertyValue());
			return selectedList;
		}
	}

	public void setSelected(List selected)
	{
		Collection collection = (Collection) propertyEditContext.getPropertyValue();
		CollectionDescriptor descriptor = (CollectionDescriptor) getPropertyDescriptor();

		for (Object o : CollectionUtils.subtract(selected, collection))
		{
			persistenceService.addToCollection(descriptor, o, tynamoBeanContext.getBeanInstance());
		}

		for (Object o : CollectionUtils.subtract(collection, selected))
		{
			persistenceService.removeFromCollection(descriptor, o, tynamoBeanContext.getBeanInstance());
		}

	}

	public boolean isNotEditable()
	{
		IdentifierDescriptor descriptor = (IdentifierDescriptor) getPropertyDescriptor();
		return descriptor.isGenerated() || propertyEditContext.getPropertyValue() != null;
	}

	public BeanModel getEmbeddedModel()
	{
		return beanModelSource.createEditModel(propertyEditContext.getPropertyType(), getMessages());
	}

	private Messages getMessages()
	{
		return resources.getContainerMessages() != null ? resources.getContainerMessages() : resources.getMessages();
	}

	/**
	 * Looks for a help message within the messages based on the id.
	 */
	public String getHelpMessage()
	{
		Messages messages = getMessages();
		String key = propertyEditContext.getPropertyId() + "-help";
		if (messages.contains(key)) return messages.get(key);
		return null;
	}

}
