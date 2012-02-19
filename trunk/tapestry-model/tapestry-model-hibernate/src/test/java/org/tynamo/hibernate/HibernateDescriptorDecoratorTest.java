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
package org.tynamo.hibernate;

import org.apache.tapestry5.func.F;
import org.apache.tapestry5.func.Predicate;
import org.apache.tapestry5.hibernate.HibernateCoreModule;
import org.apache.tapestry5.hibernate.HibernateModule;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.services.TapestryModule;
import org.testng.Assert;
import org.testng.annotations.*;
import org.tynamo.descriptor.*;
import org.tynamo.descriptor.decorators.DescriptorDecorator;
import org.tynamo.hibernate.services.HibernateDescriptorDecorator;
import org.tynamo.services.TynamoCoreModule;
import org.tynamo.testhibernate.*;

import java.util.Date;
import java.util.List;
import java.util.Set;


public class HibernateDescriptorDecoratorTest
{

	DescriptorDecorator hibernateDescriptorDecorator;
	TynamoClassDescriptor classDescriptor;

	private static Registry registry;

	@BeforeSuite
	public final void setup_registry()
	{
		RegistryBuilder builder = new RegistryBuilder();
		builder.add(TapestryModule.class);
		builder.add(HibernateCoreModule.class);
		builder.add(HibernateModule.class);
		builder.add(TynamoCoreModule.class);
		builder.add(TestModule.class);

		registry = builder.build();
		registry.performRegistryStartup();

	}

	@AfterSuite
	public final void shutdown_registry()
	{
		registry.shutdown();

		registry = null;
	}

	@AfterMethod
	public final void cleanupThread()
	{
		registry.cleanupThread();
	}

	@BeforeMethod
	public void setUp()
	{
		hibernateDescriptorDecorator = registry.autobuild(HibernateDescriptorDecorator.class);

		TynamoClassDescriptor fooDescriptor = new TynamoClassDescriptorImpl(Foo.class);
		fooDescriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Foo.class, "bazzes", Set.class));
		fooDescriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Foo.class, "bings", Set.class));
		fooDescriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Foo.class, "id", Integer.class));
		fooDescriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Foo.class, "name", String.class));
		fooDescriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Foo.class, "hidden", String.class));
		fooDescriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Foo.class, "date", Date.class));
		fooDescriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Foo.class, "readOnly", String.class));
		fooDescriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Foo.class, "multiWordProperty", String.class));
		fooDescriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Foo.class, "primitive", boolean.class));
		fooDescriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Foo.class, "bar", IBar.class));
		fooDescriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Foo.class, "fromFormula", String.class));

		classDescriptor = hibernateDescriptorDecorator.decorate(fooDescriptor);
	}

	@Test
	public void testNameDescriptor() throws Exception
	{
		TynamoPropertyDescriptor nameDescriptor = classDescriptor.getPropertyDescriptor("name");
		Assert.assertEquals(nameDescriptor.getName(), "name", "is name");
	}

	@Test
	public void testIdDescriptor() throws Exception
	{
		IdentifierDescriptor idDescriptor = (IdentifierDescriptor) classDescriptor.getIdentifierDescriptor();
		Assert.assertTrue(idDescriptor.isIdentifier(), "is id");
		Assert.assertFalse(idDescriptor.isGenerated(), "not generated");
	}

	@Test
	public void testFormulaDescriptor() throws Exception
	{
		TynamoPropertyDescriptor formulaDescriptor = classDescriptor.getPropertyDescriptor("fromFormula");
		Assert.assertTrue(formulaDescriptor.isReadOnly());
	}

	@Test
	public void testCollectionDescriptor() throws Exception
	{

		CollectionDescriptor bazzesDescriptor = (CollectionDescriptor) classDescriptor.getPropertyDescriptor("bazzes");
		Assert.assertTrue(bazzesDescriptor.isCollection(), "bazzes is a collection");
		Assert.assertEquals(bazzesDescriptor.getElementType(), Baz.class, "right element type");
		//TODO Fix when hibernate annotations add support for this..
		//assertTrue("bazzes are children", bazzesDescriptor.isChildRelationship());
		Assert.assertTrue(bazzesDescriptor.isOneToMany());
		Assert.assertEquals(bazzesDescriptor.getInverseProperty(), "foo", "bazzes are mapped by 'foo' property in Baz");
		Assert.assertTrue(classDescriptor.getHasCyclicRelationships(), "Foo has a cyclic relationship");
	}

	@Test
	public void testGetClassDescriptors() throws Exception
	{

		Assert.assertFalse(classDescriptor.isChild(), "not a child");
		List<TynamoPropertyDescriptor> propertyDescriptors = classDescriptor.getPropertyDescriptors();
		Assert.assertEquals(propertyDescriptors.size(), 11, "got 11");

		class NameFilter implements Predicate<TynamoPropertyDescriptor>
		{
			private String nameToFilter;

			NameFilter(String nameToFilter)
			{
				this.nameToFilter = nameToFilter;
			}

			@Override
			public boolean accept(TynamoPropertyDescriptor tynamoPropertyDescriptor)
			{
				return nameToFilter.equals(tynamoPropertyDescriptor.getName());
			}
		}

		TynamoPropertyDescriptor barDescriptor = F.flow(propertyDescriptors).filter(new NameFilter("bar")).toList().get(0);

		Assert.assertEquals(barDescriptor.getName(), "bar", "name");
		Assert.assertTrue(!barDescriptor.isIdentifier(), "is not an id");

		TynamoPropertyDescriptor hiddenDescriptor = F.flow(propertyDescriptors).filter(new NameFilter("hidden")).toList().get(0);
		Assert.assertNotNull(hiddenDescriptor, "didn't blow up");

		TynamoPropertyDescriptor primitiveDescriptor = F.flow(propertyDescriptors).filter(new NameFilter("primitive")).toList().get(0);
		Assert.assertTrue(primitiveDescriptor.isBoolean(), "is boolean");

	}

	@Test
	public void testIsObjectReference() throws Exception
	{
		TynamoPropertyDescriptor propertyDescriptor = classDescriptor.getPropertyDescriptor("bar");
		Assert.assertTrue(propertyDescriptor.isObjectReference());
		Assert.assertEquals(propertyDescriptor.getPropertyType(), Bar.class, "got right class");

		TynamoPropertyDescriptor primitiveDescriptor = classDescriptor.getPropertyDescriptor("primitive");
		Assert.assertFalse(primitiveDescriptor.isObjectReference());
	}

	@Test
	public void testLengthLarge() throws Exception
	{
		TynamoPropertyDescriptor multiWordDescriptor = classDescriptor.getPropertyDescriptor("multiWordProperty");
		Assert.assertTrue(multiWordDescriptor.isLarge());
		Assert.assertEquals(multiWordDescriptor.getLength(), 101, "right length");
		TynamoPropertyDescriptor nameDescriptor = classDescriptor.getPropertyDescriptor("name");
		Assert.assertFalse(nameDescriptor.isLarge(), "not large");
	}

	@Test
	public void testInheritance() throws Exception
	{
		TynamoClassDescriptor descendantDescriptor = new TynamoClassDescriptorImpl(Descendant.class);
		descendantDescriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Foo.class, "bazzes", Set.class));
		descendantDescriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Foo.class, "extra", String.class));
		descendantDescriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Foo.class, "id", Integer.class));
		descendantDescriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Foo.class, "name", String.class));
		TynamoClassDescriptor decorated = hibernateDescriptorDecorator.decorate(descendantDescriptor);
		Assert.assertEquals(4, decorated.getPropertyDescriptors().size());
	}

	@Test
	public void testEmbedded() throws Exception
	{
		TynamoClassDescriptor embeddorDescriptor = new TynamoClassDescriptorImpl(Embeddor.class);
		embeddorDescriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Embeddor.class, "embeddee", Embeddee.class));
		TynamoClassDescriptor decorated = hibernateDescriptorDecorator.decorate(embeddorDescriptor);
		TynamoPropertyDescriptor propertyDescriptor = decorated.getPropertyDescriptors().get(0);
		Assert.assertTrue(propertyDescriptor.isEmbedded());
		EmbeddedDescriptor embeddedDescriptor = (EmbeddedDescriptor) propertyDescriptor;
		Assert.assertEquals("embeddee", embeddedDescriptor.getName());
		Assert.assertEquals(Embeddor.class, embeddedDescriptor.getBeanType(), "right bean type");
		Assert.assertEquals(3, embeddedDescriptor.getPropertyDescriptors().size(), "3 prop descriptors");
	}

	@Test
	public void testTransient() throws Exception
	{
		TynamoClassDescriptor descriptor = new TynamoClassDescriptorImpl(Bar.class);
		descriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Bar.class, "name", String.class));
		descriptor.getPropertyDescriptors().add(new TynamoPropertyDescriptorImpl(Bar.class, "transientProperty", String.class));
		TynamoClassDescriptor decorated = hibernateDescriptorDecorator.decorate(descriptor);
		Assert.assertFalse(decorated.getPropertyDescriptor("transientProperty").isSearchable());
		Assert.assertTrue(decorated.getPropertyDescriptor("name").isSearchable());
	}
}
