/*
 * Copyright 2004 Chris Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.tynamo.seedentity.hibernate.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.NaturalId;

@Entity
public class Worker {
	private Integer id;

	private String name;

	private List<ActionItem> unfinishedActionItems = new ArrayList<ActionItem>();
	private List<ActionItem> finishedActionItems = new ArrayList<ActionItem>();

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *          The id to set.
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	@NaturalId
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Worker thing = (Worker) o;

		return getId() != null ? getId().equals(thing.getId()) : thing.getId() == null;

	}

	@Override
	public int hashCode() {
		return (getId() != null ? getId().hashCode() : 0);
	}

	public String toString() {
		return getName();
	}

	public void setUnfinishedActionItems(List<ActionItem> unfinishedActionItems) {
		this.unfinishedActionItems = unfinishedActionItems;
	}

	// If worker would be holding just one list, the simplest way to model the relationship is make it bi-directional
	// @OneToMany(mappedBy="worker")
	// However, if worker needs to maintain multiple lists, they need to be unidirectional
	@OneToMany
	@OrderBy("created")
	@JoinTable(name = "Worker_UnfinishedActionItem")
	public List<ActionItem> getUnfinishedActionItems() {
		return unfinishedActionItems;
	}

	public void setFinishedActionItems(List<ActionItem> finishedActionItems) {
		this.finishedActionItems = finishedActionItems;
	}

	@OneToMany
	@OrderBy("created")
	@JoinTable(name = "Worker_FinishedActionItem")
	public List<ActionItem> getFinishedActionItems() {
		return finishedActionItems;
	}

}
