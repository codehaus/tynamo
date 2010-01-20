package org.tynamo.seedentity.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tynamo.jpa.JPAEntityManagerSource;
import org.tynamo.jpa.JPATransactionManager;
import org.tynamo.seedentity.entities.AnotherThing;
import org.tynamo.seedentity.entities.Thing;

import javax.persistence.*;

public class SeedEntityImplTest {
	// @Inject
	// private SeedEntity seedEntity;
	private EntityManager em;

	@BeforeClass
	public void openSession() {
		//AnnotationConfiguration configuration = new AnnotationConfiguration();
		//configuration.addAnnotatedClass(Thing.class);
		//configuration.configure("/hibernate-test.cfg.xml");
		//SessionFactory sessionFactory = configuration.buildSessionFactory();
		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("testPU");
		em = emFactory.createEntityManager();
	}

	@Test
	public void seedEntities() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		List<Object> entities = new ArrayList<Object>();

		Thing thing = new Thing();
		thing.setName("MeinName");
		thing.setNumber(12);
		thing.setNumber2(16);

		Thing thing2 = new Thing();
		thing2.setName("MeinName");
		thing2.setNumber(12);
		thing2.setNumber2(16);

		AnotherThing aThing = new AnotherThing();
		aThing.setName("AnotherName");

		entities.add(thing);
		entities.add(thing);
		entities.add(thing2);
		aThing.setThing(thing2);
		entities.add(aThing);
		
		JPATransactionManager transactionManager = mock(JPATransactionManager.class);
		when(transactionManager.getEntityManager()).thenReturn(em);
		JPAEntityManagerSource emSource = mock(JPAEntityManagerSource.class);
		// Returning null is ok, this test doesn't use sessionFactory (nor does it commit()
		when(emSource.getEntityManagerFactory()).thenReturn(null);
		em.getTransaction().begin();
		SeedEntity service = new SeedEntityImpl(LoggerFactory.getLogger(SeedEntity.class), emSource, transactionManager, entities);
		em.getTransaction().commit();
		List<Thing> resultList = em.createQuery("select t from Thing t").getResultList();
		assertTrue(resultList.size() == 1);
		List<AnotherThing> aThingList = em.createQuery("select t from AnotherThing t").getResultList();
		assertTrue(resultList.size() == 1);
		//assertTrue(em.createCriteria(Thing.class).list().size() > 0);
	}

}
