package org.tynamo.examples.simple.functional;

import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import static org.testng.Assert.*;

public class TextAreaTest extends AbstractContainerTest
{
	@Test
	public void testTextArea() throws Exception
	{
		HtmlPage newThingPage = webClient.getPage(BASEURI + "add/thing");
		assertNotNull(newThingPage.getFormByName("form").getTextAreaByName("text") );
	}
}
