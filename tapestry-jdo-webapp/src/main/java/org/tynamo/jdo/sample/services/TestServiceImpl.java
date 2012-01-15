/*
 * Copyright © Sartini IT Solutions, 2010.
 */

package org.tynamo.jdo.sample.services;

import org.tynamo.jdo.sample.domain.TestEntity;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import javax.jdo.PersistenceManager;

/**
 * Created by IntelliJ IDEA.
 * User: ps
 * Date: 13.01.2010
 * Time: 15:15:30
 * To change this template use File | Settings | File Templates.
 */
public class TestServiceImpl implements TestService {
	@Inject private PersistenceManager pm;
	public void addTestEntity() {
		TestEntity te = new TestEntity();
		te.setValue(new Date().getTime()+"");
		pm.makePersistent(te);
	}

	public void removeTestEntity(long id) {
		TestEntity te = pm.getObjectById(TestEntity.class, id);
		pm.deletePersistent(te);	
	}
}
