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
import com.gargoylesoftware.htmlunit.html.xpath.HtmlUnitXPath;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import org.jaxen.JaxenException;

import java.io.IOException;

/**
 * @author fus8882
 *         <p/>
 *         TODO To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Style - Code Templates
 */
public class EditCategoryTest extends FunctionalTest
{
	public void testRequiredValidation() throws Exception
	{
		HtmlPage newCategoryPage;
		HtmlForm newCategoryForm = goToNewCategoryForm();
		HtmlSubmitInput saveButton = (HtmlSubmitInput) newCategoryForm
			.getInputByValue("Apply");
		newCategoryPage = (HtmlPage) saveButton.click();
		HtmlDivision errorDiv = getErrorDiv(newCategoryPage);
		assertNotNull("found the error div", errorDiv);
		newCategoryForm = getFirstForm(newCategoryPage);
		HtmlTextArea textArea = getTextAreaByName(newCategoryPage,"Description");
		textArea.setText("a description");
		newCategoryPage = clickButton(newCategoryForm, "Apply");
		assertNull("error div", getErrorDiv(newCategoryPage));
		assertTrue("got an id", getId("Id", newCategoryPage).length() > 0);
	}

	public void testRegexValidation() throws Exception
	{
		HtmlPage catalogListPage = (HtmlPage) startPage.getFirstAnchorByText(
			"List Catalogs").click();
		HtmlPage newCatalogPage = (HtmlPage) catalogListPage
			.getFirstAnchorByText("New Catalog").click();
		getInputByName(newCatalogPage, "Name").setValueAttribute("new catalog");
		newCatalogPage = clickButton(newCatalogPage, "Apply");
		assertNotNull("error div", getErrorDiv(newCatalogPage));
		HtmlInput nameInput = getInputInErrorByName(newCatalogPage, "Name");
		assertNotNull("name input label", nameInput);
		nameInput.setValueAttribute("newspacecatalog");
		newCatalogPage = clickButton(newCatalogPage, "Apply");
		assertNull("no error div", getErrorDiv(newCatalogPage));
	}

	/**
	 * It matches:
	 * <li><font color="red"><label for="stringField" class="desc">Name</label></font>
	 *
	 */
	protected HtmlInput getInputInErrorByName(HtmlPage page, String name) throws JaxenException
	{
		return (HtmlInput)
			new HtmlUnitXPath("//input/preceding-sibling::font[label[contains(text(), '" + name + "')]]/following-sibling::input").selectSingleNode(page);
	}

	private HtmlForm goToNewCategoryForm() throws Exception
	{
		HtmlPage newCategoryPage = goToNewCategoryPage();

		return (HtmlForm) newCategoryPage.getForms().get(0);
	}

	private HtmlPage goToNewCategoryPage() throws IOException, JaxenException
	{
		HtmlPage catalogListPage = (HtmlPage) startPage.getFirstAnchorByText(
			"List Catalogs").click();
		HtmlPage newCatalogPage = (HtmlPage) catalogListPage
			.getFirstAnchorByText("New Catalog").click();
		getInputByName(newCatalogPage, "Name").setValueAttribute("newcatalog");
		newCatalogPage = clickButton(newCatalogPage, "Apply");

		return clickLinkOnPage(newCatalogPage,"Add New...");
	}

	public void testOverrideOnAddToCollectionPage() throws Exception
	{

		assertXPathPresent(goToNewCategoryPage(),
			"//label[text() = 'The Description']");
	}

	public void testAddNewDisabled() throws Exception
	{
		HtmlPage listCatalogsPage = clickLinkOnPage(startPage, "List Catalogs");
		HtmlPage newCatalogPage = clickLinkOnPage(listCatalogsPage, "New Catalog");
//		HtmlSubmitInput addButton = (HtmlSubmitInput) new HtmlUnitXPath("//input[@type='submit' and @value='Add New...']").selectSingleNode(newCatalogPage);
		HtmlAnchor addLink = null;
		try
		{
			addLink = newCatalogPage.getFirstAnchorByText("Add New...");
		} catch (ElementNotFoundException e)
		{
			assertNotNull(e);  // assertTrue(addButton.isDisabled());
		}
		getInputByName(newCatalogPage, "Name").setValueAttribute("newercatalog");
		newCatalogPage = clickButton(newCatalogPage, "Apply");
//		addButton = (HtmlSubmitInput) new HtmlUnitXPath("//input[@type='submit' and @value='Add New...']").selectSingleNode(newCatalogPage);
		addLink = newCatalogPage.getFirstAnchorByText("Add New...");
		assertNotNull(addLink); // assertFalse(addButton.isDisabled());
	}

	public void testAddProductToCategory() throws Exception
	{
		webClient.setJavaScriptEnabled(false);
		HtmlForm newCategoryForm = goToNewCategoryForm();
        HtmlTextArea textArea = getTextAreaByName(newCategoryForm.getPage(), "Description");
		textArea.setText("howdya doo");
		HtmlPage categoryPage = clickButton(newCategoryForm, "Apply");
		HtmlPage newProductPage = clickLinkOnPage(categoryPage, "Add New...");
		HtmlTextInput input = (HtmlTextInput) getInputByName(newProductPage, "Name");
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
