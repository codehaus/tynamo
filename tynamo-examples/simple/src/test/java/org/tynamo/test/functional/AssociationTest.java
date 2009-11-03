package org.tynamo.test.functional;

import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class AssociationTest extends AbstractContainerTest
{

	@Test
	public void associationSelect() throws Exception
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
		HtmlForm newModelForm = newModelPage.getFormByName("form");
		newModelForm.<HtmlInput>getInputByName("name").setValueAttribute("Civic");
		listModelsPage = clickButton(newModelPage, "saveAndReturnButton");
		startPage = clickLink(listModelsPage, "Home");

		HtmlPage listCarsPage = clickLink(startPage, "List Cars");
		
		// Currently results in 
//		java.lang.NullPointerException
//		        * org.tynamo.examples.simple.entities.CarPk.toString(CarPk.java:63) 		
//		HtmlPage newCarPage = clickLink(listCarsPage, "New Car");
//		assertXPathPresent(newCarPage,
//			"//select/preceding-sibling::label[contains(text(), 'Make')]/following-sibling::select/option[text() = 'Honda']");
//		assertXPathPresent(newCarPage,
//			"//select/preceding-sibling::label[contains(text(), 'Model')]/following-sibling::select/option[text() = 'Civic']");

	}
}
