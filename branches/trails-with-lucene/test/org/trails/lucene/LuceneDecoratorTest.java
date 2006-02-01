package org.trails.lucene;

import junit.framework.TestCase;
import org.trails.descriptor.*;
import org.trails.test.IndexedPojo;

public class LuceneDecoratorTest extends TestCase
{
    LuceneDescriptorDecorator decorator = new LuceneDescriptorDecorator();

    public void testDecorate()
    {
        IClassDescriptor descriptor = new TrailsClassDescriptor(IndexedPojo.class, "IndexedPojo");

        IPropertyDescriptor unstoredFieldPropDesc = new TrailsPropertyDescriptor(IndexedPojo.class, "name", String.class);
        descriptor.getPropertyDescriptors().add(unstoredFieldPropDesc);

        IPropertyDescriptor textFieldPropDesc = new TrailsPropertyDescriptor(IndexedPojo.class, "description", String.class);
        textFieldPropDesc.setIndex(1);
        descriptor.getPropertyDescriptors().add(textFieldPropDesc);

        descriptor.getPropertyDescriptors().add(new IdentifierDescriptor(IndexedPojo.class, "id", Integer.class));
        descriptor.getPropertyDescriptors().add(new TrailsPropertyDescriptor(IndexedPojo.class, "value", Double.class));
        descriptor.getPropertyDescriptors().add(new TrailsPropertyDescriptor(IndexedPojo.class, "value2", Double.class));


        descriptor = decorator.decorate(descriptor);

        assertEquals("Indexed Pojo", descriptor.getDisplayName());
        assertTrue(descriptor.getPropertyDescriptor("name").isIndexedByLucene());
        assertTrue(descriptor.getPropertyDescriptor("description").isIndexedByLucene());
        assertTrue(descriptor.getPropertyDescriptor("id").isIndexedByLucene());
        assertTrue(!descriptor.getPropertyDescriptor("value").isIndexedByLucene());
        assertTrue(descriptor.getPropertyDescriptor("value2").isIndexedByLucene());

        assertEquals("Name", descriptor.getPropertyDescriptor("name").getLuceneFieldName());
        assertEquals("description", descriptor.getPropertyDescriptor("description").getLuceneFieldName());
        assertEquals("Value2", descriptor.getPropertyDescriptor("value2").getLuceneFieldName());

    }
}
