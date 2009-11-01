package org.tynamo.examples.recipe.pages;


import org.apache.tapestry5.Link;
import org.tynamo.hibernate.pages.HibernateNewPage;
import org.tynamo.util.DisplayNameUtils;
import org.tynamo.util.Utils;

public class Add extends HibernateNewPage
{

	public String getTitle()
	{
		return getMessages()
				.format(Utils.ADD_NEW_MESSAGE, DisplayNameUtils.getDisplayName(getClassDescriptor(), getMessages()));
	}

	public Link back()
	{
		return getPageRenderLinkSource().createPageRenderLinkWithContext(List.class, getClassDescriptor().getType());
	}
}
