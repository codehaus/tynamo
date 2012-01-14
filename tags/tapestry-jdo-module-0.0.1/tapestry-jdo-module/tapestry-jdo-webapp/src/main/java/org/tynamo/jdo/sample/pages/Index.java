package org.tynamo.jdo.sample.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import java.util.List;
import javax.jdo.PersistenceManager;
import org.tynamo.jdo.annotations.CommitAfter;
import org.tynamo.jdo.sample.domain.TestEntity;
import org.tynamo.jdo.sample.services.TestService;

/** Start page of application sample. */
public class Index {

	@Inject private PersistenceManager pm;
	@Inject private TestService testService;
	@Property private TestEntity testEntity;

	public Date getCurrentTime() {
		return new Date();
	}

	@CommitAfter
	public void onActionFromAddEntity() {
		TestEntity te = new TestEntity();
		te.setValue(new Date().getTime()+"");
		pm.makePersistent(te);
	}

	public void onActionFromDelEntity(TestEntity testEntity) {
		testService.removeTestEntity(testEntity.getId());
	}

	public List<TestEntity> getEntities() {
		return (List<TestEntity>) pm.newQuery(TestEntity.class).execute();
	}

	public int getEntityCount() {
		return ((List)pm.newQuery(TestEntity.class).execute()).size();
	}
}
