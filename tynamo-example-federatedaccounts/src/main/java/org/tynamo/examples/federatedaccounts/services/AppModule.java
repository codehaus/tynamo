package org.tynamo.examples.federatedaccounts.services;

import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ApplicationStateContribution;
import org.apache.tapestry5.services.ApplicationStateCreator;
import org.apache.tapestry5.services.BaseURLSource;
import org.tynamo.common.ModuleProperties;
import org.tynamo.examples.federatedaccounts.session.CurrentUser;
import org.tynamo.examples.federatedaccounts.session.CurrentUserImpl;
import org.tynamo.examples.federatedaccounts.session.FederatedAccountsAuthorizingRealm;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.facebook.services.FacebookFederatedAccountsModule;
import org.tynamo.security.federatedaccounts.services.FederatedAccountService;
import org.tynamo.security.federatedaccounts.services.FederatedAccountsModule;
import org.tynamo.security.federatedaccounts.twitter.services.TwitterFederatedAccountsModule;
import org.tynamo.security.services.SecurityModule;
import org.tynamo.shiro.extension.realm.text.ExtendedPropertiesRealm;

// all specified just because I'm often running the within IDE, not packaged in jars 
@SubModule(value = { SecurityModule.class, FederatedAccountsModule.class, FacebookFederatedAccountsModule.class,
		TwitterFederatedAccountsModule.class })
public class AppModule {
	private static String version = ModuleProperties.getVersion(AppModule.class);

	public static void bind(ServiceBinder binder) {
		binder.bind(FederatedAccountService.class, FederatedAccountServiceExample.class);
		binder.bind(AuthorizingRealm.class, FederatedAccountsAuthorizingRealm.class).withId(
			FederatedAccountsAuthorizingRealm.class.getSimpleName());
	}

	@SuppressWarnings("rawtypes")
	public void contributeApplicationStateManager(MappedConfiguration<Class, ApplicationStateContribution> configuration) {
		ApplicationStateCreator<CurrentUser> currentUserCreator = new ApplicationStateCreator<CurrentUser>() {
			public CurrentUser create() {
				// Inject dependencies as needed, but for GAE we resort to calling CurrentUser.merge() in
				// FederatedAccountService to set the data
				return new CurrentUserImpl();
			}
		};

		configuration.add(CurrentUser.class, new ApplicationStateContribution("session", currentUserCreator));
	}

	public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(SymbolConstants.APPLICATION_VERSION, version);

		configuration.add(FederatedAccountSymbols.COMMITAFTER_OAUTH, "false");
		configuration.add(FederatedAccountSymbols.HTTPCLIENT_ON_GAE, "true");
	}

	public static void contributeWebSecurityManager(Configuration<Realm> configuration,
		@InjectService("FederatedAccountsAuthorizingRealm") AuthorizingRealm authorizingRealm) {

		// There are annoying two little bugs.. 1) TextConfigurationRealm creates simpleaccountinfo map at start-up but
		// doesn't refresh the realm name when it's changed and 2) ExtendedPropertiesRealm doesn't take name as a constructor
		// parameter. So need to do some trickery to set the name before the configuration is loaded
		// ExtendedPropertiesRealm realm = new ExtendedPropertiesRealm("classpath:shiro-users.properties");
		// realm.setName("local");
		ExtendedPropertiesRealm realm = new ExtendedPropertiesRealm("classpath:shiro-users.properties") {
			@Override
			public void setResourcePath(String resourcePath) {
				setName("local");
				super.setResourcePath(resourcePath);
			}
		};

		configuration.add(realm);
		configuration.add(authorizingRealm);
	}

	public static void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
		@Inject @Symbol(SymbolConstants.PRODUCTION_MODE) final boolean productionMode) {
		// In T5.3, you can simply contribute symbols instead
		BaseURLSource source = new BaseURLSource() {
			@Override
			public String getBaseURL(boolean secure) {
				return String.format("%s://%s", secure ? "https" : "http",
					(productionMode ? "tynamo-federatedaccounts.tynamo.org" : "localhost"));
			}
		};
		configuration.add(BaseURLSource.class, source);
	}
}
