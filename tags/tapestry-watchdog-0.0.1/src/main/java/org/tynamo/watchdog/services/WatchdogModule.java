package org.tynamo.watchdog.services;

import java.io.IOException;
import java.util.Properties;

import org.apache.tapestry5.ioc.ServiceBinder;

public class WatchdogModule {
	private static final String version;
	public static final String javamailSpec;
	public static final String javamailProvider;
	static {
		Properties moduleProperties = new Properties();
		String aVersion = "unversioned";
		String aJavamailSpec = "unknown";
		String aJavamailProvider = "unknown";
		try {
			moduleProperties.load(WatchdogModule.class.getResourceAsStream("module.properties"));
			aVersion = moduleProperties.getProperty("module.version");
			aJavamailSpec = moduleProperties.getProperty("javamail.spec");
			aJavamailProvider = moduleProperties.getProperty("javamail.provider");
		} catch (IOException e) {
			// ignore
		}
		version = aVersion;
		javamailSpec = aJavamailSpec;
		javamailProvider = aJavamailProvider;
	}

	public static void bind(ServiceBinder binder) {
		binder.bind(WatchdogService.class, WatchdogServiceImpl.class);
	}
	// FIXME should you contribute factory defaults for smtp.host and smtp.port?

}