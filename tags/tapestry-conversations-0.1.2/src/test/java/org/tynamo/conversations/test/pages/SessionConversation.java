package org.tynamo.conversations.test.pages;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Meta;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.conversations.services.ConversationManager;

@Meta("tapestry.persistence-strategy=conversation")
public class SessionConversation {
	@Persist("session")
	private String conversationId;

	@Inject
	private ConversationManager conversationManager;

	@Inject
	private ComponentResources componentResources;

	@Persist
	@Property
	private String comment;

	@SuppressWarnings("unused")
	@Property
	private Integer secondsLeft;

	public void setupRender() {
		// Using onActivate() for setting up the conversation is problematic in this case
		// This example demonstrates why managing the existing conversation is tricky with T5.1
		// Self-referential links in T5.2 will managing this easier. Consider the case where the conversation
		// has expired or does not exist, but user submits the form - a new conversation should *not* be created
		// so it's easier to just use setupRender().
		if (comment == null || !conversationManager.isActiveConversation(conversationId)) {
			conversationId = conversationManager.createConversation(componentResources.getPageName(), 60, 60, true);
			comment = "";
		}
		secondsLeft = conversationManager.getSecondsBeforeActiveConversationBecomesIdle();
	}
	
	public void onAction() {
		conversationManager.endConversation(conversationId);
	}
	
}
