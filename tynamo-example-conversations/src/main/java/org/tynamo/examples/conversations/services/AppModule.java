package org.tynamo.examples.conversations.services;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.tynamo.conversations.services.ConversationModule;

@SubModule(ConversationModule.class)
public class AppModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(CommentConcierge.class, CommentConciergeImpl.class);
	}

}
