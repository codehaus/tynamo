package org.trails.lucene;

import ognl.Ognl;
import ognl.OgnlException;
import org.hibernate.lucene.Indexed;
import org.hibernate.lucene.Keyword;
import org.hibernate.lucene.Text;
import org.hibernate.lucene.Unstored;
import org.trails.descriptor.DescriptorDecorator;
import org.trails.descriptor.IClassDescriptor;
import org.trails.descriptor.IPropertyDescriptor;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;


public class LuceneDescriptorDecorator implements DescriptorDecorator
{

    public IClassDescriptor decorate(IClassDescriptor descriptor)
    {
        descriptor.setIndexedByLucene(descriptor.getType().getAnnotation(Indexed.class) != null);
        if (descriptor.isIndexedByLucene())
        {
            for (IPropertyDescriptor propertyDescriptor : (List<IPropertyDescriptor>) descriptor.getPropertyDescriptors())
            {
                addLuceneDescriptor(propertyDescriptor);
            }
        }
        return descriptor;
    }

    private void addLuceneDescriptor(IPropertyDescriptor descriptor)
    {
        IPropertyDescriptor clonedDescriptor = (IPropertyDescriptor) descriptor.clone();
        try
        {
            PropertyDescriptor beanPropDescriptor = (PropertyDescriptor) Ognl.getValue("propertyDescriptors.{? name == '" + descriptor.getName() + "'}[0]", Introspector.getBeanInfo(clonedDescriptor.getBeanType()));
            Method readMethod = beanPropDescriptor.getReadMethod();
            Keyword keyword = readMethod.getAnnotation(Keyword.class);
            Text text = readMethod.getAnnotation(Text.class);
            Unstored unstored = readMethod.getAnnotation(Unstored.class);

            if (keyword != null)
            {
                descriptor.setIndexedByLucene(true);
                descriptor.setLuceneFieldName(keyword.name());

            } else if (text != null)
            {
                descriptor.setIndexedByLucene(true);
                descriptor.setLuceneFieldName(text.name());
            } else if (unstored != null)
            {
                descriptor.setIndexedByLucene(true);
                descriptor.setLuceneFieldName(unstored.name());
            }

            if ("".equals(descriptor.getLuceneFieldName())) {
                descriptor.setLuceneFieldName(readMethod.getName().substring(3));
                /* I know this is awful, but it's a patch for the default lucene field name in hibernate
                 * check org.hibernate.lucene.DocumentBuilder:getAttributeName(Method method, String name)
                 */
            }

        } catch (OgnlException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IntrospectionException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
