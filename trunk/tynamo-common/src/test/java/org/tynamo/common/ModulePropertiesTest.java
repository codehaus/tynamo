package org.tynamo.common;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tynamo.common.test.filtered.Filtered;
import org.tynamo.common.test.nonexistent.NonExistent;
import org.tynamo.common.test.unfiltered.Unfiltered;

public class ModulePropertiesTest {
	private String originalProductionModeValue;
	@BeforeClass
	public void setUpEnvironment(){
		originalProductionModeValue = System.getProperty("tapestry.production-mode");
		System.setProperty("tapestry.production-mode", "true");
		
	}

	@AfterClass
	public void restoreEnvironment(){
		System.setProperty("tapestry.production-mode", originalProductionModeValue);
	}
	
	@Test
	public void testManifestVersion() {
		// jre doesn't read non-jarred manifest.. use testng class for reading version
		System.out.println("version is " + ModuleProperties.getVersion(Test.class));
	}
	
	@Test
	public void testDevelopmentVersion() {
		String original = System.getProperty("tapestry.production-mode");
		System.setProperty("tapestry.production-mode", "false");
		assertTrue(ModuleProperties.getVersion(ModuleProperties.class).contains("development-SNAPSHOT-"));
		System.setProperty("tapestry.production-mode", original);
	}
	
	@Test public void testVersion(){
		assertEquals("1.0", ModuleProperties.getVersion(Filtered.class));
	}
	
	@Test (expectedExceptions = IllegalArgumentException.class)
	public void testVersionWithUnfiltered(){
		ModuleProperties.getVersion(Unfiltered.class);
	}

	@Test (expectedExceptions = IllegalArgumentException.class)
	public void testVersionWithNonexistent(){
		ModuleProperties.getVersion(NonExistent.class);
	}
	
	@Test (expectedExceptions = IllegalArgumentException.class)
	public void testGetModulePropertyNonExistent(){
		ModuleProperties.getPropertyValue(NonExistent.class, "module.buildnumber");
	}

	@Test
	public void testGetModulePropertyFiltered(){
	  assertEquals("123",	ModuleProperties.getPropertyValue(Filtered.class, "module.buildnumber"));
	}
	
}
