package org.tynamo.editablecontent.testapp.pages;

import static org.testng.AssertJUnit.assertNull;

import java.io.IOException;
import java.net.MalformedURLException;

import org.mortbay.jetty.webapp.WebAppContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class EditableContentIntegrationTest extends AbstractContainerTest {

	private HtmlPage page;

	@Override
	@BeforeClass
	public void configureWebClient() {
		webClient.setThrowExceptionOnFailingStatusCode(false);
	}

	@Override
	public WebAppContext buildContext() {
		WebAppContext context = new WebAppContext("src/test/webapp", "/");
		/*
		 * Sets the classloading model for the context to avoid an strange "ClassNotFoundException: org.slf4j.Logger"
		 */
		context.setParentLoaderPriority(true);
		return context;
	}

	protected void logIn() throws IOException {
		page = webClient.getPage(BASEURI + "security/login");
		type("tynamoLogin", "author");
		type("tynamoPassword", "author");
		click("tynamoEnter");
	}

	private void type(String id, String value) {
		page.getForms().get(0).<HtmlInput> getInputByName(id).setValueAttribute(value);
	}

	private void click(String id) throws IOException {
		page = clickButton(page, id);
	}

	@Test
	public void noEditAvailableForGuest() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		page = webClient.getPage(BASEURI);
		HtmlElement link = page.getElementById("tynamoLogoutLink");
		if (link != null) link.click();
		assertNull(page.getElementById("editLink"));

	}
}
