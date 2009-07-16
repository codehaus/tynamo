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
package org.trails.page;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.tapestry.IEngine;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import org.apache.tapestry.valid.IValidationDelegate;
import org.apache.tapestry.valid.NumberValidator;
import org.apache.tapestry.valid.StringValidator;
import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.jmock.Mock;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.trails.callback.CollectionCallback;
import org.trails.callback.EditCallback;
import org.trails.callback.ListCallback;
import org.trails.component.ComponentTest;
import org.trails.descriptor.IClassDescriptor;
import org.trails.descriptor.IPropertyDescriptor;
import org.trails.descriptor.IdentifierDescriptor;
import org.trails.descriptor.TrailsClassDescriptor;
import org.trails.descriptor.DescriptorService;
import org.trails.descriptor.TrailsPropertyDescriptor;
import org.trails.hibernate.HasAssignedIdentifier;
import org.trails.i18n.DefaultTrailsResourceBundleMessageSource;
import org.trails.page.EditPage;
import org.trails.page.ListPage;
import org.trails.persistence.PersistenceException;
import org.trails.persistence.PersistenceService;
import org.trails.test.Bar;
import org.trails.test.Baz;
import org.trails.test.Foo;
import org.trails.test.IBar;
import org.trails.validation.ValidationException;


/**
 * @author fus8882
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EditPageTest extends ComponentTest
{
    Mock cycleMock = new Mock(IRequestCycle.class);
    Baz baz = new Baz();
    IClassDescriptor descriptor = new TrailsClassDescriptor(Bar.class);
    EditPage editPage;
    EditPage bazEditPage;
    Foo foo = new Foo();
    IdentifierDescriptor idDescriptor;
    Mock validatorMock;
    ListCallback listCallBack = new ListCallback("FooList", Foo.class.getName());
    ListPage listPage;
    
    public void setUp() throws Exception
    {
        foo.setName("foo");
        validatorMock = new Mock(IValidationDelegate.class);
        listPage = buildTrailsPage(ListPage.class);
        editPage = buildEditPage();
        editPage.setModel(foo);
        

        idDescriptor = new IdentifierDescriptor(Foo.class, "id", Foo.class);
        descriptor.getPropertyDescriptors().add(idDescriptor);
       
        
        // attach the visit to the page
        
        
        editPage.getCallbackStack().push(listCallBack);
        
        bazEditPage = buildEditPage();
        bazEditPage.setModel(baz);      
    }
    
    public void testOnFormSubmit()
    {
        Mock callbackMock = new Mock(ICallback.class);
        callbackMock.expects(once()).method("performCallback").with(isA(IRequestCycle.class));
        editPage.setNextPage((ICallback)callbackMock.proxy());
        editPage.onFormSubmit((IRequestCycle)cycleMock.proxy());
        callbackMock.verify();
    }

    public void testGetPropertyDescriptors()
    {
        descriptorServiceMock.expects(once()).method("getClassDescriptor")
        	.with(same(Foo.class)).will(returnValue(descriptor));
        
        assertEquals("got descriptors", descriptor,
            editPage.getClassDescriptor());
        descriptorServiceMock.verify();
    }
    
    public void testGetTitle() throws Exception
    {
        IClassDescriptor fooDescriptor = new TrailsClassDescriptor(Foo.class, "Foo");
        IClassDescriptor bazDescriptor = new TrailsClassDescriptor(Baz.class, "Baz");
        
        descriptorServiceMock.expects(once()).method("getClassDescriptor").with(eq(Foo.class))
        	.will(returnValue(fooDescriptor));
        descriptorServiceMock.expects(atLeastOnce()).method("getClassDescriptor").with(eq(Baz.class))
    		.will(returnValue(bazDescriptor));       
        assertEquals("Edit Foo", editPage.getTitle());
        CollectionCallback bazCallback = new CollectionCallback("fooPage", foo, "bazzes.add", "bazzes.remove");
        bazEditPage.getCallbackStack().push(bazCallback);
        bazDescriptor.getPropertyDescriptors().add(new IdentifierDescriptor(Foo.class, "id", Baz.class));
        assertEquals("Add Baz", bazEditPage.getTitle());
    }

    public void testIsNew() throws Exception
    {
        
        IClassDescriptor barDescriptor = new TrailsClassDescriptor(Bar.class);
        IdentifierDescriptor barIdDescriptor = 
            new IdentifierDescriptor(Foo.class, "id", Integer.class);
        barDescriptor.getPropertyDescriptors().add(barIdDescriptor);
        idDescriptor.setGenerated(false);

        descriptorServiceMock.expects(atLeastOnce()).method("getClassDescriptor")
			.with(same(Bar.class)).will(returnValue(barDescriptor));

//        descriptorServiceMock.expects(atLeastOnce()).method("getClassDescriptor")
//            .with(same(Foo.class)).will(returnValue(barDescriptor));
        
        assertTrue("is new", editPage.isModelNew());
        ((HasAssignedIdentifier)foo).onSave();
        assertFalse("not new", editPage.isModelNew());
        Bar bar = new Bar();
        editPage.setModel(bar);
        assertTrue("is new", editPage.isModelNew());
        bar.setId(new Integer(1));
        assertFalse("not new", editPage.isModelNew());
    }
    
    public void testSave()
    {
        Foo foo2 = new Foo();
        foo2.setName("foo2");
        persistenceMock.expects(once()).method("save").with(same(foo)).will(returnValue(foo2));
        editPage.save((IRequestCycle)cycleMock.proxy());
        assertEquals(foo2, editPage.getModel());
    }
    
    public void testSaveWithException()
    {
        persistenceMock.expects(atLeastOnce()).method("save").with(same(foo)).will(
                throwException(new ValidationException("error")));        
        editPage.save((IRequestCycle)cycleMock.proxy());
        assertTrue("delegate has errors", delegate.getHasErrors());
              
    }
    
    public void testSaveWithInvalidStateException() throws Exception
    {
        descriptorServiceMock.expects(once()).method("getClassDescriptor")
            .with(same(Foo.class)).will(returnValue(descriptor));        
        descriptor.getPropertyDescriptors().add(new TrailsPropertyDescriptor(Foo.class, "description", String.class));
        InvalidValue invalidValue = new InvalidValue("is too long", Bar.class, "description", "blarg", new Baz());
        InvalidStateException invalidStateException = new InvalidStateException(new InvalidValue[] {invalidValue});

        persistenceMock.expects(once()).method("save").with(same(foo)).will(throwException(invalidStateException));
        editPage.save((IRequestCycle)cycleMock.proxy());
        assertTrue("delegate has errors", delegate.getHasErrors());
  
    }
    
    public void testSaveAndReturn()
    {
        
        cycleMock.expects(once()).method("getPage").with(eq("FooList")).will(returnValue(
            listPage));
        cycleMock.expects(once()).method("activate").with(eq(listPage));
        persistenceMock.expects(once()).method("save").with(same(foo));
        persistenceMock.expects(once()).method("getAllInstances").with(eq(Foo.class)).will(returnValue(new ArrayList()));
        editPage.saveAndReturn((IRequestCycle)cycleMock.proxy());
        cycleMock.verify();
        persistenceMock.verify();
    }
    
//    public void testAddToChildCollection()
//    {
//        
//        //persistenceMock.expects(once()).method("save").with(eq(baz));
//        makeBazCallback(true);
//        bazEditPage.saveAndReturn((IRequestCycle)cycleMock.proxy());
//        assertEquals("1 baz", 1, foo.getBazzes().size());
//    }
    
    public void testAddToNonChildCollection()
    {
        
        persistenceMock.expects(once()).method("save").with(eq(baz));
        makeBazCallback(bazEditPage, false);
        bazEditPage.saveAndReturn((IRequestCycle)cycleMock.proxy());
        assertEquals("1 baz", 1, foo.getBazzes().size());
    }
    
    private void makeBazCallback(EditPage editPage, boolean isChild)
    {
        CollectionCallback bazCallback = new CollectionCallback("fooPage", foo, "bazzes.add", "bazzes.remove");
        bazCallback.setChildRelationship(isChild);
        editPage.getCallbackStack().push(bazCallback);
        cycleMock.expects(once()).method("getPage").with(eq("fooPage")).will(returnValue(editPage));
        cycleMock.expects(once()).method("activate").with(eq(editPage));
        
    }

    public void testRemoveFromNonChildCollection()
    {
        foo.getBazzes().add(baz);
        
        persistenceMock.expects(once()).method("remove").with(eq(baz));
        makeBazCallback(bazEditPage, false);
        bazEditPage.remove((IRequestCycle)cycleMock.proxy());
        assertEquals("no bazzes", 0, foo.getBazzes().size());
    }
 
//    public void testRemoveFromChildCollection()
//    {
//        foo.getBazzes().add(baz);
//       
//        makeBazCallback(true);
//        bazEditPage.remove((IRequestCycle)cycleMock.proxy());
//        assertEquals("no bazzes", 0, foo.getBazzes().size());
//    }
    
    public void testRemove()
    {
        
        Mock cycleMock = new Mock(IRequestCycle.class);
        
        // Pretend Foo has a custom page
        ArrayList instances = new ArrayList();
        cycleMock.expects(once()).method("getPage").with(eq("FooList")).will(returnValue(
                listPage));
        persistenceMock.expects(once()).method("getAllInstances")
                       .with(same(Foo.class)).will(returnValue(instances));
        persistenceMock.expects(once()).method("remove").with(same(foo));
        cycleMock.expects(atLeastOnce()).method("activate").with(same(listPage));

        editPage.remove((IRequestCycle) cycleMock.proxy());
        assertEquals("instances set", instances, listPage.getInstances());

        cycleMock.verify();
        persistenceMock.verify();
        
        
    }
    
    public void testPushCallback() throws Exception
    {
        editPage.pushCallback();
        assertTrue(editPage.getCallbackStack().pop() instanceof EditCallback);
    }
    

}