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
package org.trails.component;


import org.apache.hivemind.Messages;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.test.Creator;
import org.apache.tapestry.components.Block;
import org.apache.tapestry.util.ComponentAddress;
import org.jmock.Mock;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.trails.descriptor.BlockFinder;
import org.trails.descriptor.IPropertyDescriptor;
import org.trails.descriptor.TrailsPropertyDescriptor;
import org.trails.page.IEditorBlockPage;
import org.trails.test.Foo;


/**
 * @author fus8882
 *         <p/>
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class PropertyEditorTest extends MockObjectTestCase
{

	PropertyEditor propertyEditor;
	IPropertyDescriptor descriptor;
	BlockFinder blockFinder;
	Messages messages;

	Creator creator = new Creator();

	public void setUp() throws Exception
	{
		messages = mock(Messages.class);
		blockFinder = mock(BlockFinder.class);

		propertyEditor = (PropertyEditor) creator.newInstance(PropertyEditor.class,
			new Object[]{"blockFinder", blockFinder, "messages", messages});

		descriptor = new TrailsPropertyDescriptor(Foo.class, "number", Double.class);
		propertyEditor.setDescriptor(descriptor);
	}

	public void testGetEditorAddress()
	{
		final ComponentAddress componentAddress = new ComponentAddress("page", "block");
		final IPropertyDescriptor descriptor2 = new TrailsPropertyDescriptor(Foo.class, "stuff", String.class);

		checking(new Expectations()
		{
			{
				atLeast(1).of(blockFinder).findBlockAddress(descriptor); will(returnValue(componentAddress));
				atLeast(1).of(blockFinder).findBlockAddress(descriptor2); will(returnValue(null));
			}
		});

		assertEquals(componentAddress, propertyEditor.getEditorAddress());

		propertyEditor.setDescriptor(descriptor2);
		assertNull(propertyEditor.getEditorAddress());
	}

	public void testGetBlock() throws Exception
	{

		final IEditorBlockPage page = mock(IEditorBlockPage.class);
		final IRequestCycle cycle = mock(IRequestCycle.class);
		final Block block = (Block) creator.newInstance(Block.class, new Object[]{"page", page});
		final ComponentAddress componentAddress = new ComponentAddress("page", "block");

		checking(new Expectations()
		{
			{
				ignoring(page).getIdPath(); will(returnValue(null)); // the test failed BADLY without this
				ignoring(page).getLocation(); will(returnValue(null)); // the test failed BADLY without this

				atLeast(1).of(page).getRequestCycle(); will(returnValue(cycle));
				atLeast(1).of(cycle).getPage("page"); will(returnValue(page));
				atLeast(1).of(page).getNestedComponent("block"); will(returnValue(block));
				
				atLeast(1).of(cycle).getPage(); will(returnValue(page));
				atLeast(1).of(page).getPageName(); will(returnValue("whatever"));

				atLeast(1).of(blockFinder).findBlockAddress(descriptor); will(returnValue(componentAddress));
			}
		});

		propertyEditor.setPage(page);
		assertEquals(block, propertyEditor.getBlock());
	}


}
