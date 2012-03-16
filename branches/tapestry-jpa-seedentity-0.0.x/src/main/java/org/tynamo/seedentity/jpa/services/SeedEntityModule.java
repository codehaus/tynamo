package org.tynamo.seedentity.jpa.services;

import java.io.IOException;
import java.util.Properties;

import org.apache.tapestry5.ioc.ServiceBinder;

public class SeedEntityModule {
	private static final String version;
	static {
		Properties moduleProperties = new Properties();
		String aVersion = "unversioned"; 
		try {
			moduleProperties.load(SeedEntityModule.class.getResourceAsStream("module.properties"));
			aVersion = moduleProperties.getProperty("module.version");
		} catch (IOException e) {
			// ignore
		}
		version = aVersion;
	}

	public static void bind(ServiceBinder binder) {
		binder.bind(SeedEntity.class, SeedEntityImpl.class);
	}
}