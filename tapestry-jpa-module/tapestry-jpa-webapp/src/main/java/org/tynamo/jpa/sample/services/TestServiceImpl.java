/*
 * Copyright © Sartini IT Solutions, 2010.
 */

package org.tynamo.jpa.sample.services;

import org.tynamo.jpa.sample.domain.TestEntity;
import org.apache.tapestry5.ioc.annotations.Inject;

import javax.persistence.EntityManager;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: ps
 * Date: 13.01.2010
 * Time: 15:15:30
 * To change this template use File | Settings | File Templates.
 */
public class TestServiceImpl implements TestService {
	@Inject private EntityManager em;
	public void addTestEntity() {
		TestEntity te = new TestEntity();
		te.setValue(new Date().getTime()+"");
		em.persist(te);
	}

	public void removeTestEntity(long id) {
		TestEntity te = em.find(TestEntity.class, id);
		em.remove(te);	
	}
}
