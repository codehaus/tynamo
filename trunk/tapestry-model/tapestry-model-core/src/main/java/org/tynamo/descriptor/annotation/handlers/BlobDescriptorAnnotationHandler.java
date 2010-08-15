package org.tynamo.descriptor.annotation.handlers;

import org.tynamo.descriptor.annotation.BlobDescriptor;
import org.tynamo.descriptor.extension.BlobDescriptorExtension;
import org.tynamo.descriptor.TynamoPropertyDescriptor;

public class BlobDescriptorAnnotationHandler extends AbstractAnnotationHandler implements DescriptorAnnotationHandler<BlobDescriptor, TynamoPropertyDescriptor>
{

	public BlobDescriptorAnnotationHandler()
	{
		super();
	}

	public TynamoPropertyDescriptor decorateFromAnnotation(BlobDescriptor propertyDescriptorAnno, TynamoPropertyDescriptor descriptor)
	{
		BlobDescriptorExtension blobDescriptor = new BlobDescriptorExtension(descriptor.getPropertyType());
		setPropertiesFromAnnotation(propertyDescriptorAnno, blobDescriptor);
		descriptor.addExtension(BlobDescriptorExtension.class.getName(), blobDescriptor);
		descriptor.setSummary(false);
		return descriptor;
	}


}
