package org.tynamo.seedentity.jpa;

public final class SeedEntityIdentifier {
	private Object entity;
	private String uniquelyIdentifyingProperty;

	public SeedEntityIdentifier(Object entity, String uniquelyIdentifyingProperty) {
		this.entity = entity;
		this.uniquelyIdentifyingProperty = uniquelyIdentifyingProperty;
	}

	public Object getEntity() {
		return entity;
	}

	public String getUniquelyIdentifyingProperty() {
		return uniquelyIdentifyingProperty;
	}

}
