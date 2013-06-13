package org.tynamo.security.federatedaccounts.pac4j;

import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.services.LibraryMapping;
import org.tynamo.common.ModuleProperties;
import org.tynamo.security.federatedaccounts.FederatedAccount.FederatedAccountType;
import org.tynamo.security.federatedaccounts.pac4j.services.Pac4jFederatedRealm;
import org.tynamo.security.federatedaccounts.pac4j.services.Pac4jOauthClientLocator;
import org.tynamo.security.federatedaccounts.services.FederatedAccountsModule;
import org.tynamo.security.federatedaccounts.services.FederatedSignInComponentBlockSource;
import org.tynamo.security.federatedaccounts.services.FederatedSignInComponentContribution;

public class Pac4jFederatedAccountsModule {
	private static final String PATH_PREFIX = "federated-pac4j";
	private static String version = ModuleProperties.getVersion(FederatedAccountsModule.class);

	public static void bind(ServiceBinder binder) {
		binder.bind(AuthenticatingRealm.class, Pac4jFederatedRealm.class).withId(Pac4jFederatedRealm.class.getSimpleName());
		binder.bind(Pac4jOauthClientLocator.class);
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(Pac4jFederatedRealm.FACEBOOK_CLIENTID, "");
		configuration.add(Pac4jFederatedRealm.FACEBOOK_CLIENTSECRET, "");
	}

	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping(FederatedAccountsModule.PATH_PREFIX,
			"org.tynamo.security.federatedaccounts.pac4j"));
	}

	public static void contributeClasspathAssetAliasManager(MappedConfiguration<String, String> configuration) {
		configuration.add(PATH_PREFIX + "-" + version, "org/tynamo/security/federatedaccounts/pac4j");
	}

	public static void contributeWebSecurityManager(Configuration<Realm> configuration,
		@InjectService("Pac4jFederatedRealm") AuthenticatingRealm pac4jRealm) {
		configuration.add(pac4jRealm);
	}

	@Contribute(FederatedSignInComponentBlockSource.class)
	public static void addSignInComponentBlocks(Configuration<FederatedSignInComponentContribution> configuration) {
		configuration.add(new FederatedSignInComponentContribution(FederatedAccountType.pac4j_.name()
			+ Pac4jOauthClientLocator.SupportedClient.dropbox, "federated/pac4jSignInComponentBlocks"));
	}

}
