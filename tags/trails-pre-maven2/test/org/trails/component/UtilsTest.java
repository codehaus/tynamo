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

import junit.framework.TestCase;

import org.trails.test.Foo;


/**
 * @author fus8882
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UtilsTest extends TestCase
{
    public void testUnqualify()
    {
        assertEquals("Foo", Utils.unqualify(Foo.class.getName()));
    }
    
    public void testPluralize()
    {
        assertEquals("keys", Utils.pluralize("key"));
        assertEquals("nouns", Utils.pluralize("noun"));
        assertEquals("words", Utils.pluralize("word"));
        assertEquals("properties", Utils.pluralize("property"));
        assertEquals("bosses", Utils.pluralize("boss"));
    }
}