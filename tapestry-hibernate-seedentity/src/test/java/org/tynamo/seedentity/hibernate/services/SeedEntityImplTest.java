package org.tynamo.seedentity.hibernate.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
import org.tynamo.seedentity.hibernate.entities.NaturalThing;
import org.tynamo.seedentity.hibernate.entities.Thing;

public class SeedEntityImplTest {
	// @Inject
	// private SeedEntity seedEntity;
	private Session session;
	private SessionFactory sessionFactory;

	@BeforeClass
	public void openSession() {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration.addAnnotatedClass(Thing.class);
		configuration.addAnnotatedClass(NaturalThing.class);
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

		SeedEntity service = new SeedEntityImpl(LoggerFactory.getLogger(SeedEntity.class), sessionSource, sessionManager, entities);
		assertTrue(session.createCriteria(Thing.class).list().size() > 0);
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
		assertTrue(session.createCriteria(NaturalThing.class).list().size() == 1);
	}

}
