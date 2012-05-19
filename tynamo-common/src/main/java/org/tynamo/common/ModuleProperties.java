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
			if (aClass.getResource("").toString().startsWith("file:") && "false".equalsIgnoreCase(System.getProperty("tapestry.production-mode"))) version = "development-SNAPSHOT"; 
			if (version == null) throw new IllegalArgumentException("Neither version in properties file '" + expectedPropertyPath + "' nor '" + aClass.getPackage().getName() + "' package-specific Implementation-Version in META-INF/manifest.mf was found");
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
	
	public static String getModuleProperty(Class<?> aClass, String propertyName) {
		String expectedPropertyPath = aClass.getPackage().getName() + "/" + PROPERTYFILE;
		Properties moduleProperties = new Properties();
		String propertyValue = null;
		InputStream inputStream = aClass.getResourceAsStream("module.properties");
		if (inputStream == null) return null;
		else try {
			moduleProperties.load(inputStream);
			propertyValue = moduleProperties.getProperty("module.version");
			if (propertyValue == null) throw new IllegalArgumentException(VERSION + " was not found from " + expectedPropertyPath);
			if (propertyValue.startsWith("${")) throw new IllegalArgumentException(VERSION + " is not filtered in resource " + expectedPropertyPath);
		} catch (IOException e) {
			throw new IllegalArgumentException("No property file resource found from " + expectedPropertyPath);
		}
		return propertyValue;
		
	}
	

}
