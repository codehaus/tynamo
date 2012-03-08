package org.tynamo.editablecontent.services;

import org.apache.tapestry5.jpa.annotations.CommitAfter;
import org.tynamo.editablecontent.entities.TextualContent;

public interface EditableContentStorage {
	public TextualContent getTextualContent(String contentId);

	public boolean contains(String contentId);

	@CommitAfter
	public String updateContent(String contentId, String contentValue, int maxHistory);
}