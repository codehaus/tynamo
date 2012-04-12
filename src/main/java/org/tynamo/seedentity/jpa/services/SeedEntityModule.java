package org.tynamo.seedentity.jpa.services;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.tynamo.common.ModuleProperties;

public class SeedEntityModule {
	private static final String version = ModuleProperties.getVersion(SeedEntityModule.class);
	
	public static void bind(ServiceBinder binder) {
		binder.bind(SeedEntity.class, SeedEntityImpl.class);
	}
	
	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(SeedEntity.PERSISTENCEUNIT, "");
	}
}