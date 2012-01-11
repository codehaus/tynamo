package org.tynamo.exceptionpage;

import java.io.IOException;
import java.util.List;

public interface ExceptionHandlerAssistant {
	public Class handleRequestException(Throwable exception, List<Object> exceptionContext) throws IOException;
}
