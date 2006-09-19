package org.trails.descriptor;

import junit.framework.TestCase;

import org.trails.hibernate.EmbeddedDescriptor;
import org.trails.test.Embeddee;
import org.trails.test.Embeddor;
import org.trails.test.Foo;

public class EmbeddedDescriptorTest extends TestCase
{
	EmbeddedDescriptor embeddedDescriptor = new EmbeddedDescriptor(Embeddor.class, "embeddee", Embeddee.class);
	
	public void testCopyFromPropertyDescriptor() throws Exception
	{
		IPropertyDescriptor propertyDescriptor = new TrailsPropertyDescriptor(Foo.class, "blork", String.class);
		propertyDescriptor.setIndex(1);
		embeddedDescriptor.copyFrom(propertyDescriptor);
		assertEquals("blork", embeddedDescriptor.getName());
		assertEquals(1, embeddedDescriptor.getIndex());
	}
}
