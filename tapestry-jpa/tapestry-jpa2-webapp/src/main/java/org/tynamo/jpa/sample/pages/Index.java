package org.tynamo.jpa.sample.pages;

import org.tynamo.jpa.sample.domain.TestEntity;
import org.tynamo.jpa.sample.services.TestService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.jpa2.annotations.CommitAfter;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

/** Start page of application sample. */
public class Index {

	@Inject private EntityManager em;
	@Inject private TestService testService;
	@Property private TestEntity testEntity;

	public Date getCurrentTime() {
		return new Date();
	}

	@CommitAfter
	public void onActionFromAddEntity() {
		TestEntity te = new TestEntity();
		te.setValue(new Date().getTime()+"");
		em.persist(te);
	}

	public void onActionFromDelEntity(long id) {
		testService.removeTestEntity(id);
	}

	public List<TestEntity> getEntities() {
		return em.createQuery("select e from TestEntity e").getResultList();
	}

	public int getEntityCount() {
		return em.createQuery("select e from TestEntity e").getResultList().size();
	}
}
