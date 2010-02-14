package org.tynamo.test.functional;

import com.gargoylesoftware.htmlunit.html.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import java.io.IOException;
import java.net.MalformedURLException;

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

	@Test
	public void testListOnlyOrphanInstances() throws Exception {

		HtmlPage newMakePage = webClient.getPage(BASEURI +"add/make");
		HtmlForm form = newMakePage.getFormByName("form");
		form.<HtmlInput>getInputByName("name").setValueAttribute("Honda");
		clickButton(newMakePage, "saveAndReturnButton");

		newMakePage = webClient.getPage(BASEURI +"add/make");
		form = newMakePage.getFormByName("form");
		form.<HtmlInput>getInputByName("name").setValueAttribute("Toyota");
		clickButton(newMakePage, "saveAndReturnButton");

		HtmlPage newModelPage = webClient.getPage(BASEURI +"add/model");
		HtmlForm newModelForm = newModelPage.getFormByName("form");
		newModelForm.<HtmlInput>getInputByName("name").setValueAttribute("Prius");
		clickButton(newModelPage, "saveAndReturnButton");

		newModelPage = webClient.getPage(BASEURI +"add/model");
		newModelForm = newModelPage.getFormByName("form");
		newModelForm.<HtmlInput>getInputByName("name").setValueAttribute("Sedan");
		clickButton(newModelPage, "saveAndReturnButton");

		HtmlPage editMakePage = webClient.getPage(BASEURI +"edit/make/1");

		assertXPathPresent(editMakePage, "//input[@value='Honda']");
		assertXPathPresent(editMakePage, "//select[@id='palette_set-avail']");
		assertXPathNotPresent(editMakePage, "//select[@id='palette_set-avail'][not(node())]");
		assertXPathPresent(editMakePage, "//select[@id='palette_set-avail']/option[text()='Prius']");
		assertXPathPresent(editMakePage, "//select[@id='palette_set-avail']/option[text()='Sedan']");

		HtmlPage editModel = webClient.getPage(BASEURI + "edit/model/1");
		form = editModel.getFormByName("form");
		form.getSelectByName("select").getOptionByValue("1").setSelected(true);
		clickButton(editModel, "saveAndReturnButton");

		editMakePage = webClient.getPage(BASEURI +"edit/make/1");
		assertXPathNotPresent(editMakePage, "//select[@id='palette_set-avail'][not(node())]");
		assertXPathNotPresent(editMakePage, "//select[@id='palette_set'][not(node())]");

		editMakePage = webClient.getPage(BASEURI +"edit/make/2");

//		assertXPathPresent(editMakePage, "//input[@value='Toyota']");
		assertXPathPresent(editMakePage, "//select[@id='palette_set-avail']");
		assertXPathNotPresent(editMakePage, "//select[@id='palette_set-avail'][not(node())]");
		assertXPathPresent(editMakePage, "//select[@id='palette_set'][not(node())]");
		assertXPathNotPresent(editMakePage, "//select[@id='palette_set-avail']/option[text()='Prius']");
		assertXPathPresent(editMakePage, "//select[@id='palette_set-avail']/option[text()='Sedan']");

	}
}
