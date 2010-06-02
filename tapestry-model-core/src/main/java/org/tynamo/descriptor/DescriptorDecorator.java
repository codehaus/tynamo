package org.tynamo.descriptor;

/**
 * @author Chris Nelson
 *         <p/>
 *         A descriptor decorator may modify or replace an TynamoClassDescriptor.  A
 *         "pipeline" of these is reponsible for ultimately building the final descriptors.
 */
public interface DescriptorDecorator
{
	public TynamoClassDescriptor decorate(TynamoClassDescriptor descriptor);
}
