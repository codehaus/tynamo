package org.tynamo.editablecontent;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.jpa.JpaTransactionAdvisor;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.LibraryMapping;
import org.tynamo.editablecontent.internal.services.EditableContentStorageImpl;
import org.tynamo.editablecontent.services.EditableContentStorage;

public class EditableContentModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(EditableContentStorage.class, EditableContentStorageImpl.class);
	}

	@Contribute(ComponentClassResolver.class)
	public static void setupCkEditorLibrary(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping("tynamo", EditableContentModule.class.getPackage().getName()));
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(EditableContentSymbols.LRU_CACHE_SIZE, "100");
		configuration.add(EditableContentSymbols.LOCALIZED_CONTENT, Boolean.TRUE.toString());
		configuration.add(EditableContentSymbols.READONLY_BYDEFAULT, Boolean.FALSE.toString());
		configuration.add(EditableContentSymbols.DEFAULT_AUTHORROLE, "");
	}

	@Match("EditableContentStorage")
	public static void adviseTransactions(JpaTransactionAdvisor advisor, MethodAdviceReceiver receiver) {
		advisor.addTransactionCommitAdvice(receiver);
	}
}
