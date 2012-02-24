package org.tynamo.examples.simple.functional;

import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import static org.testng.Assert.*;


public class EmbeddedTest extends AbstractContainerTest
{
	@Test
	public void addPerson() throws Exception
	{
		HtmlPage newPersonPage = webClient.getPage(BASEURI + "add/person");
		HtmlForm form = newPersonPage.getHtmlElementById("form");
		form.<HtmlInput>getInputByName("firstName").setValueAttribute("John");
		form.<HtmlInput>getInputByName("lastName").setValueAttribute("Doe");
		form.<HtmlInput>getInputByName("city").setValueAttribute("Sunnyville");
		newPersonPage = clickButton(newPersonPage, "save");
		assertEquals("Sunnyville", form.<HtmlInput>getInputByName("city").getAttribute("value"));
		// FIXME currently apply and ok execute the same action
//		listPersonsPage = clickButton(newPersonPage, "saveAndReturn");
//		assertXPathPresent(listPersonsPage, "//td['John doe']");
	}
}
