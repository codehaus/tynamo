package org.tynamo.descriptor;

import junit.framework.TestCase;
import org.tynamo.test.Embeddee;
import org.tynamo.test.Embeddor;
import org.tynamo.test.Foo;

public class EmbeddedDescriptorTest extends TestCase
{
	EmbeddedDescriptor embeddedDescriptor = new EmbeddedDescriptor(Embeddor.class, "embeddee", Embeddee.class);

	public void testClone() throws Exception
	{
		EmbeddedDescriptor embeddedDescriptor = new EmbeddedDescriptor(Embeddor.class, "embeddee", Embeddee.class);
		embeddedDescriptor.getPropertyDescriptors().add(new TrailsPropertyDescriptor(Embeddee.class, "title", String.class));
		EmbeddedDescriptor cloned = (EmbeddedDescriptor) embeddedDescriptor.clone();
		assertEquals(Embeddor.class, cloned.getBeanType());
		assertEquals(Embeddee.class, cloned.getType());
		assertEquals(1, cloned.getPropertyDescriptors().size());
	}

	public void testCopyFromPropertyDescriptor() throws Exception
	{
		IPropertyDescriptor propertyDescriptor = new TrailsPropertyDescriptor(Foo.class, "blork", String.class);
		propertyDescriptor.setIndex(1);
		embeddedDescriptor.copyFrom(propertyDescriptor);
		assertEquals("blork", embeddedDescriptor.getName());
		assertEquals(1, embeddedDescriptor.getIndex());
	}
}
