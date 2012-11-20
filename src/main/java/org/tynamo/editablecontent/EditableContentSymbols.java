package org.tynamo.editablecontent;

public class EditableContentSymbols {
	// type int, default 10 (items), 0 to disable
	public static final String LRU_CACHE_SIZE = "editablecontent.lrucachesize";
	// type boolean, default true (store using current locale)
	public static final String LOCALIZED_CONTENT = "editablecontent.localizedcontent";
	// type boolean, default false (the default for component readOnly parameter)
	public static final String READONLY_BYDEFAULT = "editablecontent.readonlybydefault";
	// type string, default "" (the default for component's author role parameter)
	public static final String DEFAULT_AUTHORROLE = "editablecontent.defaultauthorrole";
	// type string, default "{'toolbar': 'Basic'}" (the default value for underlying ckeditor parameters attribute)
	public static final String DEFAULT_EDITORPARAMETERS = "editablecontent.editorparameters";
	// type string, default "" (if unit name is empty us the configured persistence unit if there's only a single one)
	public static final String PERSISTENCEUNIT = "editablecontent.persistenceunit";
}
