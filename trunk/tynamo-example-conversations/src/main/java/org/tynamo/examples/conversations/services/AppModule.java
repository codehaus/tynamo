package org.tynamo.examples.conversations.services;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.tynamo.conversations.ConversationAware;
import org.tynamo.conversations.services.ConversationModule;
import org.tynamo.examples.conversations.pages.LeaveComment;

@SubModule(ConversationModule.class)
public class AppModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(CommentConcierge.class, CommentConciergeImpl.class);
	}

	public static void contributeConversationManager(MappedConfiguration<Class, ConversationAware> configuration,
			CommentConcierge commentConcierge) {
		configuration.add(LeaveComment.class, commentConcierge);
	}

}
