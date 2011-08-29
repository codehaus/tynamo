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

package org.tynamo.jdo.example.app0.pages;

import java.util.List;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;


import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import org.tynamo.jdo.annotations.CommitAfter;
import org.tynamo.jdo.example.app0.entities.User;
import org.tynamo.jdo.example.app0.services.UserDAO;


@SuppressWarnings("unused")
public class CachedForm
{
	@Property
	private String name;

	@Property
	private User user;

	@Property
	private int index;

	@Inject
	private PersistenceManager pm;

	@Inject
	private UserDAO userDAO;

	@CommitAfter
	void onSuccess()
	{
		User user = new User();
		user.setFirstName(name);

		pm.makePersistent(user);
	}

	@SuppressWarnings("unchecked")
	@Cached
	public List<User> getUsers()
	{
		Query q = pm.newQuery(User.class);
		return (List<User>)q.execute();
	}

	void onActionFromSetup()
	{
		userDAO.deleteAll();
	}

}
