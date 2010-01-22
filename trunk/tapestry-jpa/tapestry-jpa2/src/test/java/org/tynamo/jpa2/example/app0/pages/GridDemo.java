// Copyright 2008 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.tynamo.jpa2.example.app0.pages;

import javax.persistence.EntityManager;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;

import org.tynamo.jpa2.annotations.CommitAfter;
import org.tynamo.jpa2.example.app0.entities.User;
import org.tynamo.jpa2.example.app0.services.UserDAO;
import org.tynamo.jpa2.internal.JPAGridDataSource;

public class GridDemo
{
	@Inject
	private EntityManager entityManager;

	@Inject
	private UserDAO userDAO;

	public GridDataSource getSource()
	{
		return new JPAGridDataSource(entityManager, User.class);
	}

	@CommitAfter
	void onActionFromSetup()
	{
		userDAO.deleteAll();

		for (int i = 1; i <= 20; i++)
		{
			User user = new User();

			String suffix = String.valueOf(i);

			user.setFirstName("Joe_" + suffix);
			user.setLastName("User");
			user.setEncodedPassword("####");
			user.setEmail("joe" + suffix + "@null.org");

			entityManager.persist(user);
		}

	}
}
