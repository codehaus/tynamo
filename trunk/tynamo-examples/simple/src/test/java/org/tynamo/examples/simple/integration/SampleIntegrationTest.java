package org.tynamo.examples.simple.integration;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import static org.testng.Assert.assertEquals;

public class SampleIntegrationTest extends AbstractContainerTest
{
	@Test
	public void assertCorrectTitle() throws Exception
	{
		final HtmlPage homePage = webClient.getPage(BASEURI);
		assertEquals("Tynamo!", homePage.getTitleText());
	}
}