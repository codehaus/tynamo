package org.tynamo.exceptionpage.services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;
import org.tynamo.exceptionpage.ContextAwareException;

@SuppressWarnings("serial")
public class ConfigurableRequestExceptionHandlerTest {
	private ConfigurableRequestExceptionHandler contextFormer = new ConfigurableRequestExceptionHandler(null, null, null, null, null, null,
			null);

	private static class MyContextAwareException extends Throwable implements ContextAwareException {
		private Object[] context;

		public MyContextAwareException(Object[] context) {
			this.context = context;
		}

		public Object[] getContext() {
			return context;
		}

	}

	@Test
	public void noContextWhenExceptionDoesntContainMessage() {
		Object[] context = contextFormer.formExceptionContext(new RuntimeException() {
		});
		assertEquals(context.length, 0);
	}

	@Test
	public void contextIsExceptionMessage() {
		Object[] context = contextFormer.formExceptionContext(new RuntimeException() {
			public String getMessage() {
				return "hello world";
			}
		});
		assertEquals(context.length, 1);
		assertTrue(context[0] instanceof String);
	}

	@Test
	public void contextIsProvidedByContextAwareException() {
		Object[] sourceContext = new Object[] { new Integer(10), this };

		Object[] context = contextFormer.formExceptionContext(new MyContextAwareException(sourceContext) {
		});
		assertEquals(context, sourceContext);

	}
}
