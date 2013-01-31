package org.tynamo.seedentity.hibernate.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("child")
public class ChildUniqueEntity extends BaseUniqueEntity {

	public ChildUniqueEntity() {
		super();
	}

	public ChildUniqueEntity(Integer id, String code, String name, String description) {
		super(id, code, name, description);
	}
}
