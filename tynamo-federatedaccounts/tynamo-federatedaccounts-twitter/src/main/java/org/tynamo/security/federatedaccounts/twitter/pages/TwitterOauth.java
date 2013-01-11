package org.tynamo.security.federatedaccounts.twitter.pages;

import java.net.URL;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.BaseURLSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.slf4j.Logger;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.base.AbstractOauthPage;
import org.tynamo.security.federatedaccounts.components.FlashMessager;
import org.tynamo.security.federatedaccounts.twitter.TwitterAccessToken;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterOauth extends AbstractOauthPage {
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

	private boolean oauthAuthenticated;

	@Inject
	private TwitterFactory twitterFactory;

	private String returnUri;

	protected TwitterFactory getTwitterFactory() {
		return twitterFactory;
	}

	@Override
	protected Object onOauthActivate(EventContext eventContext) throws Exception {
		if (eventContext.getCount() > 2) {
			String action = eventContext.get(String.class, 1);
			// pass along this redirectUrl
			returnUri = eventContext.get(String.class, 2);
			if ("request_token".equals(action)) {
				Twitter twitter = getTwitterFactory().getInstance();
				twitter.setOAuthConsumer(getOauthClientId(), getOauthClientSecret());
				return new URL(twitter.getOAuthRequestToken(getOauthRedirectLink(getWindowMode(), returnUri))
					.getAuthenticationURL());
			}
		}

		// capture the redirect url again
		if (eventContext.getCount() > 1) returnUri = eventContext.get(String.class, 1);

		String oauth_token = request.getParameter("oauth_token");
		String oauth_verifier = request.getParameter("oauth_verifier");
		if (oauth_verifier == null) {
			flashMessager.setFailureMessage("No Oauth verifier code provided");
			return null;
		}

		Twitter twitter = getTwitterFactory().getInstance();
		twitter.setOAuthConsumer(getOauthClientId(), getOauthClientSecret());
		AccessToken accessToken = twitter.getOAuthAccessToken(new RequestToken(oauth_token, getOauthClientSecret()),
			oauth_verifier);

		try {
			SecurityUtils.getSubject().login(new TwitterAccessToken(accessToken));
			flashMessager.setSuccessMessage("User successfully authenticated");
			oauthAuthenticated = true;
		} catch (AuthenticationException e) {
			logger
				.error("Using access token " + accessToken + "\nCould not sign in a Twitter federated user because of: ", e);
			// FIXME Deal with other account exception types like expired and
			// locked
			flashMessager.setFailureMessage("A Twitter federated user cannot be signed in, report this to support.\n "
				+ e.getMessage());
		}
		return null;
	}

	@Inject
	private BaseURLSource baseURLSource;

	public String getSuccessLink() {
		if (returnUri != null) return returnUri;

		return "".equals(successUrl) ? "" : baseURLSource.getBaseURL(request.isSecure()) + successUrl;
	}

	@Environmental
	private JavaScriptSupport javaScriptSupport;

	protected void afterRender() {
		if (oauthAuthenticated)
			javaScriptSupport.addScript("onAuthenticationSuccess('" + getSuccessLink() + "', '" + getWindowMode().name()
				+ "');");
	}
}
