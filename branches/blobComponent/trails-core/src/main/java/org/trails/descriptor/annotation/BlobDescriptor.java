package org.trails.descriptor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@DescriptorAnnotation(BlobDescriptorAnnotationHandler.class)
public @interface BlobDescriptor
{

    public static final String DEFAULT_fileName = "";
    public static final String DEFAULT_contentType = "";
//    public static final BlobDescriptorExtension.RenderType DEFAULT_renderType = BlobDescriptorExtension.RenderType.LINK;

    public String fileName() default "";

    public String contentType() default "";

//    public BlobDescriptorExtension.RenderType renderType() default BlobDescriptorExtension.RenderType.LINK;


}
