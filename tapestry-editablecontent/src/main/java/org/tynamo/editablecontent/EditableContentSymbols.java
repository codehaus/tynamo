package org.tynamo.editablecontent;

public class EditableContentSymbols {
	public static final String LRU_CACHE_SIZE = "editablecontent.lrucachesize"; // type int, default 10 (items), 0 to disable
	public static final String LOCALIZED_CONTENT = "editablecontent.localizedcontent"; // type boolean, default true (store using current
																																											// locale)
	public static final String READONLY_BYDEFAULT = "editablecontent.readonlybydefault"; // type boolean, default false (the default for
																																												// component readOnly parameter)
	public static final String DEFAULT_AUTHORROLE = "editablecontent.defaultauthorrole"; // type string, default "" (the default for
																																												// component's author role parameter)
	public static final String PERSISTENCEUNIT = "editablecontent.persistenceunit"; // type string, default "" (if unit name is empty use the
																																									// configured persistence unit if there's only a single
																																									// one)
}
