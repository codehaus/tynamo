package org.trails.lucene;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.trails.persistence.PersistenceService;

public class LuceneStartup
{



    public void main(String args[])
    {

        ApplicationContext context = new FileSystemXmlApplicationContext("WEB-INF/applicationContext.xml");
        PersistenceService persistenceService = (PersistenceService) context.getBean("persistenceService");


/*
        List<IndexedPojo> list = persistenceService.getAllInstances(IndexedPojo.class);

        IndexedPojo p = new IndexedPojo();
        p = persistenceService.save(p);

        for (IndexedPojo proveedor : list)
        {
            persistenceService.remove(proveedor);
            persistenceService.save(proveedor);
        }

        persistenceService.remove(p);
*/


    }
}
