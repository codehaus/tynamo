package org.tynamo.components.internal;

import org.apache.tapestry5.*;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.TapestryInternalUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.*;
import org.tynamo.builder.BuilderDirector;
import org.tynamo.descriptor.CollectionDescriptor;
import org.tynamo.services.DescriptorService;
import org.tynamo.services.PersistenceService;
import org.tynamo.util.DisplayNameUtils;

import java.util.Collection;
import java.util.List;

/**
 * Copy of org.tynamo.components.Composition designed to be used from Block pages (i.e: PropertyDisplayBlocks)
 * This class is for internal use only.
 *
 */
public class Composition
{

	@Inject
	private DescriptorService descriptorService;

	@Inject
	private PersistenceService persitenceService;

	@Inject
	private PropertyAccess propertyAccess;

	@Inject
	private BeanModelSource beanModelSource;

	@Inject
	private Messages messages;

	@Inject
	private BuilderDirector builderDirector;

	@Inject
	private ComponentResources resources;

	@Inject
	private ValueEncoderSource valueEncoderSource;

	@Inject
	private Environment environment;

	@Inject
	private ContextValueEncoder contextValueEncoder;


	/**
	 * The id used to generate a page-unique client-side identifier for the component. If a component renders multiple
	 * times, a suffix will be appended to the to id to ensure uniqueness. The uniqued value may be accessed via the
	 * clientId property.
	 */
	@Parameter(value = "prop:resources.id", defaultPrefix = BindingConstants.LITERAL)
	@Property(write = false)
	private String clientId;

	/**
	 * A Block to render when the source is empty. The default is simply the text "There is no data to display". This
	 * parameter is used to customize that message.
	 */
	@Parameter(value = "block:empty", defaultPrefix = BindingConstants.LITERAL)
	@Property(write = false)
	private Block empty;

	@Parameter(required = false)
	private List instances;

	@Parameter(required = true)
	@Property(write = false)
	private Collection collection;

	/**
	 * The object which owns the collection being edited
	 */
	@Parameter(name = "owner", required = true)
	private Object ownerParameter;

	private Object ownerProperty;

	/**
	 * Ognl expression to invoke on the model to create a new child instance
	 */
	@Parameter(required = false)
	private String createExpression;

	/**
	 * @return The CollectionDescriptor for the collection being edited
	 */
	@Property(write = false)
	private CollectionDescriptor collectionDescriptor;

	@Property
	private Object collectionIterator;

	@Property
	private Object formBean;

	@Parameter(value = "true")
	private boolean allowCreate;

	@InjectComponent
	private Zone compositionZone;

	@InjectComponent
	@Property(write = false)
	private Form form;

	@Parameter(value = "prop:collectionDescriptor.allowRemove")
	private boolean allowRemove;

	@Property
	private int index;

	@Parameter(value = "asset:../move_up.gif")
	@Property(write = false)
	private Asset upImage;

	@Parameter(value = "asset:../move_down.gif")
	@Property(write = false)
	private Asset downImage;

	@Parameter(required = true)
	private String property;

	/**
	 * The image to use for the delete icon
	 */
	@Parameter(value = "asset:../delete.png")
	@Property(write = false)
	private Asset deleteIcon;

	@Property(write = false)
	private BeanModel beanModel;

	public boolean isAllowCreate()
	{
		return propertyAccess.get(ownerProperty, descriptorService.getClassDescriptor(ownerProperty.getClass()).getIdentifierDescriptor().getName()) != null;
	}

	private void activate(Object owner, String property)
	{
		ownerProperty = owner;
		collectionDescriptor = (CollectionDescriptor) descriptorService.getClassDescriptor(owner.getClass()).getPropertyDescriptor(property);
		beanModel = beanModelSource.createEditModel(collectionDescriptor.getElementType(), messages);
	}

	void onPrepareFromForm()
	{
		resources.triggerEvent(EventConstants.PREPARE, null, null);

		if (beanModel == null)
		{
			beanModel = beanModelSource.createEditModel(collectionDescriptor.getElementType(), messages);
		}
	}

	void setupRender()
	{
		activate(ownerParameter, property);
		formBean = builderDirector.createNewInstance(collectionDescriptor.getElementType());
	}

	public Object[] getShowPageContext()
	{
		return new Object[]{collectionIterator.getClass(), collectionIterator};
	}

	public String getLegendMessage()
	{
		return messages.format("org.tynamo.i18n.add",
				DisplayNameUtils.getDisplayName(collectionDescriptor.getElementType(), messages));
	}

	public Object[] getFormContext()
	{
		return new Object[]{ownerProperty.getClass(), ownerProperty, collectionDescriptor.getName()};
	}


	void onPrepareForSubmitFromForm(final Class clazz, String id, final String property)
	{
		activate(contextValueEncoder.toValue(clazz, id), property);
		pushPropertyOutputContextIntoEnvironment(property);
	}

	private void pushPropertyOutputContextIntoEnvironment(final String property)
	{
		PropertyOutputContext context = new PropertyOutputContext()
		{
			public Messages getMessages()
			{
				return messages;
			}

			public Object getPropertyValue()
			{
				return propertyAccess.get(ownerProperty, property);
			}

			public String getPropertyId()
			{
				return TapestryInternalUtils.extractIdFromPropertyExpression(property);
			}

			public String getPropertyName()
			{
				return property;
			}
		};

		environment.push(PropertyOutputContext.class, context);
	}

	public Object onSuccess()
	{
		persitenceService.addToCollection(collectionDescriptor, formBean, ownerProperty);
		return compositionZone.getBody();
	}

	public Object[] getDeleteContext()
	{
		return new Object[]{ownerProperty.getClass(), ownerProperty, collectionDescriptor.getName(), collectionIterator};
	}

	public Object onActionFromDeleteChild(final Class clazz, String id, final String property, final String elementid)
	{
		activate(contextValueEncoder.toValue(clazz, id), property);

		ValueEncoder valueEncoder = valueEncoderSource.getValueEncoder(collectionDescriptor.getElementType());
		Object element = valueEncoder.toValue(elementid);

		persitenceService.removeFromCollection(collectionDescriptor, element, ownerProperty);

		pushPropertyOutputContextIntoEnvironment(property);

		return compositionZone.getBody();
	}

}