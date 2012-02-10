/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.tynamo.examples.federatedaccounts.pages;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.ExceptionReporter;
import org.apache.tapestry5.services.Request;
import org.tynamo.examples.federatedaccounts.session.CurrentUser;
import org.tynamo.security.federatedaccounts.facebook.FacebookAccessToken;
import org.tynamo.security.federatedaccounts.oauth.tokens.OauthAccessToken;
import org.tynamo.security.federatedaccounts.twitter.TwitterAccessToken;
import org.tynamo.security.federatedaccounts.twitter.services.TwitterRealm;
import org.tynamo.security.services.SecurityService;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;

public class Index implements ExceptionReporter {

	@SuppressWarnings("unused")
	@SessionState(create = false)
	@Property
	private CurrentUser currentUser;

	private Throwable exception;

	@Override
	public void reportException(Throwable exception) {
		this.exception = exception;
	}

	public Throwable getException() {
		return exception;
	}

	public String getMessage() {
		if (exception != null) {
			return exception.getMessage() + " Try login.";
		} else {
			return "";
		}
	}

	@Inject
	private ApplicationStateManager applicationStateManager;

	@Inject
	private SecurityService securityService;

	void onActivate() {
		if (securityService.getSubject().isAuthenticated() && !applicationStateManager.exists(CurrentUser.class)) {
			CurrentUser currentUser = applicationStateManager.get(CurrentUser.class);
			currentUser.merge(securityService.getSubject().getPrincipal());
		}

	}

	@Inject
	private Request request;

	Object onActionFromLogout() {
		// Need to call this explicitly to invoke onlogout handlers (for remember me etc.)
		SecurityUtils.getSubject().logout();
		try {
			// the session is already invalidated, but need to cause an exception since tapestry doesn't know about it
			// and you'll get a container exception message instead without this. Unfortunately, there's no way of
			// configuring Shiro to not invalidate sessions right now. See DefaultSecurityManager.logout()
			// There's a similar issues in Tapestry - Howard has fixed, but no in T5.2.x releases yet
			request.getSession(false).invalidate();
		} catch (Exception e) {
		}

		return this;
	}

	@InjectComponent
	private Zone friendResults;

	@Property
	private List<User> friends;

	@Property
	private User friend;

	@RequiresPermissions("facebook")
	Block onActionFromListFriends() {
		OauthAccessToken accessToken = securityService.getSubject().getPrincipals().oneByType(FacebookAccessToken.class);
		// could check for expiration
		FacebookClient facebookClient = new DefaultFacebookClient(accessToken.toString());

		friends = facebookClient.fetchConnection("me/friends", User.class).getData();
		return friendResults.getBody();
	}
	
	@Inject
	private TwitterFactory twitterFactory;
	
	@Inject
	@Symbol(TwitterRealm.TWITTER_CLIENTID)
	private String oauthClientId;

	@Inject
	@Symbol(TwitterRealm.TWITTER_CLIENTSECRET)
	private String oauthClientSecret;
	
	
	@InjectComponent
	private Zone tweetResults;

	@Property
	private List<Status> tweets;

	@Property
	private Status tweet;

	@RequiresPermissions("twitter")
	Block onActionFromListTweets() throws TwitterException {
		OauthAccessToken accessToken = securityService.getSubject().getPrincipals().oneByType(TwitterAccessToken.class);
		Twitter twitter = twitterFactory.getInstance();
		twitter.setOAuthConsumer(oauthClientId, oauthClientSecret);
		twitter.setOAuthAccessToken((AccessToken)accessToken.getCredentials());
		tweets = twitter.getHomeTimeline();
		return tweetResults.getBody();

	}
	
}
