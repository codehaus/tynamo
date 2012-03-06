package org.tynamo.editablecontent.services;

import org.tynamo.editablecontent.entities.TextualContent;

public interface EditableContentStorage {
	public String getHtmlContent(String contentId);

	public TextualContent getTextualContent(String contentId);

	public boolean contains(String contentId);

	public String updateContent(String contentId, String contentValue, int maxHistory);
}