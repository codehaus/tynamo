package org.tynamo.seedentity.jpa.services;

public interface SeedEntity {
	// type string, default "" (if unit name is empty us the configured persistence unit if there's only a single one)
	public static final String PERSISTENCEUNIT = "seedentity.persistenceunit";
}
