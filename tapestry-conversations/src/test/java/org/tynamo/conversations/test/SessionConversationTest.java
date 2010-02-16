/*
 * Created on Dec 13, 2004
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
package org.tynamo.conversations.test;

import java.io.IOException;
import java.net.MalformedURLException;

import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import static com.gargoylesoftware.htmlunit.WebAssert.*;
import static org.testng.Assert.*;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class SessionConversationTest extends AbstractContainerTest
{
	@Test
	public void conversationStartedAndOngoing() throws Exception
	{
		// Should we reset the conversation to make sure this succeeds?
		assertEquals(60, getSecondsLeft());
		Thread.sleep(1000);
		assertTrue(getSecondsLeft() < 60);
		HtmlPage page = webClient.getPage(BASEURI + "sessionconversation");
		page = page.getAnchorByName("endconversation").click();
		assertEquals(60, getSecondsLeft(page));
	}
	
	private int getSecondsLeft() throws Exception {
		return getSecondsLeft((HtmlPage)webClient.getPage(BASEURI + "sessionconversation") );
	}
	
	private int getSecondsLeft(HtmlPage page) throws Exception {
		HtmlElement element = page.getElementById("secondsLeft");
		return Integer.parseInt(element.getTextContent());
	}
	
}
