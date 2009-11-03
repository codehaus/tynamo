package org.tynamo.test.functional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import static org.testng.Assert.*;

public class TreeTest extends AbstractContainerTest
{
	private HtmlPage startPage;

	@BeforeMethod
	public void setStartPage() throws Exception {
		startPage = webClient.getPage(BASEURI);
	}

	@Test
	public void testTree() throws Exception
	{
		HtmlPage listTreeNodesPage = clickLink(startPage, "List Tree Nodes");
		HtmlPage newTreeNodePage = clickLink(listTreeNodesPage, "New Tree Node");
		HtmlForm form = newTreeNodePage.getFormByName("form");
		form.<HtmlInput>getInputByName("name").setValueAttribute("one");
//		newTreeNodePage = clickButton(newTreeNodePage, "save");
//		assertNotNull(newTreeNodePage.getElementById("Identifier") );
		listTreeNodesPage = clickButton(newTreeNodePage, "saveAndReturnButton");
		newTreeNodePage = clickLink(listTreeNodesPage, "New Tree Node");
		form = newTreeNodePage.getFormByName("form");
		form.<HtmlInput>getInputByName("name").setValueAttribute("two");
		listTreeNodesPage = clickButton(newTreeNodePage, "saveAndReturnButton");
		HtmlPage editTreeNodePage = clickLink(listTreeNodesPage, "1");
		form = newTreeNodePage.getFormByName("form");
		form.getSelectByName("select").setSelectedAttribute("1", true);
		// FIXME Save button doesn't work yet
//		editTreeNodePage = clickButton(editTreeNodePage, "save");
//		HtmlOption option = newTreeNodePage.getFormByName("form").getSelectByName("Parent").getOptionByValue("2");
//		assertTrue(option.isSelected(), "2 is selected");
//
//		// now delete one
//		listTreeNodesPage = clickButton(editTreeNodePage, "Delete");
//		assertNull(new HtmlUnitXPath("//td[text() = 'one']").selectSingleNode(listTreeNodesPage));
	}
}
