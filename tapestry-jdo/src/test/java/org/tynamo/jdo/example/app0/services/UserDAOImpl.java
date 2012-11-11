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

package org.tynamo.jdo.example.app0.services;

import java.util.List;
import javax.jdo.PersistenceManager;


import org.tynamo.jdo.example.app0.entities.User;

public class UserDAOImpl implements UserDAO
{
	private final PersistenceManager pm;

	public UserDAOImpl(PersistenceManager value)
	{
		this.pm = value;
	}

	public void add(User user)
	{
		pm.makePersistent(user);
	}

	@SuppressWarnings( { "unchecked" })
	public List<User> findAll()
	{
		return (List<User>) pm.newQuery(User.class).execute();
	}

	public void delete(User... users)
	{
		for (User user : users)
			pm.deletePersistent(user);
	}

	public void deleteAll()
	{
		for (User u : findAll())
		{
			pm.deletePersistent(u);
		}
	}
}
