package org.trailsframework.conversations.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.trailsframework.conversations.services.ConversationManager;

@IncludeJavaScriptLibrary("ConversationModerator.js")
public class ConversationModerator {
	private static final String eventName = "checkIdle";

	@Inject
	private ComponentResources componentResources;

	@Environmental
	private RenderSupport renderSupport;
	
  @Parameter("15")
	private int idleCheck;

  @Parameter("30")
	private int warnBefore;
 
  @Parameter(defaultPrefix="literal")
  private String warnBeforeHandler;

  @Parameter(defaultPrefix="literal")
  private String endedHandler;

  @Parameter("false")
	private boolean keepAlive;
  
  JSONObject onCheckidle() {
  	// FIXME check if keepalive is set
		int nextCheckInSeconds = -1;
		JSONObject object = new JSONObject();
		String conversationId = conversationManager.getActiveConversation();
		// Conversation still exists
		if (conversationId != null) {
			nextCheckInSeconds = conversationManager.getSecondsBeforeActiveConversationBecomesIdle();
			// Shouldn't be negative. 
			if (nextCheckInSeconds < 0) return null;
			// If keepalive is true, subtract 1 so conversation will be refreshed before end,
			if ("true".equals(request.getParameter(ConversationManager.Parameters.keepalive.name()))) nextCheckInSeconds--;
			else {
			}
				// Negative if warn is disabled
				int warnInSeconds = Integer.valueOf(request.getParameter("warn")) ;
				// add 1 , no keepalive
				if (warnInSeconds < 0) nextCheckInSeconds++;
				else {
					warnInSeconds = nextCheckInSeconds - warnInSeconds;
					// Change next check time for warn time or warn
					if (warnInSeconds > 0) nextCheckInSeconds = warnInSeconds;
					// limit how many times you trigger the warn
					else if (warnInSeconds > -nextCheckInSeconds) object.put("warn", nextCheckInSeconds);
					
				}
			}
		
		object.put("nextCheck", nextCheckInSeconds);
		return object;
	}
  
  JSONObject onRefresh() {
  	return null;
  }
  
  JSONObject onEnd() {
  	conversationManager.endConversation(conversationManager.getActiveConversation());
  	return new JSONObject();
  }

	@Inject
	private Request request;

	@Inject
	private ConversationManager conversationManager;

	@AfterRender
	public void afterRender() {
		Link link = componentResources.createEventLink(eventName);
		String baseURI = link.toAbsoluteURI();
		int index = baseURI.indexOf(":" + eventName);
		String defaultURIparameters = baseURI.substring(index + eventName.length() + 1);
		defaultURIparameters += "".equals(defaultURIparameters) ? "?" : "&";
		defaultURIparameters += ConversationManager.Parameters.keepalive.name() + "=";
		baseURI = baseURI.substring(0, index + 1);

		
		// System.out.println("Active conversation is " + conversationManager.getActiveConversation());
		renderSupport.addScript(String.format("var conversationModerator = new ConversationModerator('%s', '%s', %s, true, %s, %s, '%s', '%s');", 
				baseURI, defaultURIparameters, keepAlive, idleCheck, warnBefore, warnBeforeHandler, endedHandler));
	}

}
