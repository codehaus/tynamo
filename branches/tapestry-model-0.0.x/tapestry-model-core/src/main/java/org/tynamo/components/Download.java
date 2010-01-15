package org.tynamo.components;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.tynamo.blob.BlobManager;
import org.tynamo.descriptor.TynamoPropertyDescriptor;
import org.tynamo.descriptor.extension.BlobDescriptorExtension;
import org.tynamo.services.DescriptorService;
import org.tynamo.services.PersistenceService;

public class Download
{

	@Parameter(value = "asset:cross.png")
	@Property(write = false)
	private Asset noIcon;

	@Parameter(value = "asset:noimage.jpg")
	@Property(write = false)
	private Asset noImage;

	@Inject
	private BlobManager filePersister;

	@Inject
	private DescriptorService descriptorService;

	@Inject
	private PropertyAccess propertyAccess;

/*
	@Inject
	private IconResolver iconResolver;

	@Inject
	private AssetFactory contextAssetFactory;
*/

	@Inject
	private ComponentResources resources;

	@Parameter(required = true)
	private Object model;

	@Parameter(required = true)
	private TynamoPropertyDescriptor propertyDescriptor;

	public BlobDescriptorExtension.RenderType getRenderType()
	{
		return propertyDescriptor.getExtension(BlobDescriptorExtension.class).getRenderType();
	}

	public Link getBlobLink()
	{
		return filePersister.createBlobLink(propertyDescriptor, model);
	}

/*
	public Asset getIcon()
	{
		return getContentType() != null ? iconResolver.getAsset(getContentType()) : getNoIcon();
	}
*/

	private String getContentType()
	{
		return filePersister.getContentType(propertyDescriptor, model);
	}

	public String getFileName()
	{
		return filePersister.getFileName(propertyDescriptor, model);
	}

	public boolean isNotNull()
	{
		return isModelNew() && filePersister.isNotNull(propertyDescriptor, model);
	}

	public boolean isModelNew()
	{
		return propertyAccess.get(model, descriptorService.getClassDescriptor(propertyDescriptor.getBeanType())
				.getIdentifierDescriptor().getName()) != null;
	}

	public boolean isNotImageNotIconNotNull()
	{
		return isNotNull() && !getRenderType().isIcon() && !getRenderType().isImage();
	}
}
