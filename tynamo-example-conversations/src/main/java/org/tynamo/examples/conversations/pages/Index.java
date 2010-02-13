package org.tynamo.examples.conversations.pages;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.examples.conversations.services.CommentConcierge;

public class Index {
	@InjectPage
	private Guess guess;

	@Inject
	@Property
	private CommentConcierge commentConcierge;

	Object onAction() {
		return guess;
	}

	@Property
	private String comment;

	public String[] getComments() {
		return commentConcierge.getComments();
	}
}