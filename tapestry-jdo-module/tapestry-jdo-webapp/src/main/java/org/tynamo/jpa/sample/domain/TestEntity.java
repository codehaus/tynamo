package org.tynamo.jpa.sample.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Piero Sartini
 */
@Entity
public class TestEntity {
	@Id @GeneratedValue
	private long id;
	private String value;

	public TestEntity() {}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
