package org.tynamo.examples.simple.pages;

import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.util.Version;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.hibernate.HibernateGridDataSource;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.search.FullTextSession;
import org.tynamo.components.SearchFilters;
import org.tynamo.descriptor.TynamoClassDescriptor;
import org.tynamo.descriptor.TynamoPropertyDescriptor;
import org.tynamo.hibernate.SearchableHibernateGridDataSource;
import org.tynamo.routing.annotations.At;
import org.tynamo.services.DescriptorService;
import org.tynamo.services.PersistenceService;
import org.tynamo.util.TynamoMessages;
import org.tynamo.util.Utils;

/**
 * Page for listing elements of a given type.
 *
 * @note:
 * When extending this page for customization purposes, it's better to copy & paste code than trying to use inheritance.
 *
 */
@At("/{0}")
public class List
{
	
	@Inject
	private DescriptorService descriptorService;

	@Inject
	private PersistenceService persistenceService;
	
	@Inject
	private FullTextSession session;
	
	@Inject
	private Messages messages;

	@Property(write = false)
	private Class beanType;
	
	@Persist(PersistenceConstants.FLASH)
	@Property
	private String searchTerms;

	@Property
	private Object bean;

	@InjectComponent
	private Grid grid;

	@InjectComponent
	private SearchFilters searchFilters;

	@OnEvent(EventConstants.ACTIVATE)
	Object onActivate(Class clazz)
	{
		if (clazz == null) return Utils.new404(messages);
		this.beanType = clazz;
		return null;
	}

	@OnEvent(EventConstants.PASSIVATE)
	Object[] passivate()
	{
		return new Object[]{beanType};
	}

	/**
	 * This is where you can perform any one-time per-render setup for your component. This is a good place to read
	 * component parameters and use them to set temporary instance variables.
	 * More info: http://tapestry.apache.org/tapestry5.1/guide/rendering.html
	 * {@see org.apache.tapestry5.annotations.SetupRender}
	 */
	@SetupRender
	void setupRender()
	{
		grid.reset();
	}

	/**
	 * The source of data for the Grid to display.
	 * This will usually be a List or array but can also be an explicit GridDataSource
	 * @throws ParseException 
	 */
	public GridDataSource getSource() throws ParseException
	{
		// return new TynamoGridDataSource(persistenceService, beanType);
//		return new HibernateGridDataSource(session, beanType);

		if (searchTerms == null) return new HibernateGridDataSource(session, beanType); 

		TynamoClassDescriptor classDescriptor = descriptorService.getClassDescriptor(beanType);
		java.util.List<String> fieldNames = new ArrayList<String>();
		java.util.List<TynamoPropertyDescriptor> propertyDescriptors = classDescriptor.getPropertyDescriptors();
		for (TynamoPropertyDescriptor propertyDescriptor : propertyDescriptors) {
			if (propertyDescriptor.isSearchable() && propertyDescriptor.isString())
				fieldNames.add(propertyDescriptor.getName());
		}
		
		MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36, fieldNames.toArray(new String[0]),
			new StandardAnalyzer(Version.LUCENE_36));
		// parser.setDefaultOperator(QueryParser.AND_OPERATOR); // overrides the default OR_OPERATOR, so that all words in the search are
		// required
		org.apache.lucene.search.Query query = parser.parse(searchTerms);

		// NOTE Hibernate Search DSL checks that the fields exists, otherwise it throws exceptions. Lucene is more forgiving
		// QueryBuilder qb = session.getSearchFactory().buildQueryBuilder().forEntity( beanType ).get();
		// org.apache.lucene.search.Query query = qb.keyword().onFields(fieldNames.toArray(new String[0])).matching(searchTerms).createQuery();

		return new SearchableHibernateGridDataSource(session, beanType, session.createFullTextQuery(query, beanType));
	}

	public Object[] getShowPageContext()
	{
		return new Object[]{beanType, bean};
	}

	public String getTitle()
	{
		return TynamoMessages.list(messages, beanType);
	}

	public String getNewLinkMessage()
	{
		return TynamoMessages.add(messages, beanType);
	}
	
	public boolean isSearchable()
	{
		return descriptorService.getClassDescriptor(beanType).isSearchable();
	}
	
	Object onSuccessFromFulltextSearch() {
		return null;
	}
	

}
