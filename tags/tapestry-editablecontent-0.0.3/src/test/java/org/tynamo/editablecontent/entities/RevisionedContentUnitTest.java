package org.tynamo.editablecontent.entities;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

public class RevisionedContentUnitTest {

	@Test
	public void compressAndDecompressContent() {
		TextualContent content = new TextualContent();
		content.setId("id");
		content.setValue("test content is stored tähän ");
		RevisionedContent revision = new RevisionedContent(content);
		assertEquals(content.getValue(), revision.valueToString());
	}
}
