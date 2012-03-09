package org.tynamo.editablecontent;

import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;

import org.mortbay.jetty.webapp.WebAppContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
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
		// context.setExtraClasspath("../../src/test/resources");
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

	protected void logOut() throws IOException {
		HtmlElement link = page.getElementById("tynamoLogoutLink");
		if (link != null) page = link.click();
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
		logOut();
		assertNull(page.getElementById("editLink"));
	}

	@Test
	public void cacheIsUpdated() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		logIn();
		page = webClient.getPage(BASEURI);
		page = page.getAnchorByText("[ edit ]").click();
		webClient.waitForBackgroundJavaScript(1000);

		HtmlForm form = page.getForms().get(0);
		form.getTextAreaByName("contentArea").setTextContent("testvalue");
		form.getInputByValue("OK").click();
		webClient.waitForBackgroundJavaScript(1000);
		page = webClient.getPage(BASEURI);
		System.out.println("content is " + page.asText());
		assertTrue(page.asText().contains("testvalue"));
		logOut();
		assertTrue(page.asText().contains("testvalue"));
	}
}
