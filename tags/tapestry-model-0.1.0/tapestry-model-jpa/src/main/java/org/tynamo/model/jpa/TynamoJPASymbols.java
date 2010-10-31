package org.tynamo.model.jpa;


public class TynamoJPASymbols
{

	/**
	 * Columns longer than this will have their large property set to true.
	 */
	public static final String LARGE_COLUMN_LENGTH = "tynamo.model.jpa.large-column-length";

	/**
	 * When working with objects from multiple sources jpa decorator complains about "metadata not found",
	 * this symbol configured to true tells JPADescriptorDecorator to ignore these errors.
	 */
	public static final String IGNORE_NON_HIBERNATE_TYPES = "tynamo.model.jpa.ignore-non-jpa-types";

}
