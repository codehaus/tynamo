package org.tynamo.seedentity.services;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tynamo.seedentity.entities.Thing;

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
		SeedEntity service = new SeedEntityImpl(LoggerFactory.getLogger(SeedEntity.class), session, entities);
		assertTrue(session.createCriteria(Thing.class).list().size() > 0);
	}
}
