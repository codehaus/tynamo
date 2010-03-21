package org.tynamo.pages;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.Defense;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.ContextValueEncoder;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.tynamo.builder.BuilderDirector;
import org.tynamo.descriptor.CollectionDescriptor;
import org.tynamo.descriptor.TynamoClassDescriptor;
import org.tynamo.descriptor.TynamoPropertyDescriptor;
import org.tynamo.services.DescriptorService;
import org.tynamo.services.PersistenceService;
import org.tynamo.util.DisplayNameUtils;
import org.tynamo.util.Utils;

/**
 * Add Composition Page
 */

public class AddC {

	@Inject
	private BuilderDirector builderDirector;

	@Inject
	private ContextValueEncoder contextValueEncoder;

	@Inject
	private BeanModelSource beanModelSource;

	@Inject
	private Messages messages;

	@Inject
	private PersistenceService persitenceService;

	@Inject
	private DescriptorService descriptorService;

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;


	@Property(write = false)
	private CollectionDescriptor collectionDescriptor;

	@Property
	private Object bean;

	@Property(write = false)
	private Object parentBean;

	@Property(write = false)
	private TynamoClassDescriptor classDescriptor;

	@Property
	private BeanModel beanModel;

	final void onActivate(Class clazz, String parentId, String property) throws Exception {

		Defense.notNull(clazz, "class"); //@todo throw a proper exception

		TynamoPropertyDescriptor propertyDescriptor = descriptorService.getClassDescriptor(clazz).getPropertyDescriptor(property);

		Defense.notNull(propertyDescriptor, "propertyDescriptor"); //@todo throw a proper exception

		this.collectionDescriptor = ((CollectionDescriptor) propertyDescriptor);

		this.classDescriptor = descriptorService.getClassDescriptor(collectionDescriptor.getElementType());
		this.bean = builderDirector.createNewInstance(classDescriptor.getType());
		this.beanModel = beanModelSource.createEditModel(classDescriptor.getType(), messages);

		this.parentBean = contextValueEncoder.toValue(clazz, parentId);
		Defense.notNull(parentBean, "parentBean"); //@todo throw a proper exception

	}

	@Log
	protected Object onSuccess() {
		persitenceService.addToCollection(collectionDescriptor, bean, parentBean);
		persitenceService.save(parentBean);
		return back();
	}


	/**
	 * This tells Tapestry to put type & id into the URL, making it bookmarkable.
	 *
	 * @return
	 */
	protected Object[] onPassivate() {
		return new Object[]{collectionDescriptor.getBeanType(), parentBean, collectionDescriptor.getName()};
	}

	public Link onActionFromCancel() {
		return back();
	}

	protected void cleanupRender() {
		bean = null;
		classDescriptor = null;
		beanModel = null;
		parentBean = null;
		collectionDescriptor = null;
	}

	public String getTitle() {
		return messages.format(Utils.EDIT_MESSAGE, DisplayNameUtils.getDisplayName(collectionDescriptor.getElementType(), messages));
	}


	public Link back() {
		return pageRenderLinkSource.createPageRenderLinkWithContext("Edit", collectionDescriptor.getBeanType(), parentBean);
	}

	public String getListAllLinkMessage() {
		return messages.format(Utils.LISTALL_LINK_MESSAGE, DisplayNameUtils.getPluralDisplayName(classDescriptor, messages));
	}

}