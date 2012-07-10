package org.tynamo.seedentity.jpa.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.tapestry5.ioc.internal.services.PropertyAccessImpl;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.jpa.EntityManagerManager;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tynamo.seedentity.SeedEntityIdentifier;
import org.tynamo.seedentity.jpa.entities.AnotherThing;
import org.tynamo.seedentity.jpa.entities.PrettyUniqueThing;
import org.tynamo.seedentity.jpa.entities.Thing;
import org.tynamo.seedentity.jpa.entities.ThingWithMultipleUniques;

public class SeedEntityImplTest {
	// @Inject
	// private SeedEntity seedEntity;
	private EntityManager em;
	private PropertyAccess propertyAccess = new PropertyAccessImpl();

	@BeforeClass
	public void openSession() {
		// AnnotationConfiguration configuration = new AnnotationConfiguration();
		// configuration.addAnnotatedClass(Thing.class);
		// configuration.configure("/hibernate-test.cfg.xml");
		// SessionFactory sessionFactory = configuration.buildSessionFactory();
		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("testPU");
		em = emFactory.createEntityManager();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void seedEntities() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		List<Object> entities = new ArrayList<Object>();

		entities.add("alter table Thing add column extracolumn int");
		entities.add("alter table Thing add column extracolumn int");

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
		entities.add(new AnotherThing());

		// SEE: TYNAMO-151
		entities.add(new PrettyUniqueThing(thing, 1));
		entities.add(new PrettyUniqueThing(thing, 1));

		entities.add(new SeedEntityIdentifier(new ThingWithMultipleUniques("hello", "world"), "firstName"));
		entities.add(new SeedEntityIdentifier(new ThingWithMultipleUniques("hello", "anotherWorld"), "firstName"));

		Map<String, EntityManager> entityManagers = new HashMap<String, EntityManager>();
		entityManagers.put("", em);
		EntityManagerManager entityManagerManager = mock(EntityManagerManager.class);
		when(entityManagerManager.getEntityManagers()).thenReturn(entityManagers);
		// em.getTransaction().begin();
		SeedEntity service = new SeedEntityImpl(LoggerFactory.getLogger(SeedEntity.class), propertyAccess,
			entityManagerManager, "", entities);
		// em.getTransaction().commit();
		List<Thing> resultList = em.createQuery("select t from Thing t").getResultList();
		assertTrue(resultList.size() == 1);
		List<AnotherThing> aThingList = em.createQuery("select t from AnotherThing t").getResultList();
		assertTrue(aThingList.size() == 2);
		List<AnotherThing> aPrettyUniqueThingList = em.createQuery("select t from PrettyUniqueThing t").getResultList();
		assertTrue(aPrettyUniqueThingList.size() == 1);
		// assertTrue(em.createCriteria(Thing.class).list().size() > 0);

		List<AnotherThing> thingWithMultipleUniquesList = em.createQuery("select t from ThingWithMultipleUniques t")
			.getResultList();
		assertTrue(thingWithMultipleUniquesList.size() == 1);
	}

}
