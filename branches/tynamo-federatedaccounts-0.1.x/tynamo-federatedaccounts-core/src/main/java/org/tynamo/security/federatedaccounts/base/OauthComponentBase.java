package org.tynamo.security.federatedaccounts.base;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.tynamo.security.federatedaccounts.FederatedAccount.FederatedAccountType;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.util.WindowMode;

public abstract class OauthComponentBase {
	private static final String CLIENTID = ".clientid";
	private static final String CLIENTSECRET = ".clientsecret";
	@Inject
	@Symbol(FederatedAccountSymbols.COMMITAFTER_OAUTH)
	private boolean autocommit;

	public boolean isAutocommit() {
		return autocommit;
	}

	@Inject
	private SymbolSource symbolSource;

	public FederatedAccountType getAccountType() {
		String name = getClass().getSimpleName();
		if (name.startsWith("OpenId")) return FederatedAccountType.openid;
		try {
			return FederatedAccountType.valueOf(name.substring(0, name.indexOf("Oauth")).toLowerCase());
			// FIXME implement the try-catch properly
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public String getOauthClientId() {
		return symbolSource.valueForSymbol(getAccountType().name() + CLIENTID);
	}

	protected String getOauthClientSecret() {
		return symbolSource.valueForSymbol(getAccountType().name() + CLIENTSECRET);
	}

	public boolean isOauthConfigured() {
		return !"".equals(getOauthClientId()) && !"".equals(getOauthClientSecret());
	}

	protected abstract Class getOauthPageClass();

	@Inject
	private PageRenderLinkSource linkSource;

	protected String getOauthRedirectLink(Object... context) {
		if (context == null || !(context[0] instanceof WindowMode))
			throw new IllegalArgumentException("WindowMode is required as the first context parameter");
		return linkSource.createPageRenderLinkWithContext(getOauthPageClass(), context).toAbsoluteURI();
	}

}
