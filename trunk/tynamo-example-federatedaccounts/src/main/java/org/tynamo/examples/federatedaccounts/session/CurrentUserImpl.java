package org.tynamo.examples.federatedaccounts.session;

import java.io.Serializable;

public class CurrentUserImpl implements Serializable, CurrentUser {
	private static final long serialVersionUID = 1L;

	private String username;
	private String firstName;
	private String lastName;

	public String getUsername() {
		return username;
	}

	public String getDisplayableName() {
		String displayableName = firstName == null ? "" : firstName;
		if (displayableName.length() > 0) if (lastName != null && lastName.length() > 0) displayableName += " " + lastName;
		if (displayableName.length() <= 0) displayableName = username;
		return displayableName;
	}

	/**
	 * @param facebookUser
	 *          User data returned by a Graph API /me request
	 * 
	 *          Merges data from given facebookUser to this current user. In database backed environments, you'd probably
	 *          contribute an ApplicationStateCreator and typically do a database lookup in the constructor of this
	 *          session object rather than allowing external objects call an operation like this
	 */
	public void merge(com.restfb.types.User facebookUser) {
		username = facebookUser.getName();
		firstName = facebookUser.getFirstName();
		lastName = facebookUser.getLastName();
	}

	public void merge(Object remoteAccount) {
		if (remoteAccount instanceof com.restfb.types.User) merge((com.restfb.types.User) remoteAccount);
	}

}
