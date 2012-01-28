package org.tynamo.security.federatedaccounts.oauth.tokens;

import java.util.Date;

import org.apache.shiro.authc.AuthenticationToken;

public class OauthAccessToken implements AuthenticationToken {

	private static final long serialVersionUID = 1L;
	private String token;
	private Date expiration;

	public OauthAccessToken(String accessToken, long expiresInSeconds) {
		this(accessToken, expiresInSeconds < 0 ? null : new Date(System.currentTimeMillis() + expiresInSeconds * 1000L));
	}

	public OauthAccessToken(String accessToken, Date expiration) {
		this.token = accessToken;
		this.expiration = expiration;
	}

	public String getToken() {
		return token;
	}

	public Date getExpiration() {
		return expiration;
	}

	public String toString() {
		return token;
	}

	/**
	 * @return the granted oauth access token
	 */
	@Override
	public Object getPrincipal() {
		return getToken();
	}

	@Override
	public Object getCredentials() {
		return getExpiration();
	}

}
