/*
 * Created on Dec 9, 2004
 *
 * Copyright 2004 Chris Nelson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.tynamo.test.functional;

import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import java.io.IOException;
import static com.gargoylesoftware.htmlunit.WebAssert.*;
import static org.testng.Assert.*;

// FIXME these tests don't work because apply button doesn't work currently
// see goToNew... methods
public class EditCategoryTest extends AbstractContainerTest
{
	private HtmlPage startPage;

	@BeforeMethod
	public void setStartPage() throws Exception {
		startPage = webClient.getPage(BASEURI);
	}
	
	// @Test
	public void testRequiredValidation() throws Exception
	{
		HtmlPage newCategoryPage;
		HtmlForm newCategoryForm = goToNewCategoryForm();
		HtmlSubmitInput saveButton = (HtmlSubmitInput) newCategoryForm
			.getInputByValue("saveAndReturnButton");
		newCategoryPage = (HtmlPage) saveButton.click();
		assertErrorTextPresent(newCategoryPage);
		newCategoryForm = newCategoryPage.getFormByName("form");
		HtmlTextArea textArea = newCategoryForm.getTextAreaByName("Description");
		textArea.setText("a description");
		newCategoryPage = clickButton(newCategoryForm, "save");
		assertErrorTextNotPresent(newCategoryPage);
		assertElementPresent(newCategoryPage, "Id");
	}

	public void testRegexValidation() throws Exception
	{
		HtmlPage catalogListPage = (HtmlPage) startPage.getFirstAnchorByText(
			"List Catalogs").click();
		HtmlPage newCatalogPage = (HtmlPage) catalogListPage
			.getFirstAnchorByText("New Catalog").click();
		HtmlForm form = newCatalogPage.getFormByName("form");
		HtmlInput nameInput = form.<HtmlInput>getInputByName("name");
		nameInput.setValueAttribute("new catalog");
		newCatalogPage = clickButton(newCatalogPage, "save");
		assertErrorTextPresent(newCatalogPage);
		assertTextPresent(newCatalogPage, "NameLabel is Required");
		nameInput.setValueAttribute("newspacecatalog");
		newCatalogPage = clickButton(newCatalogPage, "saveAndReturnButton");
		assertErrorTextNotPresent(newCatalogPage);
	}

	private HtmlForm goToNewCategoryForm() throws Exception
	{
		HtmlPage newCategoryPage = goToNewCategoryPage();

		return (HtmlForm) newCategoryPage.getForms().get(0);
	}

	private HtmlPage goToNewCategoryPage() throws IOException
	{
		HtmlPage catalogListPage = (HtmlPage) startPage.getFirstAnchorByText(
			"List Catalogs").click();
		HtmlPage newCatalogPage = (HtmlPage) catalogListPage
			.getFirstAnchorByText("New Catalog").click();
		HtmlForm form = newCatalogPage.getFormByName("form");
		form.<HtmlInput>getInputByName("name").setValueAttribute("newcatalog");
		newCatalogPage = clickButton(newCatalogPage, "save");

		return clickLink(newCatalogPage,"Add New...");
	}

	public void testOverrideOnAddToCollectionPage() throws Exception
	{

		assertXPathPresent(goToNewCategoryPage(),
			"//label[text() = 'The Description']");
	}

	public void testAddNewDisabled() throws Exception
	{
		HtmlPage listCatalogsPage = clickLink(startPage, "List Catalogs");
		HtmlPage newCatalogPage = clickLink(listCatalogsPage, "New Catalog");
//		HtmlSubmitInput addButton = (HtmlSubmitInput) new HtmlUnitXPath("//input[@type='submit' and @value='Add New...']").selectSingleNode(newCatalogPage);
		HtmlAnchor addLink = null;
		try
		{
			addLink = newCatalogPage.getFirstAnchorByText("Add New...");
		} catch (ElementNotFoundException e)
		{
			assertNotNull(e);  // assertTrue(addButton.isDisabled());
		}
		newCatalogPage.getFormByName("form").getInputByName("name").setValueAttribute("newercatalog");
		newCatalogPage = clickButton(newCatalogPage, "save");
//		addButton = (HtmlSubmitInput) new HtmlUnitXPath("//input[@type='submit' and @value='Add New...']").selectSingleNode(newCatalogPage);
		addLink = newCatalogPage.getFirstAnchorByText("Add New...");
		assertNotNull(addLink); // assertFalse(addButton.isDisabled());
	}

	public void testAddProductToCategory() throws Exception
	{
		webClient.setJavaScriptEnabled(false);
		HtmlForm newCategoryForm = goToNewCategoryForm();
        HtmlTextArea textArea = newCategoryForm.getTextAreaByName("Description");
		textArea.setText("howdya doo");
		HtmlPage categoryPage = clickButton(newCategoryForm, "Apply");
		HtmlPage newProductPage = clickLink(categoryPage, "Add New...");
		HtmlTextInput input = newProductPage.getFormByName("form").getInputByName("name");
		input.setValueAttribute("a new product");

		categoryPage = clickButton(newProductPage, "Ok");
		assertXPathPresent(categoryPage,
			"//td[@class='selected-cell']/select/option['a new product']");
		HtmlPage catalogPage = clickButton(categoryPage, "Ok");
		assertXPathPresent(catalogPage, "//td/a['howdya doo']");
		HtmlPage listPage = clickButton(catalogPage, "Ok");
		assertXPathPresent(listPage, "//td/a['newercatalog']");
	}
}
