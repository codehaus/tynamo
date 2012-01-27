package org.tynamo.security.federatedaccounts.twitter.base;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.BaseURLSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.components.FlashMessager;
import org.tynamo.security.federatedaccounts.twitter.TwitterAuthenticationToken;
import org.tynamo.security.federatedaccounts.util.WindowMode;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

public abstract class AbstractTwitterOauthPage extends TwitterOauthComponentBase {
	@Inject
	@Symbol(FederatedAccountSymbols.HTTPCLIENT_ON_GAE)
	private boolean httpClientOnGae;

	@Inject
	@Symbol(FederatedAccountSymbols.SUCCESSURL)
	private String successUrl;

	@Inject
	private Logger logger;

	@Inject
	private Request request;

	@Component
	private FlashMessager flashMessager;

	@Inject
	private PageRenderLinkSource linkSource;

	private boolean fbAuthenticated;

	@Property
	private WindowMode windowMode;

	protected void onActivate(String windowModeText) throws TwitterException {
		try {
			windowMode = WindowMode.valueOf(windowModeText);
		} catch (IllegalArgumentException e) {
		}

		String oauth_verifier = request.getParameter("oauth_verifier");
		if (oauth_verifier == null) {
			flashMessager.setFailureMessage("No Oauth verifier code provided");
			return;
		}

		Twitter twitter = getTwitterFactory().getInstance();
		twitter.setOAuthConsumer(getOauthClientId(), getOauthClientSecret());
		AccessToken accessToken = twitter.getOAuthAccessToken(oauth_verifier);

		try {
			SecurityUtils.getSubject().login(new TwitterAuthenticationToken(accessToken, -1));
			flashMessager.setSuccessMessage("User successfully authenticated");
			fbAuthenticated = true;
		} catch (AuthenticationException e) {
			logger
				.error("Using access token " + accessToken + "\nCould not sign in a Twitter federated user because of: ", e);
			// FIXME Deal with other account exception types like expired and
			// locked
			flashMessager.setFailureMessage("A Twitter federated user cannot be signed in, report this to support.\n "
				+ e.getMessage());
		}
	}

	@Inject
	private BaseURLSource baseURLSource;

	public String getSuccessLink() {
		return "".equals(successUrl) ? "" : baseURLSource.getBaseURL(request.isSecure()) + successUrl;
	}

	@Environmental
	private JavaScriptSupport javaScriptSupport;

	protected void afterRender() {
		if (fbAuthenticated)
			javaScriptSupport.addScript("onAuthenticationSuccess('" + getSuccessLink() + "', '" + windowMode.name() + "');");
	}
}
