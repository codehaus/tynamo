package org.trails.page;

import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.hibernate.lucene.Environment;
import org.hibernate.lucene.Indexed;
import org.trails.TrailsRuntimeException;
import org.trails.component.Utils;
import org.trails.descriptor.IClassDescriptor;
import org.trails.descriptor.IPropertyDescriptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public abstract class LuceneSearchPage extends SearchPage
{

    @InjectObject("spring:hibernateProperties")
    public abstract Properties getCfg();

    private static final Log LOG = LogFactory.getLog(LuceneSearchPage.class);

    public void search(IRequestCycle iRequestCycle)
    {
        ListPage listPage = (ListPage) Utils.findPage(iRequestCycle,
                Utils.unqualify(getExampleModel().getClass().getName()) + "List", "List");

//        listPage.setInstances(getPersistenceService().getInstances(getExampleModel()));
        listPage.setInstances(searchLucene(getExampleModel()));

        pushCallback();
        iRequestCycle.activate(listPage);
    }

    public List searchLucene(Object example)
    {
        Integer[] ids = null;
        List result = new ArrayList();
        int tmp;// to check consistency

        try
        {
            String indexDirName = getCfg().getProperty(Environment.INDEX_BASE_DIR);
            String fileName = example.getClass().getAnnotation(Indexed.class).index();

            IClassDescriptor classDescriptor = getDescriptorService().getClassDescriptor(example.getClass());

            File f = new File(indexDirName, fileName);

            boolean create = false;
            FSDirectory directory = FSDirectory.getDirectory(f, create);

            IndexReader reader = IndexReader.open(directory);

            IndexSearcher searcher = new IndexSearcher(reader);

            org.apache.lucene.search.Query query = buildLuceneQuery(example);

            Hits hits = searcher.search(query);
            if (hits.length() < 1)
            {
                searcher.close();
                return new ArrayList();
            }
            tmp = hits.length();
            ids = new Integer[hits.length()];
            for (int i = 0; i < hits.length(); i++)
            {
                ids[i] = new Integer(hits.doc(i).getField(classDescriptor.getIdentifierDescriptor().getLuceneFieldName()).stringValue());
                result.add(getPersistenceService().getInstance(example.getClass(), ids[i]));
            }
            searcher.close();
        }
        catch (Exception e)
        {
            LOG.error("Lucene error : " + e);
            e.printStackTrace();
            return new ArrayList();
        }

        if (result.size() != tmp)
        {
            LOG.error("Inconsistency between Database and Lucene!");
            return new ArrayList();
        }
        return result;
    }

    protected org.apache.lucene.search.Query buildLuceneQuery(Object example) throws ParseException, OgnlException
    {
        IClassDescriptor classDescriptor = getDescriptorService().getClassDescriptor(example.getClass());
        BooleanQuery query = new BooleanQuery();

        for (IPropertyDescriptor propertyDescriptor : (List<IPropertyDescriptor>) classDescriptor.getPropertyDescriptors())
        {
            if (propertyDescriptor.isIndexedByLucene())
            {
                Object value = Ognl.getValue(propertyDescriptor.getName(), example);
                if ((value != null) && (!"".equals(value.toString())))
                {
                    org.apache.lucene.search.Query lquery = QueryParser.parse(value.toString(), propertyDescriptor.getLuceneFieldName(), new StandardAnalyzer());
                    query.add(lquery, true, false); //this is an AND query. Use false, false to get an OR query
                }
            }
        }
        return query;
    }

    public String[] getSearchableProperties()
    {
        try
        {
            // Danger: gross code
            ArrayList<String> stringList = new ArrayList<String>();
            stringList.addAll(
                    (List) Ognl.getValue("propertyDescriptors.{? indexedByLucene}.{name}", getClassDescriptor()));
            return stringList.toArray(new String[]{});
        }
        catch (OgnlException oe)
        {
            throw new TrailsRuntimeException(oe);
        }
    }
}
