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
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tynamo.seedentity.hibernate.entities.Thing;
import org.tynamo.seedentity.hibernate.services.SeedEntity;
import org.tynamo.seedentity.hibernate.services.SeedEntityImpl;

public class SeedEntityImplTest {
	// @Inject
	// private SeedEntity seedEntity;
	private Session session;

	@BeforeClass
	public void openSession() {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration.addAnnotatedClass(Thing.class);
		configuration.configure("/hibernate-test.cfg.xml");
		SessionFactory sessionFactory = configuration.buildSessionFactory();
		session = sessionFactory.openSession();
	}

	@Test
	public void seedEntities() {
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
}
