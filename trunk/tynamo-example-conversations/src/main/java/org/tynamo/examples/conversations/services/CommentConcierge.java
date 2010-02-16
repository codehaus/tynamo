package org.tynamo.examples.conversations.services;

import org.tynamo.conversations.ConversationAware;

public interface CommentConcierge extends ConversationAware {

	public Integer reserveCommentSpot();

	public boolean setComment(String comment);

	public String[] getComments();

	public void cleanScheduledReservations();
}
