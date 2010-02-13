package org.tynamo.examples.conversations.services;

public interface CommentConcierge {

	public Integer reserveCommentSpot();

	public boolean setComment(String comment);

	public String[] getComments();

	public void cleanScheduledReservations();
}
