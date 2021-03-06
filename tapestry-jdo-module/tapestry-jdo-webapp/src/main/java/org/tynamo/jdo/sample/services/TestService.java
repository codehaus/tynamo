/*
 * Copyright © Sartini IT Solutions, 2010.
 */

package org.tynamo.jdo.sample.services;

import org.tynamo.jdo.annotations.CommitAfter;

/**
 * Created by IntelliJ IDEA.
 * User: ps
 * Date: 13.01.2010
 * Time: 15:14:54
 * To change this template use File | Settings | File Templates.
 */
public interface TestService {
	@CommitAfter void addTestEntity();
	@CommitAfter void removeTestEntity(long id);
}
