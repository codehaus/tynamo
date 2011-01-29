package org.tynamo.examples.federatedaccounts.session;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccount;
import org.tynamo.security.federatedaccounts.oauth.OauthAccessToken;

public class FederatedAccountsAuthorizingRealm extends AuthorizingRealm {
	private Logger logger;

	public FederatedAccountsAuthorizingRealm(Logger logger) {
		super(new MemoryConstrainedCacheManager());
		this.logger = logger;
		setName("oauthauthorizer");
		setAuthenticationTokenClass(OauthAccessToken.class);
		setPermissionResolver(new WildcardPermissionResolver());
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		if (principals.fromRealm(FederatedAccount.Type.facebook.name()).isEmpty()) return null;

		// We are overcomplicating things for the purposes of this example
		// If you really only wanted to know if user was authenticated against Facebook,
		// you do the above check in any page or service by obtaining Subject from SecurityService
		// However, we wanted to demonstrate local authorization with remote authentication
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		authorizationInfo.addStringPermission(FederatedAccount.Type.facebook.name());
		return authorizationInfo;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// Never participate in authentication process
		return null;
	}

}
