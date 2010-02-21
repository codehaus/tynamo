package org.tynamo.seedentity.hibernate.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tynamo.seedentity.SeedEntityIdentifier;
import org.tynamo.seedentity.hibernate.entities.NaturalThing;
import org.tynamo.seedentity.hibernate.entities.NonUniqueThing;
import org.tynamo.seedentity.hibernate.entities.Thing;

public class SeedEntityImplTest {
	private Session session;
	private SessionFactory sessionFactory;

	@BeforeClass
	public void openSession() {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration.addAnnotatedClass(Thing.class);
		configuration.addAnnotatedClass(NaturalThing.class);
		configuration.addAnnotatedClass(NonUniqueThing.class);
		configuration.configure("/hibernate-test.cfg.xml");
		sessionFactory = configuration.buildSessionFactory();
		session = sessionFactory.openSession();
	}

	@Test
	public void seedThingEntities() {
		Thing thing = new Thing();
		List<Object> entities = new ArrayList<Object>();
		entities.add(thing);
		HibernateSessionManager sessionManager = mock(HibernateSessionManager.class);
		when(sessionManager.getSession()).thenReturn(session);
		HibernateSessionSource sessionSource = mock(HibernateSessionSource.class);
		// Returning null is ok, this test doesn't use sessionFactory (nor does it commit()
		when(sessionSource.getSessionFactory()).thenReturn(null);

		new SeedEntityImpl(LoggerFactory.getLogger(SeedEntity.class), sessionSource, sessionManager, entities);
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
		HibernateSessionManager sessionManager = mock(HibernateSessionManager.class);
		when(sessionManager.getSession()).thenReturn(session);
		HibernateSessionSource sessionSource = mock(HibernateSessionSource.class);
		when(sessionSource.getSessionFactory()).thenReturn(sessionFactory);

		new SeedEntityImpl(LoggerFactory.getLogger(SeedEntity.class), sessionSource, sessionManager, entities);
		assertEquals(session.createCriteria(NonUniqueThing.class).list().size(), 1);
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
		HibernateSessionManager sessionManager = mock(HibernateSessionManager.class);
		when(sessionManager.getSession()).thenReturn(session);
		HibernateSessionSource sessionSource = mock(HibernateSessionSource.class);
		when(sessionSource.getSessionFactory()).thenReturn(sessionFactory);

		SeedEntity service = new SeedEntityImpl(LoggerFactory.getLogger(SeedEntity.class), sessionSource, sessionManager, entities);
		assertEquals(session.createCriteria(NaturalThing.class).list().size(), 1);
	}

}
