package org.tynamo.seedentity.hibernate.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tynamo.seedentity.SeedEntityIdentifier;
import org.tynamo.seedentity.SeedEntityUpdater;
import org.tynamo.seedentity.hibernate.entities.ActionItem;
import org.tynamo.seedentity.hibernate.entities.BaseUniqueEntity;
import org.tynamo.seedentity.hibernate.entities.ChildUniqueEntity;
import org.tynamo.seedentity.hibernate.entities.NaturalThing;
import org.tynamo.seedentity.hibernate.entities.NonUniqueThing;
import org.tynamo.seedentity.hibernate.entities.Thing;
import org.tynamo.seedentity.hibernate.entities.TotallyNonUniqueThing;
import org.tynamo.seedentity.hibernate.entities.Worker;

public class SeedEntityImplTest {
	private Session session;
	private SessionFactory sessionFactory;
	private SeedEntityImpl seedEntityImpl;

	@BeforeClass
	public void buildSessionFactory() {
		Configuration configuration = new Configuration();
		configuration.addAnnotatedClass(Thing.class);
		configuration.addAnnotatedClass(NaturalThing.class);
		configuration.addAnnotatedClass(NonUniqueThing.class);
		configuration.addAnnotatedClass(ActionItem.class);
		configuration.addAnnotatedClass(Worker.class);
		configuration.addAnnotatedClass(TotallyNonUniqueThing.class);
		configuration.addAnnotatedClass(BaseUniqueEntity.class);
		configuration.addAnnotatedClass(ChildUniqueEntity.class);
		configuration.configure("/hibernate-test.cfg.xml");
		sessionFactory = configuration.buildSessionFactory();
	}

	@BeforeMethod
	public void openSession() {
		session = sessionFactory.openSession();
		HibernateSessionSource sessionSource = mock(HibernateSessionSource.class);
		when(sessionSource.create()).thenReturn(session);
		when(sessionSource.getSessionFactory()).thenReturn(sessionFactory);
		seedEntityImpl = new SeedEntityImpl(LoggerFactory.getLogger(SeedEntity.class), sessionSource, null);
	}

	@Test
	public void seedThingEntities() {
		Thing thing = new Thing();
		List<Object> entities = new ArrayList<Object>();
		entities.add(thing);
		mock(HibernateSessionSource.class);
		seedEntityImpl.seed(session, entities);
		assertTrue(session.createCriteria(Thing.class).list().size() > 0);
	}

	@Test
	public void useSeedEntityIdentifierToSkipReseedingNonUniqueThing() {
		NonUniqueThing thing = new NonUniqueThing();
		thing.setText("individualize");
		Transaction tx = session.beginTransaction();
		session.save(thing);
		tx.commit();
		List<Object> entities = new ArrayList<Object>();
		// Make the test fail by commenting this line out
		entities.add(new SeedEntityIdentifier(NonUniqueThing.class, "text"));
		thing = new NonUniqueThing();
		thing.setText("individualize");
		entities.add(thing);
		seedEntityImpl.seed(session, entities);
		assertEquals(session.createCriteria(NonUniqueThing.class).list().size(), 1);
	}
	
	@Test()
	public void alwaysSeedNonUniqueThings() {
		List<Object> entities = new ArrayList<Object>();
		entities.add(new TotallyNonUniqueThing("one"));
		entities.add(new TotallyNonUniqueThing("two"));
		seedEntityImpl.seed(session, entities);
		assertEquals(session.createCriteria(TotallyNonUniqueThing.class).list().size(), 2);
	}
	
	@Test
	public void seedWorkersAndUpdateWithPartialCommits() {
		List<Object> entities = new ArrayList<Object>();

		Worker worker = new Worker();
		worker.setName("hard worker");
		entities.add(worker);
		ActionItem finishedItem = new ActionItem();
		finishedItem.setText("done work");
		entities.add(finishedItem);
		ActionItem unfinishedItem = new ActionItem();
		unfinishedItem.setText("unfinished work");
		entities.add(unfinishedItem);
		Worker updatedWorker = new Worker();
		updatedWorker.setName("hard worker");
		updatedWorker.getUnfinishedActionItems().add(unfinishedItem);
		updatedWorker.getFinishedActionItems().add(finishedItem);
		entities.add(new SeedEntityUpdater(worker, updatedWorker));

		seedEntityImpl.seed(session, entities);
		updatedWorker = (Worker) session.createCriteria(Worker.class).uniqueResult();
		assertEquals(updatedWorker.getUnfinishedActionItems().size(), 1);
		assertEquals(updatedWorker.getFinishedActionItems().size(), 1);
	}

	@Test
	public void seedNaturalThingEntities() {
		NaturalThing thing = new NaturalThing();
		thing.setId(0);
		thing.setName("existing");
		Transaction tx = session.beginTransaction();
		session.save(thing);
		tx.commit();
		List<Object> entities = new ArrayList<Object>();
		entities.add(thing);
		seedEntityImpl.seed(session, entities);
		assertEquals(session.createCriteria(NaturalThing.class).list().size(), 1);
	}

	@Test
	public void seedInheritanceMappingChildEntitiesWithTwoUniqueConstraintOnBaseClass() {
		List<Object> entities = new ArrayList<Object>();
		entities.add(new ChildUniqueEntity(1, "child", "First Child", "Max"));
		entities.add(new ChildUniqueEntity(2, "child", "First Child", "Mary"));
		entities.add(new ChildUniqueEntity(3, "child", "Second Child", "Max"));
		seedEntityImpl.seed(session, entities);
		assertEquals(session.createCriteria(ChildUniqueEntity.class).list().size(), 1);
	}
}
