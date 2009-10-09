package org.tynamo.descriptor;

import java.util.Set;

import junit.framework.TestCase;
import org.tynamo.test.Baz;
import org.tynamo.test.Bing;
import org.tynamo.test.Foo;

public class CollectionDescriptorTest extends TestCase
{
	public void testFindExpression()
	{
		CollectionDescriptor bingsDescriptor = new CollectionDescriptor(Foo.class, "bings", Set.class);
		bingsDescriptor.setElementType(Bing.class);
		assertEquals("bings.add", bingsDescriptor.getAddExpression());
		CollectionDescriptor bazzesDescriptor = new CollectionDescriptor(Foo.class, "bazzes", Set.class);
		bazzesDescriptor.setElementType(Baz.class);
		assertEquals("addBaz", bazzesDescriptor.getAddExpression());
		assertEquals("removeBaz", bazzesDescriptor.getRemoveExpression());

	}

	public void testClone()
	{
		CollectionDescriptor bazzesDescriptor = new CollectionDescriptor(Foo.class, "bazzes", Set.class);
		bazzesDescriptor.setElementType(Baz.class);
		bazzesDescriptor.setOneToMany(true);
		bazzesDescriptor.setChildRelationship(true);
		bazzesDescriptor.setInverseProperty("inverse");
		bazzesDescriptor.setDisplayName(null);

		CollectionDescriptor clonedDescriptor = (CollectionDescriptor) bazzesDescriptor.clone();

		assertTrue(clonedDescriptor.isChildRelationship());
		assertEquals("inverse", clonedDescriptor.getInverseProperty());
		assertTrue(clonedDescriptor.isOneToMany());
		assertNull(clonedDescriptor.getDisplayName());
	}
}
