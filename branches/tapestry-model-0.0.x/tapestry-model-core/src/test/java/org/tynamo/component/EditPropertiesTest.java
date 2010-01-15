/*
 * Created on Jan 4, 2005
 *
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
package org.tynamo.component;

import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;

import org.tynamo.descriptor.TrailsClassDescriptor;
import org.tynamo.descriptor.IPropertyDescriptor;
import org.tynamo.descriptor.TrailsClassDescriptor;
import org.tynamo.descriptor.TrailsPropertyDescriptor;
import org.tynamo.test.Foo;

/**
 * @author fus8882
 *         <p/>
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class EditPropertiesTest extends ComponentTest
{

	public void testGetPropertyDescriptors() throws Exception
	{
		TrailsClassDescriptor classDescriptor = new TrailsClassDescriptor(Foo.class);
		IPropertyDescriptor nameDescriptor = new TrailsPropertyDescriptor(Foo.class,
			"name", String.class);
		IPropertyDescriptor dateDescriptor = new TrailsPropertyDescriptor(Foo.class,
			"date", Date.class);
		IPropertyDescriptor numberDescriptor = new TrailsPropertyDescriptor(Foo.class,
			"nubmer", Double.class);
		classDescriptor.getPropertyDescriptors().add(nameDescriptor);
		classDescriptor.getPropertyDescriptors().add(dateDescriptor);
		classDescriptor.getPropertyDescriptors().add(numberDescriptor);

		EditProperties propertyTable = (EditProperties) creator.newInstance(EditProperties.class,
				new Object[]{
					"classDescriptor", classDescriptor,
				});

		assertEquals("got 3", 3, propertyTable.getPropertyDescriptors().size());
		nameDescriptor.setHidden(true);
		assertEquals("got 2", 2, propertyTable.getPropertyDescriptors().size());
		assertFalse(propertyTable.getPropertyDescriptors().contains(nameDescriptor));

		propertyTable = (EditProperties) creator.newInstance(EditProperties.class,
				new Object[]{
					"classDescriptor", classDescriptor,
					"propertyNames", Arrays.asList("date", "name")
				});

		assertTrue(propertyTable.getPropertyDescriptors().contains(nameDescriptor));
		assertEquals("got 2", 2, propertyTable.getPropertyDescriptors().size());
		assertEquals("first one is date", dateDescriptor,
			propertyTable.getPropertyDescriptors().get(0));
	}
}
