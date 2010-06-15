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

package org.tynamo.jpa.example.app0.services;

import java.util.List;

import javax.persistence.EntityManager;

import org.tynamo.jpa.example.app0.entities.User;

public class UserDAOImpl implements UserDAO
{
	private final EntityManager entityManager;

	public UserDAOImpl(EntityManager value)
	{
		this.entityManager = value;
	}

	public void add(User user)
	{
		entityManager.persist(user);
	}

	@SuppressWarnings( { "unchecked" })
	public List<User> findAll()
	{
		return entityManager.createQuery("from User").getResultList();
	}

	public void delete(User... users)
	{
		for (User user : users)
			entityManager.remove(user);
	}

	public void deleteAll()
	{
		for (User u : findAll())
		{
			entityManager.remove(u);
		}
	}
}
