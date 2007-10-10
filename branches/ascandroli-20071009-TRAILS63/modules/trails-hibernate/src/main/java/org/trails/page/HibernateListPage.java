package org.trails.page;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.hibernate.criterion.DetachedCriteria;

public abstract class HibernateListPage extends ListPage implements IExternalPage
{

	public void activateExternalPage(Object[] args, IRequestCycle cycle)
	{
		setCriteria(DetachedCriteria.forClass(getClassDescriptor().getType()));
	}

	public abstract DetachedCriteria getCriteria();

	public abstract void setCriteria(DetachedCriteria Criteria);

	public Class getType()
	{
		return getClassDescriptor().getType();
	}
}
