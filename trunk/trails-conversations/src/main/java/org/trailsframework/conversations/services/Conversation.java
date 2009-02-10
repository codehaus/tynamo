package org.trailsframework.conversations.services;

public class Conversation {
	private final String pageName;

	private final String id;

	private final boolean usingCookie;

	private final long maxIdleSeconds;

	private long lastTouched;
	
	private long requiredEndTimestamp;

	public boolean isUsingCookie() {
		return usingCookie;
	}

	protected Conversation(String id, String pageName, int maxIdleSeconds, int maxConversationLengthSeconds, boolean usingCookie) {
		this.id = id;
		this.pageName = pageName;
		this.usingCookie = usingCookie;
		this.maxIdleSeconds = maxIdleSeconds;
		this.requiredEndTimestamp = maxConversationLengthSeconds == 0 ? 0 : System.currentTimeMillis() + maxConversationLengthSeconds * 1000L;
		touch();
	}

	public String getId() {
		return id;
	}

	public String getPageName() {
		return pageName;
	}

	public void touch() {
		lastTouched = System.currentTimeMillis();
	}

	public Integer getSecondsBeforeBecomesIdle() {
		if (maxIdleSeconds <= 0) return null;
		return (int) ((maxIdleSeconds - (System.currentTimeMillis() - lastTouched) / 1000L));
	}

	/**
	 * True if conversation has been idle for too long or past its maxConversationLength, 
	 * otherwise resets the idletime if resetIdle is true
	 **/
	public boolean isIdle(boolean resetIdle) {
		if (requiredEndTimestamp > 0) {
			if (System.currentTimeMillis() > requiredEndTimestamp) return true;
		}
		if (maxIdleSeconds < 1) {
			if (resetIdle) touch();
			return false;
		}
		if ((System.currentTimeMillis() - lastTouched) / 1000L > maxIdleSeconds) return true;
		if (resetIdle) touch();
		return false;
	}

}
