package org.trails.i18n;

import java.io.IOException;

import org.apache.hivemind.service.ThreadLocale;
import org.apache.tapestry.services.WebRequestServicer;
import org.apache.tapestry.services.WebRequestServicerFilter;
import org.apache.tapestry.web.WebRequest;
import org.apache.tapestry.web.WebResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RefererFilter implements WebRequestServicerFilter
{

	private static final Log LOG = LogFactory.getLog(RefererFilter.class);

	public void service(WebRequest request, WebResponse response, WebRequestServicer servicer) throws IOException

	{
		LOG.debug(request.getHeader("referer"));
		servicer.service(request, response);
	}


}