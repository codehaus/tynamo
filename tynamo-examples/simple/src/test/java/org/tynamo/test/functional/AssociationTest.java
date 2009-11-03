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
		HtmlPage newMakePage = webClient.getPage(BASEURI +"add/make");
		HtmlForm form = newMakePage.getFormByName("form");
		form.<HtmlInput>getInputByName("name").setValueAttribute("Honda");
		HtmlPage listMakesPage = clickButton(newMakePage, "saveAndReturnButton");

		HtmlPage newModelPage = webClient.getPage(BASEURI +"add/model");
		HtmlForm newModelForm = newModelPage.getFormByName("form");
		newModelForm.<HtmlInput>getInputByName("name").setValueAttribute("Civic");

		HtmlPage listCarsPage = webClient.getPage(BASEURI +"list/car");
		
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
