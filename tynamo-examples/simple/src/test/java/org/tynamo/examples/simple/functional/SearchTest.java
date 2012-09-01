package org.tynamo.examples.simple.functional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class SearchTest extends AbstractContainerTest
{
	private HtmlPage startPage;

	@BeforeMethod
	public void setStartPage() throws Exception {
		startPage = webClient.getPage(BASEURI);
	}

	
	@Test
	public void testSearch() throws Exception
	{
		HtmlPage listApplesPage = clickLink(startPage, "List Apples");
		HtmlPage newApplePage = clickLink(listApplesPage, "New Apple");
		HtmlForm form = newApplePage.getHtmlElementById("form");
		form.getInputByName("color").setValueAttribute("Blue");
		form.getSelectByName("origin").setSelectedAttribute("AMERICA", true);

		listApplesPage = clickButton(newApplePage, "saveAndReturn");
		// FIXME Search page not implemented yet
//		HtmlPage searchApplesPage = clickLink(listApplesPage, "Search Apples");
//		newApplePage.getHtmlElementById("form").getInputByName("color").setValueAttribute("Blue");
//		listApplesPage = clickButton(searchApplesPage, "Search");
//		assertXPathPresent(listApplesPage, "//td[text() = 'Blue']");
//		searchApplesPage = clickLink(listApplesPage, "Search Apples");
//		searchApplesPage.getHtmlElementById("searchform").getInputByName("color").setValueAttribute("lu");
//		listApplesPage = clickButton(searchApplesPage, "Search");
//		assertXPathPresent(listApplesPage, "//td[text() = 'Blue']");
	}

	// @Test 
	// FIXME Search not implemented yet
	public void testSearchByEnum() throws Exception
	{
		HtmlPage listApplesPage = clickLink(startPage, "List Apples");
		HtmlPage newApplePage = clickLink(listApplesPage, "New Apple");
		HtmlForm form = newApplePage.getHtmlElementById("form");
		form.getInputByName("color").setValueAttribute("Red");
		form.getSelectByName("origin").setSelectedAttribute("OCEANIA", true);
		listApplesPage = clickButton(newApplePage, "Ok");

		HtmlPage searchApplesPage = clickLink(listApplesPage, "Search Apples");
		searchApplesPage.<HtmlForm>getHtmlElementById("searchform").getSelectByName("origin").setSelectedAttribute("OCEANIA", true);

		listApplesPage = clickButton(searchApplesPage, "Search");
		assertXPathPresent(listApplesPage, "//td[text() = 'Red']");
		assertXPathNotPresent(listApplesPage, "//td[text() = 'Blue']");

		searchApplesPage = clickLink(listApplesPage, "Search Apples");
		searchApplesPage.<HtmlForm>getHtmlElementById("searchform").getSelectByName("origin").setSelectedAttribute("EUROPE", true);

		listApplesPage = clickButton(searchApplesPage, "Search");
		assertXPathNotPresent(listApplesPage, "//td[text() = 'Blue']");
		assertXPathNotPresent(listApplesPage, "//td[text() = 'Red']");
	}
}
