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

package org.tynamo.jpa.example.app0.pages;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import org.tynamo.jpa.annotations.CommitAfter;
import org.tynamo.jpa.example.app0.entities.User;

public class EncodeEntities
{
	@Inject
	private EntityManager entityManager;

	@SuppressWarnings("unused")
	@Property
	private User user;

	@CommitAfter
	void onCreate()
	{
		User user = new User();
		user.setFirstName("name");

		entityManager.persist(user);
	}

	@SuppressWarnings("unchecked")
	User onPassivate()
	{
		List<User> users = entityManager.createQuery("selec u from User u").getResultList();
		if (users.isEmpty())
			return null;

		return users.get(0);
	}

	void onActivate(User user)
	{
		this.user = user;
	}
}
