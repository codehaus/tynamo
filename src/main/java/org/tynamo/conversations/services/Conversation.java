package org.tynamo.conversations.services;

import java.io.Serializable;

public final class Conversation implements Serializable {
	private static final long serialVersionUID = 333320784727498530L;

	private final String pageName;

	private final String sessionId;

	private final String id;

	private final boolean usingCookie;

	private final long maxIdleSeconds;

	private long lastTouched;

	private long requiredEndTimestamp;

	public boolean isUsingCookie() {
		return usingCookie;
	}

	Conversation(String sessionId, String id, String pageName, Integer maxIdleSeconds, Integer maxConversationLengthSeconds, boolean usingCookie) {
		if (maxIdleSeconds == null) maxIdleSeconds = 0;
		if (maxConversationLengthSeconds == null) maxConversationLengthSeconds = 0;
		this.sessionId = sessionId;
		this.id = id;
		this.pageName = pageName;
		this.usingCookie = usingCookie;
		this.maxIdleSeconds = maxIdleSeconds;
		this.requiredEndTimestamp = maxConversationLengthSeconds == 0 ? 0L : System.currentTimeMillis() + maxConversationLengthSeconds * 1000L;
		touch();
	}

	public String getId() {
		return id;
	}

	public String getSessionId() {
		return sessionId;
	}
	
	public String getPageName() {
		return pageName;
	}

	public void touch() {
		lastTouched = System.currentTimeMillis();
	}

	public Integer getSecondsBeforeBecomesIdle() {
		if (maxIdleSeconds <= 0) return null;
		int secondsLeft = (int) ((maxIdleSeconds - (System.currentTimeMillis() - lastTouched) / 1000L));
		if (requiredEndTimestamp > 0) secondsLeft = Math.min(secondsLeft, (int)((requiredEndTimestamp - System.currentTimeMillis())/1000L));
		return secondsLeft < 0 ? 0 : secondsLeft;
	}

	/**
	 * True if conversation has been idle for too long or past its maxConversationLength, otherwise resets the idletime if
	 * resetIdle is true
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
