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

import ognl.Ognl;
import ognl.OgnlException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.tynamo.test.Bar;
import org.tynamo.test.BlogEntry;
import org.tynamo.test.Foo;
import org.tynamo.test.Searchee;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class TynamoClassDescriptorTest extends Assert
{
	TynamoClassDescriptorImpl classDescriptor;
	IdentifierDescriptor idProp;
	TynamoPropertyDescriptor multiWordProp;

	@BeforeMethod
	public void setUp() throws Exception
	{
		classDescriptor = new TynamoClassDescriptorImpl(Foo.class);
		idProp = new IdentifierDescriptorImpl(Foo.class, "id", String.class);

		multiWordProp = new TynamoPropertyDescriptorImpl(Foo.class, "multiWordProperty", String.class);
		classDescriptor.getPropertyDescriptors().add(idProp);
		classDescriptor.getPropertyDescriptors().add(multiWordProp);
		classDescriptor.getMethodDescriptors().add(new TynamoMethodDescriptorImpl(Foo.class, "foo", void.class, new Class[]{}));
		classDescriptor.setHasCyclicRelationships(true);
	}

	@Test
	public void testClone() throws Exception
	{
		TynamoClassDescriptorImpl clone = (TynamoClassDescriptorImpl) classDescriptor.clone();
		assertEquals(Foo.class, clone.getType(), "still foo");
		assertEquals(clone.getPropertyDescriptors().size(), 2, "2 props");
		assertTrue(clone.getPropertyDescriptor("id") instanceof IdentifierDescriptor, "clone has id");
		assertEquals(idProp.getName(), clone.getIdentifierDescriptor().getName(), "clone has id");
		assertEquals(1, clone.getMethodDescriptors().size(), "still has a method");
		assertTrue(clone.getHasCyclicRelationships(), "still has cyclic relationships");
	}

	@Test public void testCopyConstructor() throws Exception
	{
		TynamoClassDescriptorImpl copiedDescriptor = new TynamoClassDescriptorImpl(classDescriptor);
		assertEquals(copiedDescriptor.getType().getSimpleName(), "Foo");
		assertEquals(2, copiedDescriptor.getPropertyDescriptors().size(), "2 properties");
		assertTrue(copiedDescriptor.getHasCyclicRelationships(), "still has cyclic relationships");
	}

	@Test public void testGetIdentifierProperty() throws Exception
	{
		assertEquals(idProp, classDescriptor.getIdentifierDescriptor(), "right id prop");

		classDescriptor.getPropertyDescriptors().add(new EmbeddedDescriptor(Foo.class, "blork", Bar.class));

		assertEquals(idProp, classDescriptor.getIdentifierDescriptor(), "right id prop");

	}

	@Test public void testGetDescriptor() throws Exception
	{
		assertEquals(multiWordProp, classDescriptor.getPropertyDescriptor("multiWordProperty"), "got right descriptor");
		assertNull(classDescriptor.getPropertyDescriptor("doesntexist"), "should return null if none found");
		List descriptors = classDescriptor.getPropertyDescriptors(Arrays.asList("multiWordProperty", "id"));
		assertEquals(2, descriptors.size(), "get 2 descriptors");
		assertEquals(multiWordProp, descriptors.get(0), "in specified order");
	}

	@Test public void testHasCyclicRelationshipsDefaultValueFalse() throws Exception
	{
		classDescriptor = new TynamoClassDescriptorImpl(BlogEntry.class);
		assertFalse(classDescriptor.getHasCyclicRelationships(), "default value should be false");
	}

	@Test public void testGetSearchableProperties()
	{
		TynamoClassDescriptorImpl classDescriptor = new TynamoClassDescriptorImpl(Searchee.class);
		classDescriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Foo.class, "someProperty", String.class));
		classDescriptor.getPropertyDescriptors().add(new IdentifierDescriptorImpl(Foo.class, "id", String.class));
		classDescriptor.getPropertyDescriptors().add(new CollectionDescriptor(Foo.class, "name", Set.class));

		try
		{
			List<TynamoPropertyDescriptor> searchableProperties = (List<TynamoPropertyDescriptor>) Ognl.getValue("propertyDescriptors.{? searchable}", classDescriptor);
			assertEquals(2, searchableProperties.size(), "should only be 2 search properties");
			assertEquals(searchableProperties.get(0).getName(), "someProperty");
		} catch (OgnlException e)
		{
			fail();
		}
	}
}
