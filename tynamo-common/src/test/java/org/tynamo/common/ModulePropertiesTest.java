package org.tynamo.common;

import org.testng.annotations.Test;

public class ModulePropertiesTest {
	@Test
	public void testManifestVersion() {
		String original = System.getProperty("tapestry.production-mode");
		System.setProperty("tapestry.production-mode", "false");
		System.out.println("version is " + ModuleProperties.getVersion(ModuleProperties.class));
		System.setProperty("tapestry.production-mode", original);
	}

}
