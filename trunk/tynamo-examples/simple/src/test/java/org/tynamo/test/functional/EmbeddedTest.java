package org.tynamo.test.functional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import static org.testng.Assert.*;


public class EmbeddedTest extends AbstractContainerTest
{
	private HtmlPage startPage;

	@BeforeMethod
	public void setStartPage() throws Exception {
		startPage = webClient.getPage(BASEURI);
	}

	@Test
	public void addPerson() throws Exception
	{
		HtmlPage listPersonsPage = clickLink(startPage, "List Persons");
		HtmlPage newPersonPage = clickLink(listPersonsPage, "New Person");
		HtmlForm form = newPersonPage.getFormByName("form");
		form.<HtmlInput>getInputByName("firstName").setValueAttribute("John");
		form.<HtmlInput>getInputByName("lastName").setValueAttribute("Doe");
		form.<HtmlInput>getInputByName("city").setValueAttribute("Sunnyville");
		newPersonPage = clickButton(newPersonPage, "save");
		assertEquals("Sunnyville", form.<HtmlInput>getInputByName("city").getAttribute("value"));
		// FIXME currently apply and ok execute the same action
//		listPersonsPage = clickButton(newPersonPage, "saveAndReturnButton");
//		assertXPathPresent(listPersonsPage, "//td['John doe']");
	}
}
