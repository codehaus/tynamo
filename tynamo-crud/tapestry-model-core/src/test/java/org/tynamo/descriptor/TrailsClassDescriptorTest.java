/*
 * Copyright 2004 Chris Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.tynamo.descriptor;

import java.util.List;
import java.util.Arrays;
import java.util.Set;

import junit.framework.TestCase;
import org.tynamo.test.Bar;
import org.tynamo.test.BlogEntry;
import org.tynamo.test.Foo;
import org.tynamo.test.Searchee;
import ognl.Ognl;
import ognl.OgnlException;


/**
 * @author fus8882
 *         <p/>
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class TrailsClassDescriptorTest extends TestCase
{
	TrailsClassDescriptor classDescriptor;
	IdentifierDescriptor idProp;
	IPropertyDescriptor multiWordProp;

	public void setUp() throws Exception
	{
		classDescriptor = new TrailsClassDescriptor(Foo.class, "Foo");
		idProp = new IdentifierDescriptor(Foo.class, "id", String.class);

		multiWordProp = new TrailsPropertyDescriptor(Foo.class, "multiWordProperty", String.class);
		classDescriptor.getPropertyDescriptors().add(idProp);
		classDescriptor.getPropertyDescriptors().add(multiWordProp);
		classDescriptor.getMethodDescriptors().add(new TrailsMethodDescriptor(Foo.class, "foo", void.class, new Class[] {}));
		classDescriptor.setHasCyclicRelationships(true);
		classDescriptor.setShortDescription("a simple foo");
	}

	public void testClone() throws Exception
	{
		TrailsClassDescriptor clone = (TrailsClassDescriptor) classDescriptor.clone();
		assertEquals("still foo", Foo.class, clone.getType());
		assertEquals("2 props", 2, clone.getPropertyDescriptors().size());
		assertTrue("clone has id", clone.getPropertyDescriptor("id") instanceof IdentifierDescriptor);
		assertEquals("clone has id",idProp.getName(), clone.getIdentifierDescriptor().getName());
		assertEquals("still has a method", 1, clone.getMethodDescriptors().size());
		assertEquals("a simple foo", clone.getShortDescription());
		assertTrue("still has cyclic relationships", clone.getHasCyclicRelationships());
	}

	public void testCopyConstructor() throws Exception
	{
		TrailsClassDescriptor copiedDescriptor = new TrailsClassDescriptor(classDescriptor);
		assertEquals("Foo", copiedDescriptor.getDisplayName());
		assertEquals("2 properties", 2, copiedDescriptor.getPropertyDescriptors().size());
		assertTrue("still has cyclic relationships", copiedDescriptor.getHasCyclicRelationships());
	}

	public void testGetIdentifierProperty() throws Exception
	{
		assertEquals("right id prop", idProp,
			classDescriptor.getIdentifierDescriptor());

		classDescriptor.getPropertyDescriptors().add(new EmbeddedDescriptor(Foo.class, "blork", Bar.class));

		assertEquals("right id prop", idProp,
			classDescriptor.getIdentifierDescriptor());

	}

	public void testGetDescriptor() throws Exception
	{
		assertEquals("got right descriptor", multiWordProp,
			classDescriptor.getPropertyDescriptor("multiWordProperty"));
		assertNull("should return null if none found",
			classDescriptor.getPropertyDescriptor("doesntexist"));
		List descriptors = classDescriptor.getPropertyDescriptors(Arrays.asList("multiWordProperty", "id"));
		assertEquals("get 2 descriptors", 2, descriptors.size());
		assertEquals("in specified order", multiWordProp, descriptors.get(0));
	}

	public void testGetPluralName() throws Exception
	{
		classDescriptor = new TrailsClassDescriptor(Foo.class);
		classDescriptor.setDisplayName("Foo");
		assertEquals("Foos", classDescriptor.getPluralDisplayName());
	}

	public void testDisplayName() throws Exception
	{
		ReflectionDescriptorFactory descriptorFactory = new ReflectionDescriptorFactory();
		TrailsClassDescriptor classDescriptor = descriptorFactory.buildClassDescriptor(BlogEntry.class);
		assertEquals("Blog Entry", classDescriptor.getDisplayName());
	}

	public void testHasCyclicRelationshipsDefaultValueFalse() throws Exception
	{
		classDescriptor = new TrailsClassDescriptor(BlogEntry.class);
		assertFalse("default value should be false", classDescriptor.getHasCyclicRelationships());
	}

		public void testGetSearchableProperties()
	{
		TrailsClassDescriptor classDescriptor = new TrailsClassDescriptor(Searchee.class);
		classDescriptor.getPropertyDescriptors().add(new TrailsPropertyDescriptor(Foo.class, "someProperty", String.class));
		classDescriptor.getPropertyDescriptors().add(new IdentifierDescriptor(Foo.class, "id", String.class));
		classDescriptor.getPropertyDescriptors().add(new CollectionDescriptor(Foo.class, "name", Set.class));

		try {
			List<IPropertyDescriptor> searchableProperties = (List<IPropertyDescriptor>) Ognl.getValue("propertyDescriptors.{? searchable}", classDescriptor);
			assertEquals("should only be 2 search properties", 2, searchableProperties.size());
			assertEquals("someProperty", searchableProperties.get(0).getName());
		} catch (OgnlException e) {
			fail();
		}
	}
}
