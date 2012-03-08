package org.tynamo.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ModuleProperties {
	public static final String PROPERTYFILE = "module.properties";
	public static final String VERSION = "module.version";

	public static String getVersion(Class<?> aClass) {
		String expectedPropertyPath = aClass.getPackage().getName() + "/" + PROPERTYFILE;
		Properties moduleProperties = new Properties();
		String version = null;
		InputStream inputStream = aClass.getResourceAsStream("module.properties");
		if (inputStream == null) {
			version = aClass.getPackage().getImplementationVersion();
			System.out.println("resource path is " + aClass.getResource(""));
			if (aClass.getResource("").toString().startsWith("file:") && "false".equalsIgnoreCase(System.getProperty("tapestry.production-mode"))) version = "development-SNAPSHOT"; 
			if (version == null) throw new IllegalArgumentException("Neither properties file '" + expectedPropertyPath + "' nor META-INF/manifest.mf was found");
		}
		else try {
			moduleProperties.load(inputStream);
			version = moduleProperties.getProperty("module.version");
			if (version == null) throw new IllegalArgumentException(VERSION + " was not found from " + expectedPropertyPath);
			if (version.startsWith("${")) throw new IllegalArgumentException(VERSION + " is not filtered in resource " + expectedPropertyPath);
		} catch (IOException e) {
			throw new IllegalArgumentException("No property file resource found from " + expectedPropertyPath);
		}
		if (version.endsWith("SNAPSHOT")) version += "-" + System.currentTimeMillis();
		return version;
	}

}
