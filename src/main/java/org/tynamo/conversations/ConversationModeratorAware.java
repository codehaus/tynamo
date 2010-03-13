package org.tynamo.conversations;

public interface ConversationModeratorAware {
	public void onConversationIdleCheck();
	
	public void onConversationEnded();
}
