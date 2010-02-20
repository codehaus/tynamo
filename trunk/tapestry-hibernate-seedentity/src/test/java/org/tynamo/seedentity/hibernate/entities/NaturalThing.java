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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.NaturalId;

@Entity
public class NaturalThing {
	private Integer id;

	private String name;

	private String text;

	private Integer number;

	private Integer number2;

	private boolean flag;

	@Id
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

	/**
	 * @return Returns the name.
	 */
	@NaturalId
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *          The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the on.
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * @param on
	 *          The on to set.
	 */
	public void setFlag(boolean on) {
		this.flag = on;
	}

	@Column(length = 300)
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		NaturalThing thing = (NaturalThing) o;

		return getId() != null ? getId().equals(thing.getId()) : thing.getId() == null;

	}

	@Override
	public int hashCode() {
		return (getId() != null ? getId().hashCode() : 0);
	}

	public String toString() {
		return getName();
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getNumber2() {
		return number2;
	}

	public void setNumber2(Integer number2) {
		this.number2 = number2;
	}

	public void weirdOperation() {
		try {
			setNumber2(getNumber() * getNumber2());
		} catch (RuntimeException e) {
			setNumber2(0);
		}
	}

}
