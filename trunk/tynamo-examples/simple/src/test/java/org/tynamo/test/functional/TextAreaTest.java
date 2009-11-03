package org.tynamo.test.functional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import static org.testng.Assert.*;

public class TextAreaTest extends AbstractContainerTest
{
	private HtmlPage startPage;

	@BeforeMethod
	public void setStartPage() throws Exception {
		startPage = webClient.getPage(BASEURI);
	}

	@Test
	public void testTextArea() throws Exception
	{
		HtmlPage listThingsPage = clickLink(startPage, "List Things");
		HtmlPage newThingPage = clickLink(listThingsPage, "New Thing");
		assertNotNull(newThingPage.getFormByName("form").getTextAreaByName("text") );
	}
}
