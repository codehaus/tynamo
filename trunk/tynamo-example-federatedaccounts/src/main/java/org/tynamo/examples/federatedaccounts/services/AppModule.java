package org.tynamo.examples.federatedaccounts.services;

import java.io.IOException;
import java.util.Properties;

import org.apache.shiro.realm.Realm;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.ApplicationStateContribution;
import org.apache.tapestry5.services.ApplicationStateCreator;
import org.tynamo.examples.federatedaccounts.session.CurrentUser;
import org.tynamo.examples.federatedaccounts.session.CurrentUserImpl;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.federatedaccounts.HostSymbols;
import org.tynamo.security.federatedaccounts.services.FederatedAccountService;
import org.tynamo.security.federatedaccounts.services.FederatedAccountsModule;
import org.tynamo.security.services.SecurityModule;
import org.tynamo.shiro.extension.realm.text.ExtendedPropertiesRealm;

@SubModule(value = { SecurityModule.class, FederatedAccountsModule.class })
public class AppModule {
	private static final String version;
	static {
		Properties moduleProperties = new Properties();
		String aVersion = "unversioned-" + System.currentTimeMillis();
		try {
			moduleProperties.load(AppModule.class.getResourceAsStream("module.properties"));
			aVersion = moduleProperties.getProperty("module.version");
		} catch (IOException e) {
			// ignore
		}
		version = aVersion;
	}

	public static void bind(ServiceBinder binder) {
		binder.bind(FederatedAccountService.class, FederatedAccountServiceExample.class);
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
		configuration.add(SecuritySymbols.SHOULD_LOAD_INI_FROM_CONFIG_PATH, "true");

		configuration.add(HostSymbols.HOSTNAME, "tynamo-federatedaccounts.tynamo.org");
		configuration.add(HostSymbols.COMMITAFTER_OAUTH, "false");
		configuration.add(HostSymbols.HTTPCLIENT_ON_GAE, "true");
	}

	public static void contributeWebSecurityManager(Configuration<Realm> configuration) {
		ExtendedPropertiesRealm realm = new ExtendedPropertiesRealm("classpath:shiro-users.properties");
		configuration.add(realm);
	}
}
