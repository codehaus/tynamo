package org.tynamo.examples.conversations.pages;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.Meta;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.conversations.services.ConversationManager;
import org.tynamo.examples.conversations.services.CommentConcierge;

@Meta("tapestry.persistence-strategy=conversation")
@IncludeJavaScriptLibrary( { "LeaveComment.js" })
public class LeaveComment {

	@Persist("session")
	private String conversationId;

	@Inject
	private ConversationManager conversationManager;

	@Inject
	private CommentConcierge commentConcierge;

	@Inject
	private ComponentResources componentResources;

	@Persist
	@Property
	private String comment;

	@Persist
	@Property
	private Integer commentSpot;

	@Property
	private Integer secondsLeft;

	@SuppressWarnings("unused")
	@Property
	@Persist("flash")
	private String message;

	public void setupRender() {
		// Using onActivate() for setting up the conversation is problematic in this case
		// This example demonstrates why managing the existing conversation is tricky with T5.1
		// Self-referential links in T5.2 will managing this easier. Consider the case where the conversation
		// has expired or does not exist, but user submits the form - a new conversation should *not* be created
		// so it's easier to just use setupRender().
		if (commentSpot == null || !conversationManager.isActiveConversation(conversationId)) {
			Integer commentSpot = commentConcierge.reserveCommentSpot();
			if (commentSpot == null) return;
			conversationId = conversationManager.createConversation(componentResources.getPageName(), 60, 60, true);
			// Add 1 to make it more user friendly and so we can directly test against it for conditional rendering
			this.commentSpot = commentSpot + 1;
			comment = "";
		}
		secondsLeft = conversationManager.getSecondsBeforeActiveConversationBecomesIdle();
	}

	public void onActivate() {
		// A hack to make things work in environments where creating threads isn't allowed, such as GAE
		commentConcierge.cleanScheduledReservations();
	}

	public Object onSuccess() {
		if (commentConcierge.setComment(comment)) {
			conversationManager.endConversation(conversationId);
			return Index.class;
		} else {
			message = "Sorry, your reservation for the comment slot had expired";
			conversationManager.endConversation(conversationId);
			return this;
		}
	}

	@Environmental
	private RenderSupport renderSupport;

	public void afterRender() {
		// Should use JavascriptSupport but not ready to completely switch to T5.2 yet
		renderSupport.addScript("initializeIdleDisplay(" + (secondsLeft - 1) + ");");
	}
}
