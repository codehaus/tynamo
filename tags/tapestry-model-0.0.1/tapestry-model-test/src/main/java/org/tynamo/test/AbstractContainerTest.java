/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.tynamo.test;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.ClickableElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.resource.ResourceCollection;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;

import java.io.IOException;

import static com.gargoylesoftware.htmlunit.WebAssert.assertTextNotPresent;
import static com.gargoylesoftware.htmlunit.WebAssert.assertTextPresent;
import static org.testng.Assert.*;

public abstract class AbstractContainerTest
{
	protected static PauseableServer server;

	protected static final int port = 8180;

	protected static final String BASEURI = "http://localhost:" + port + "/";

	protected final WebClient webClient = new WebClient();

	static String errorText = "You must correct the following errors before you may continue";

	@BeforeClass
	public static void startContainer() throws Exception
	{
		if (server == null)
		{
			server = new PauseableServer();
			Connector connector = new SelectChannelConnector();
			connector.setPort(port);
			server.setConnectors(new Connector[]{connector});

			WebAppContext context = new WebAppContext("src/main/webapp", "/");
			ResourceCollection resourceCollection =
					new ResourceCollection(new String[]{"src/main/webapp", "src/test/webapp"});
			context.setBaseResource(resourceCollection);

			HandlerCollection handlers = new HandlerCollection();
			handlers.setHandlers(new Handler[]{context, new DefaultHandler()});
			server.setHandler(handlers);
			server.start();
			assertTrue(server.isStarted());
		}
	}

	@BeforeTest
	public void configureWebClient()
	{
		webClient.setThrowExceptionOnFailingStatusCode(true);
	}

	public void pauseServer(boolean paused)
	{
		if (server != null) server.pause(paused);
	}

	public static class PauseableServer extends Server
	{
		public synchronized void pause(boolean paused)
		{
			try
			{
				if (paused) for (Connector connector : getConnectors())
					connector.stop();
				else for (Connector connector : getConnectors())
					connector.start();
			} catch (Exception e)
			{
			}
		}
	}

	protected void assertXPathPresent(HtmlPage page, String xpath) throws Exception
	{
		assertNotNull(page.getByXPath(xpath).get(0));
	}

	protected void assertXPathNotPresent(HtmlPage page, String xpath) throws Exception
	{
		assertNull(page.getByXPath(xpath).get(0));
	}


	protected HtmlPage clickLink(HtmlPage page, String linkText)
	{
		try
		{
			return (HtmlPage) page.getFirstAnchorByText(linkText).click();
		} catch (ElementNotFoundException e)
		{
			fail("Couldn't find a link with text '" + linkText + "' on page " + page);
		} catch (IOException e)
		{
			fail("Clicking on link '" + linkText + "' on page " + page + " failed because of: ", e);
		}
		return null;
	}

	protected HtmlPage clickButton(HtmlPage page, String buttonId) throws IOException
	{
		ClickableElement button = (ClickableElement) page.getElementById(buttonId);
		return button.click();
	}

	protected HtmlPage clickButton(HtmlForm form, String buttonValue) throws IOException
	{
		try
		{
			return form.<HtmlInput>getInputByValue(buttonValue).click();
		} catch (ElementNotFoundException e)
		{
			try
			{
				return form.getButtonByName(buttonValue).click();
			} catch (ElementNotFoundException e1)
			{
				fail("Couldn't find a button with text/name '" + buttonValue + "' on form '" + form.getNameAttribute() +
						"'");
			}
		}
		return null;
	}

	protected void assertErrorTextPresent(HtmlPage page)
	{
		assertTextPresent(page, errorText);
	}

	protected void assertErrorTextNotPresent(HtmlPage page)
	{
		assertTextNotPresent(page, errorText);
	}

}
