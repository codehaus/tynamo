package org.tynamo.security.jpa.testapp.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Unsecured {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	// @ManyToOne
	// private TestOwnerEntity owner;
	//
	// public TestOwnerEntity getOwner() {
	// return owner;
	// }
	//
	// public void setOwner(TestOwnerEntity owner) {
	// this.owner = owner;
	// }

	public Long getId() {
		return id;
	}

}
