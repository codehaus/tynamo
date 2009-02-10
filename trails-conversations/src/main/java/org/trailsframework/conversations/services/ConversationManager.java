package org.trailsframework.conversations.services;

public interface ConversationManager {
	public enum Parameters {
		keepalive
	};

	public boolean exists(String conversationId);

	public String createConversation(String pageName, Integer maxIdleSeconds);

	public String createConversation(String pageName, Integer maxIdleSeconds, boolean useCookie);

	public String createConversation(String pageName, Integer maxIdleSeconds, Integer maxConversationLengthSeconds, boolean useCookie);

	public String createConversation(String id, String pageName, Integer maxIdleSeconds, Integer maxConversationLengthSeconds, boolean useCookie);

	public String getActiveConversation();

	public boolean isActiveConversation(String conversationId);

	public int getSecondsBeforeActiveConversationBecomesIdle();

	public boolean activateConversation(String conversationId);

	public String endConversation(String conversationId);

	public void setPagePersistentFieldStrategy(ConversationalPersistentFieldStrategy pagePersistentFieldStrategy);

}
