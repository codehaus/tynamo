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

import java.util.Locale;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IEngine;
import org.apache.tapestry.IPage;
import org.trails.page.TrailsPage;


/*
 * Created on Sep 26, 2004
 *
 */

/**
 * @author fus8882
 *
 */
public abstract class ListAllLink extends TypeNavigationLink
{

    /**
     * @return
     */
    public String getLinkText() {
    	Locale locale = null;
    	IComponent container = getContainer();
    	if (container != null) {
    		IPage page = container.getPage();
    		if (page != null) {
    			IEngine engine = page.getEngine();
    			if (engine != null) {
    				locale = engine.getLocale();
    			}
    		}
    	}
    	Object[] params = new Object[]{getClassDescriptor().getPluralDisplayName()};
       	return getResourceBundleMessageSource().getMessageWithDefaultValue("org.trails.component.listalllink",
       													params,
       													locale,
       													"[TRAILS][ORG.TRAILS.COMPONENT.LISTALLLINK]");
    }

	public Object getListPageName()
	{

		return getPageResolver().resolvePage(
				getPage().getRequestCycle(), 
				getTypeName(),
				TrailsPage.PageType.LIST).getPageName();
		
	}
}