package org.tynamo.conversations.services;

import org.tynamo.conversations.ConversationAware;

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

	public boolean activateConversation(Object parameterObject);

	public String endConversation(String conversationId);

	public void setPagePersistentFieldStrategy(ConversationalPersistentFieldStrategy pagePersistentFieldStrategy);
	
	public void addConversationListener(String pageName, ConversationAware conversationAware);

	public void removeConversationListener(String pageName, ConversationAware conversationAware);

}
