package org.tynamo.security.federatedaccounts.pac4j.services;

import static org.tynamo.security.federatedaccounts.FederatedAccount.FederatedAccountType.pac4j_;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.pac4j.core.profile.UserProfile;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.pac4j.Pac4jAuthenticationToken;
import org.tynamo.security.federatedaccounts.services.FederatedAccountService;

/**
 * <p/>
 * A {@link org.apache.shiro.realm.Realm} that authenticates with Google.
 */
public class Pac4jFederatedRealm extends AuthenticatingRealm {
	public static final String DROPBOX_CLIENTID = "pac4j_dropbox.clientid";
	public static final String DROPBOX_CLIENTSECRET = "pac4j_dropbox.clientsecret";
	public static final String FACEBOOK_CLIENTID = "pac4j_facebook.clientid";
	public static final String FACEBOOK_CLIENTSECRET = "pac4j_facebook.clientsecret";
	public static final String GITHUB_CLIENTID = "pac4j_github.clientid";
	public static final String GITHUB_CLIENTSECRET = "pac4j_github.clientsecret";
	public static final String GOOGLE_CLIENTID = "pac4j_google.clientid";
	public static final String GOOGLE_CLIENTSECRET = "pac4j_google.clientsecret";
	public static final String LINKEDIN_CLIENTID = "pac4j_linkedin.clientid";
	public static final String LINKEDIN_CLIENTSECRET = "pac4j_linkedin.clientsecret";
	public static final String TWITTER_CLIENTID = "pac4j_twitter.clientid";
	public static final String TWITTER_CLIENTSECRET = "pac4j_twitter.clientsecret";
	public static final String WINDOWSLIVE_CLIENTID = "pac4j_windowslive.clientid";
	public static final String WINDOWSLIVE_CLIENTSECRET = "pac4j_windowslive.clientsecret";
	public static final String WORDPRESS_CLIENTID = "pac4j_wordpress.clientid";
	public static final String WORDPRESS_CLIENTSECRET = "pac4j_wordpress.clientsecret";
	public static final String YAHOO_CLIENTID = "pac4j_yahoo.clientid";
	public static final String YAHOO_CLIENTSECRET = "pac4j_yahoo.clientsecret";

	private Logger logger;

	private FederatedAccountService federatedAccountService;

	public Pac4jFederatedRealm(Logger logger, FederatedAccountService federatedAccountService) {

		super(new MemoryConstrainedCacheManager());
		this.federatedAccountService = federatedAccountService;
		this.logger = logger;

		setName(pac4j_.name());
		setAuthenticationTokenClass(Pac4jAuthenticationToken.class);
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
		throws AuthenticationException {

		Pac4jAuthenticationToken token = (Pac4jAuthenticationToken) authenticationToken;
		UserProfile profile = (UserProfile) token.getPrincipal();
		/*
		 * OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.google.com/m8/feeds/contacts/default/full");
		 * service.signRequest(token.getToken(), request); request.send().getBody();
		 */

		// Null username is invalid, throw an exception if so - indicates that user hasn't granted the right
		// permissions (and/or we haven't asked for it)
		/*
		 * if (facebookUser == null) throw new AccountException("Null Facebook user is not allowed by this realm.");
		 * 
		 * String principalValue = null; switch (principalProperty) { case id: principalValue = facebookUser.getId(); break; case email:
		 * principalValue = facebookUser.getEmail(); break; case name: principalValue = facebookUser.getName(); break; }
		 */

		return federatedAccountService.federate(pac4j_.name() + profile.getClass().getSimpleName(), profile.getTypedId(),
			authenticationToken, profile);
		// SimplePrincipalCollection principalCollection = new SimplePrincipalCollection(authenticationToken.getPrincipal(),
		// pac4j.name());
		// principalCollection.add(authenticationToken, pac4j.name());
		// return new SimpleAuthenticationInfo(principalCollection, authenticationToken.getCredentials());

	}
}
