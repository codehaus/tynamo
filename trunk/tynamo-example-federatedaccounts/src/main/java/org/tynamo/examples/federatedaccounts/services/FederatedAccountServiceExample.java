package org.tynamo.examples.federatedaccounts.services;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.tynamo.examples.federatedaccounts.session.CurrentUser;
import org.tynamo.security.federatedaccounts.services.FederatedAccountService;

public class FederatedAccountServiceExample implements FederatedAccountService {
	private final ApplicationStateManager applicationStateManager;

	public FederatedAccountServiceExample(ApplicationStateManager applicationStateManager) {
		this.applicationStateManager = applicationStateManager;
	}

	@Override
	public AuthenticationInfo federate(String realmName, Object remotePrincipal, AuthenticationToken authenticationToken, Object remoteAccount) {
		CurrentUser currentUser = applicationStateManager.get(CurrentUser.class);
		currentUser.merge(remoteAccount);
		SimplePrincipalCollection principalCollection = new SimplePrincipalCollection(remotePrincipal, realmName);
		principalCollection.add(authenticationToken, realmName);
		return new SimpleAuthenticationInfo(principalCollection, authenticationToken.getCredentials());
		// return new SimpleAuthenticationInfo(currentUser.getUsername(), authenticationToken.getCredentials(), realmName);
	}

}
