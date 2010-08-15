package org.tynamo.descriptor.annotation;

import org.apache.tapestry5.ioc.annotations.AnnotationUseContext;
import org.apache.tapestry5.ioc.annotations.UseWith;
import org.tynamo.descriptor.TynamoPropertyDescriptor;
import org.tynamo.descriptor.annotation.handlers.PropertyDescriptorAnnotationHandler;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@HandledBy(PropertyDescriptorAnnotationHandler.class)
@Documented
@UseWith(AnnotationUseContext.BEAN)
public @interface PropertyDescriptor
{

	public static final int DEFAULT_index = TynamoPropertyDescriptor.UNDEFINED_INDEX;
	public static final String DEFAULT_format = "no_format";

	/**
	 * Specifies if a property should appear on the list page.
	 *
	 * @return
	 */
	boolean summary() default true;

	/**
	 * Specifies if a property should appear on both edit and list pages
	 *
	 * @return
	 * @see org.tynamo.descriptor.Descriptor#isHidden()
	 */
	boolean hidden() default false;


	boolean readOnly() default false;

	/**
	 * Specifies if property should appear on search pages
	 *
	 * @return
	 */
	boolean searchable() default true;

	/**
	 * Specifies if property can contain html.
	 *
	 * @return
	 */
	boolean richText() default false;

	/**
	 * A format pattern string
	 *
	 * @return
	 * @see java.text.SimpleDateFormat
	 * @see java.text.NumberFormat
	 */
	String format() default "no_format";

	@Deprecated
	int index() default TynamoPropertyDescriptor.UNDEFINED_INDEX;
}
