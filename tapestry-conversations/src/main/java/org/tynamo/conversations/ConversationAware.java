package org.tynamo.conversations;

import org.tynamo.conversations.services.Conversation;

public interface ConversationAware {
	public void onIdleCheck();
	
	public void onConversationCreated(Conversation conversation);

	public void onConversationEnded(Conversation conversation, boolean expired);
}
