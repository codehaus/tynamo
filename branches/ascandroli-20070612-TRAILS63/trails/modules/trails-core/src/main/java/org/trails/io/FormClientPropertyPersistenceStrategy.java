//
// Based on com.zillow.web.infrastructure.FormClientPropertyPersistenceStrategy
// described in http://wiki.apache.org/jakarta-tapestry/FormClientPersistence
//

package org.trails.io;

import org.apache.tapestry.engine.ServiceEncoding;
import org.apache.tapestry.record.ClientPropertyPersistenceStrategy;

public class FormClientPropertyPersistenceStrategy extends ClientPropertyPersistenceStrategy
{

	@Override
	public void addParametersForPersistentProperties(ServiceEncoding encoding, boolean post)
	{
		if (post)
		{
			super.addParametersForPersistentProperties(encoding, post);
		}
	}
}