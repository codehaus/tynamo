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
package org.tynamo.page;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.RedirectException;
import org.jmock.Mock;
import org.tynamo.callback.UrlCallback;
import org.tynamo.component.ComponentTest;
import org.tynamo.descriptor.CollectionDescriptor;
import org.tynamo.descriptor.TrailsClassDescriptor;
import org.tynamo.descriptor.TrailsClassDescriptor;
import org.tynamo.persistence.PersistenceException;
import org.tynamo.test.Baz;
import org.tynamo.test.Foo;
import org.tynamo.validation.ValidationException;

import java.util.Set;

public class EditPageTest extends ComponentTest
{

	private static final String CALLBACK_URL = "http://home";
	Mock cycleMock = new Mock(IRequestCycle.class);

	Baz baz = new Baz();
	Foo foo = new Foo();

	EditPage fooEditPage;
	EditPage bazEditPage;

	final UrlCallback callback = new UrlCallback(CALLBACK_URL);

	CollectionDescriptor bazzesDescriptor = new CollectionDescriptor(Foo.class, "bazzes", Set.class);
	TrailsClassDescriptor fooDescriptor = new TrailsClassDescriptor(Foo.class, "Foo");
	TrailsClassDescriptor bazDescriptor = new TrailsClassDescriptor(Baz.class, "Baz");

	public void setUp() throws Exception
	{
		foo.setName("foo");

		fooEditPage = buildEditPage();
		fooEditPage.setClassDescriptor(fooDescriptor);
		fooEditPage.setModel(foo);

		bazzesDescriptor.setElementType(Baz.class);

		bazEditPage = buildEditPage();
		bazEditPage.setModel(baz);
		bazEditPage.setModelNew(true);

		callbackStack.clear();
		callbackStack.push(callback);
		callbackStack.push(new UrlCallback("editPageCallback"));

		bazEditPage.setAssociationDescriptor(bazzesDescriptor);
		bazEditPage.setParent(foo);
	}

	public void testGetTitle() throws Exception
	{

		bazEditPage.setClassDescriptor(bazDescriptor);
		bazEditPage.setModelNew(true);
		bazEditPage.setParent(new Foo());

		assertEquals("Edit Foo", fooEditPage.getTitle());
		assertEquals("Add Baz", bazEditPage.getTitle());
	}

	public void testSave()
	{
		Foo foo2 = new Foo();
		foo2.setName("foo2");
		persistenceMock.expects(once()).method("save").with(same(foo)).will(returnValue(foo2));
		pageServiceMock.expects(once()).method("getLink");
		fooEditPage.save((IRequestCycle) cycleMock.proxy());
		assertEquals(foo2, fooEditPage.getModel());
	}

	public void testSaveWithException()
	{
		persistenceMock.expects(atLeastOnce()).method("save").with(same(foo)).will(
				throwException(new ValidationException("error")));
		fooEditPage.save((IRequestCycle) cycleMock.proxy());
		assertTrue("delegate has errors", delegate.getHasErrors());

	}

	public void testSaveAndReturn()
	{
		persistenceMock.expects(once()).method("save").with(same(foo));

		try
		{
			fooEditPage.saveAndReturn((IRequestCycle) cycleMock.proxy());
			fail();

		} catch (RedirectException e)
		{
			assertEquals(CALLBACK_URL, e.getRedirectLocation());
		}
		persistenceMock.verify();
	}

	public void testCancel()
	{
		persistenceMock.expects(never());
		try
		{
			fooEditPage.cancel((IRequestCycle) cycleMock.proxy());
			fail();

		} catch (RedirectException e)
		{
			assertEquals(CALLBACK_URL, e.getRedirectLocation());
		}
		persistenceMock.verify();
	}

	public void testAddToChildCollection()
	{
		bazzesDescriptor.setChildRelationship(true);
		persistenceMock.expects(once()).method("addToCollection").with(eq("addBaz"), eq(baz), eq(foo));

		try
		{
			bazEditPage.saveAndReturn((IRequestCycle) cycleMock.proxy());
			fail();

		} catch (RedirectException e)
		{
			assertEquals(CALLBACK_URL, e.getRedirectLocation());
		}

	}

	public void testAddToNonChildCollection()
	{
		bazzesDescriptor.setChildRelationship(false);
		persistenceMock.expects(once()).method("addToCollection").with(eq("addBaz"), eq(baz), eq(foo));

		try
		{
			bazEditPage.saveAndReturn((IRequestCycle) cycleMock.proxy());
			fail();

		} catch (RedirectException e)
		{
			assertEquals(CALLBACK_URL, e.getRedirectLocation());
		}

	}

	public void testRemoveFrom()
	{
		foo.getBazzes().add(baz);

		persistenceMock.expects(once()).method("removeFromCollection").with(eq("removeBaz"), eq(baz), eq(foo));

		try
		{

			bazEditPage.remove((IRequestCycle) cycleMock.proxy());
			fail();

		} catch (RedirectException e)
		{
			assertEquals(CALLBACK_URL, e.getRedirectLocation());
		}

	}

	public void testRemove()
	{
		persistenceMock.expects(once()).method("remove").with(same(foo));

		try
		{
			assertNotNull(fooEditPage.remove((IRequestCycle) cycleMock.proxy()));
			fail();

		} catch (RedirectException e)
		{
			assertEquals(CALLBACK_URL, e.getRedirectLocation());
		}

		persistenceMock.verify();
	}

	public void testRemoveWithException() throws Exception
	{
		persistenceMock.expects(once()).method("remove").with(same(foo))
				.will(throwException(new PersistenceException()));
		fooEditPage.remove((IRequestCycle) cycleMock.proxy());
		assertTrue(fooEditPage.getDelegate().getHasErrors());
	}
}
