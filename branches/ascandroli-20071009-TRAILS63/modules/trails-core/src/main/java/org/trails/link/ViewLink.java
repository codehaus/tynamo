package org.trails.link;

import org.trails.page.PageType;
import org.trails.component.AbstractModelNavigationLink;

/**
 * This component displays a link to the ViewPage for an object
 */
public abstract class ViewLink extends AbstractModelNavigationLink
{
	public PageType getPageType()
	{
		return PageType.View;
	}
}