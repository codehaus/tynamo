package org.tynamo.test.functional;

import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class IntegerFieldTest extends AbstractContainerTest
{
	@Test
	public void testIntegerFields() throws Exception
	{
		HtmlPage newThingPage = webClient.getPage(BASEURI + "add/things"); 
		HtmlForm form = newThingPage.getFormByName("form");
		form.<HtmlInput>getInputByName("id").setValueAttribute("1");
		form.<HtmlInput>getInputByName("number").setValueAttribute("3");
		newThingPage = clickButton(newThingPage, "save");
		
		// FIXME this is failing for several reasons:
		// - Thing doesn't allow to set id but it's not auto-generated
		// - pressing apply is same as ok
		//assertNotNull(getId("Id", newThingPage));
	}
}
