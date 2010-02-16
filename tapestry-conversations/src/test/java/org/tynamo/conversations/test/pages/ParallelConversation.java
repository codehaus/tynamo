package org.tynamo.conversations.test.pages;

import java.util.Random;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.conversations.services.ConversationManager;

public class ParallelConversation {
	private final Random random = new Random();

	@Inject
	private ConversationManager conversationManager;

	@Inject
	private ComponentResources componentResources;

	@Persist("conversation")
	@Property
	private int target;

	private String conversationId;

	public Object onActivate() {
		if (target == 0) return createConversation();
		else return null;
	}

	private Object createConversation() {
		conversationId = conversationManager.createConversation(componentResources.getPageName(), 60, false);
		return this;
	}

	public Object onActivate(String conversationId) {
		if (!conversationId.equals(conversationManager.getActiveConversation())) return createConversation();

		if (target == 0) target = random.nextInt(10) + 1;

		this.conversationId = conversationId;
		return null;
	}

	String onPassivate() {
		return conversationId;
	}

	public int getTarget() {
		return target;
	}
}
