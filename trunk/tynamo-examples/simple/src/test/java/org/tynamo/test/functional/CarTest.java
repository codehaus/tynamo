package org.tynamo.test.functional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class CarTest extends AbstractContainerTest
{
	private HtmlPage startPage;

	@BeforeMethod
	public void setStartPage() throws Exception {
		startPage = webClient.getPage(BASEURI);
	}
	
	@Test
	public void testObjectTableOnEditPage() throws Exception
	{
		HtmlPage startPage = webClient.getPage(BASEURI);
		HtmlPage listMakesPage = clickLink(startPage, "List Makes");
		HtmlPage newMakePage = clickLink(listMakesPage, "New Make");
		HtmlForm form = newMakePage.getFormByName("form");
		form.<HtmlInput>getInputByName("name").setValueAttribute("Honda");
		
		listMakesPage = clickButton(newMakePage, "saveAndReturnButton");

		startPage = clickLink(listMakesPage, "Home");

		HtmlPage listModelsPage = clickLink(startPage, "List Models");
		HtmlPage newModelPage = clickLink(listModelsPage, "New Model");
		form = newModelPage.getFormByName("form");
		form.<HtmlInput>getInputByName("name").setValueAttribute("Civic");
		listModelsPage = clickButton(newModelPage, "saveAndReturnButton");

		startPage = clickLink(listModelsPage, "Home");

		HtmlPage listCarsPage = clickLink(startPage, "List Cars");
		HtmlPage newCarPage = clickLink(listCarsPage, "New Car");
		form = newModelPage.getFormByName("form");
		form.<HtmlInput>getInputByName("name").setValueAttribute("Accord");
		// kaosko -2009-11-02: not implemented yet
//		HtmlSelect makeSelect = form.getSelectByName("Make");
//		makeSelect.setSelectedAttribute("1", true);
//		listCarsPage = clickButton(newCarPage, "saveAndReturnButton");
//
//		startPage = clickLink(listCarsPage, "Home");
//
//		listModelsPage = clickLink(startPage, "List Models");
//		HtmlPage model1Page = clickLink(listModelsPage, "1");
//		assertXPathPresent(model1Page, "//td/a[contains(text(),'Accord')]");
	}

	@Test
	public void testCancelAndDefaultCallback() throws Exception {

		HtmlPage listMakesPage = clickLink(startPage, "List Makes");
		HtmlPage newMakePage = clickLink(listMakesPage, "New Make");

		newMakePage = clickLink(newMakePage, "Cancel");

		assertXPathPresent(newMakePage, "//h1[contains(text(),'List')]");
	}

	@Test
	public void testCancelWithSessionCallbackStack() throws Exception
	{
		HtmlPage listMakesPage = clickLink(startPage, "List Makes");
		HtmlPage listCarsPage = clickLink(startPage, "List Cars");

		HtmlPage newMakePage = clickLink(listMakesPage, "New Make"); 
		//this is to add a callback pointing to the New Make page to the callbackStack
		HtmlPage newCarPage = clickLink(listCarsPage, "New Car");

		newMakePage = clickLink(newCarPage, "Cancel");
		// kaosko - 2009-11-02: this won't work currently not so sure this even makes sense in tynamo
//		assertXPathPresent(newMakePage, "//h1[text() = 'Edit Make']");
	}
}
