/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.tynamo.hibernate.services;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.tapestry5.func.F;
import org.apache.tapestry5.func.Predicate;
import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.tynamo.descriptor.CollectionDescriptor;
import org.tynamo.descriptor.EmbeddedDescriptor;
import org.tynamo.descriptor.IdentifierDescriptor;
import org.tynamo.descriptor.IdentifierDescriptorImpl;
import org.tynamo.descriptor.ObjectReferenceDescriptor;
import org.tynamo.descriptor.TynamoClassDescriptor;
import org.tynamo.descriptor.TynamoPropertyDescriptor;
import org.tynamo.descriptor.decorators.DescriptorDecorator;
import org.tynamo.descriptor.extension.EnumReferenceDescriptor;
import org.tynamo.descriptor.factories.DescriptorFactory;
import org.tynamo.exception.MetadataNotFoundException;
import org.tynamo.exception.TynamoRuntimeException;
import org.tynamo.hibernate.TynamoHibernateSymbols;

/**
 * This decorator will add metadata information. It will replace simple
 * reflection based TynamoPropertyTynamoPropertyDescriptors with appropriate
 * Hibernate descriptors <p/> Background... TynamoDescriptorService operates one
 * ReflectorDescriptorFactory - TynamoDescriptorService iterates/scans all class
 * types encountered - ReflectorDescriptorFactory allocates property descriptor
 * instance for the class type - TynamoDescriptorService decorates property
 * descriptor by calling this module HibernateDescriptorDecorator -
 * HibernateDescriptorDecorator caches the decorated property descriptor into a
 * decorated descriptor list - decorated descriptor list gets populated into
 * class descriptor for class type - TynamoDescriptorService finally populates
 * decorated class descriptor and it's aggregated list of decorated property
 * descriptors into it's own list/cache of referenced class descriptors
 *
 * @see TynamoPropertyDescriptor
 * @see ObjectReferenceDescriptor
 * @see CollectionDescriptor
 * @see EmbeddedDescriptor
 */
public class HibernateDescriptorDecorator implements DescriptorDecorator
{
	private Logger logger;

	private HibernateSessionSource hibernateSessionSource;

	private DescriptorFactory descriptorFactory;

	/**
	 * Columns longer than this will have their large property set to true.
	 */
	private final int largeColumnLength;

	private final boolean ignoreNonHibernateTypes;

	public HibernateDescriptorDecorator(HibernateSessionSource hibernateSessionSource,
	                                    DescriptorFactory descriptorFactory,
	                                    @Inject @Symbol(TynamoHibernateSymbols.LARGE_COLUMN_LENGTH)
	                                    int largeColumnLength,
	                                    @Inject @Symbol(TynamoHibernateSymbols.IGNORE_NON_HIBERNATE_TYPES)
	                                    boolean ignoreNonHibernateTypes,
	                                    Logger logger)
	{
		this.hibernateSessionSource = hibernateSessionSource;
		this.descriptorFactory = descriptorFactory;
		this.largeColumnLength = largeColumnLength;
		this.ignoreNonHibernateTypes = ignoreNonHibernateTypes;
		this.logger = logger;
	}

	public TynamoClassDescriptor decorate(TynamoClassDescriptor descriptor)
	{
		java.util.List<TynamoPropertyDescriptor> decoratedPropertyDescriptors = new ArrayList<TynamoPropertyDescriptor>();

		Class beanType = descriptor.getBeanType();
		ClassMetadata classMetaData = null;

		try
		{
			classMetaData = findMetadata(beanType);
		} catch (MetadataNotFoundException e)
		{
			if (ignoreNonHibernateTypes)
			{
				logger.warn("MetadataNotFound! could not decorate: " + descriptor.getBeanType().getSimpleName());
				descriptor.setNonVisual(true);
				return descriptor;
			} else
			{
				throw new TynamoRuntimeException(e);
			}
		}

		for (TynamoPropertyDescriptor propertyDescriptor : descriptor.getPropertyDescriptors())
		{
			try
			{
				TynamoPropertyDescriptor descriptorReference;

				if (propertyDescriptor.getName().equals(getIdentifierProperty(beanType)))
				{
					descriptorReference = createIdentifierDescriptor(beanType, propertyDescriptor);
				} else if (notAHibernateProperty(classMetaData, propertyDescriptor))
				{
					propertyDescriptor.setTransient(true);
					descriptorReference = propertyDescriptor;
				} else
				{
					Property mappingProperty = getMapping(beanType).getProperty(propertyDescriptor.getName());
					descriptorReference = decoratePropertyDescriptor(beanType, mappingProperty, propertyDescriptor);
				}

				decoratedPropertyDescriptors.add(descriptorReference);

			} catch (HibernateException e)
			{
				throw new TynamoRuntimeException(e);
			}
		}
		descriptor.setPropertyDescriptors(decoratedPropertyDescriptors);
		return descriptor;
	}

	protected TynamoPropertyDescriptor decoratePropertyDescriptor(Class beanType, Property mappingProperty, TynamoPropertyDescriptor descriptor)
	{
		if (isFormula(mappingProperty))
		{
			descriptor.setReadOnly(true);
			return descriptor;
		}
		descriptor.setLength(findColumnLength(mappingProperty));
		descriptor.setLarge(isLarge(mappingProperty));
		if (!mappingProperty.isOptional())
		{
			descriptor.setRequired(true);
		}

		if (!mappingProperty.isInsertable() && !mappingProperty.isUpdateable())
		{
			descriptor.setReadOnly(true);
		}

		TynamoPropertyDescriptor descriptorReference = descriptor;

		Type hibernateType = mappingProperty.getType();

		if (mappingProperty.getType() instanceof ComponentType)
		{
			descriptorReference = buildEmbeddedDescriptor(beanType, mappingProperty, descriptor);
		} else if (Collection.class.isAssignableFrom(descriptor.getPropertyType()))
		{
			descriptorReference = decorateCollectionDescriptor(beanType, descriptor);
		} else if (hibernateType.isAssociationType())
		{
			descriptorReference = decorateAssociationDescriptor(beanType, mappingProperty, descriptor);
		} else if (hibernateType.getReturnedClass().isEnum())
		{
			descriptor.addExtension(EnumReferenceDescriptor.class.getName(), new EnumReferenceDescriptor(hibernateType.getReturnedClass()));
		}

		return descriptorReference;
	}

	private EmbeddedDescriptor buildEmbeddedDescriptor(Class beanType, Property mappingProperty, TynamoPropertyDescriptor descriptor)
	{
		Component componentMapping = (Component) mappingProperty.getValue();
		TynamoClassDescriptor baseDescriptor = descriptorFactory.buildClassDescriptor(descriptor.getPropertyType());

		// build from base descriptor
		EmbeddedDescriptor embeddedDescriptor = new EmbeddedDescriptor(beanType, baseDescriptor);

		// and copy from property descriptor
		embeddedDescriptor.copyFrom(descriptor);

		java.util.List<TynamoPropertyDescriptor> decoratedProperties = new ArrayList<TynamoPropertyDescriptor>();

		// go thru each property and decorate it with Hibernate info
		for (TynamoPropertyDescriptor propertyDescriptor : embeddedDescriptor.getPropertyDescriptors())
		{
			if (notAHibernateProperty(componentMapping, propertyDescriptor))
			{
				decoratedProperties.add(propertyDescriptor);
			} else
			{
				Property property = componentMapping.getProperty(propertyDescriptor.getName());
				TynamoPropertyDescriptor tynamopropertydescriptor =
						decoratePropertyDescriptor(embeddedDescriptor.getPropertyType(), property, propertyDescriptor);
				decoratedProperties.add(tynamopropertydescriptor);
			}
		}
		embeddedDescriptor.setPropertyDescriptors(decoratedProperties);
		return embeddedDescriptor;
	}

	/**
	 * Find the Hibernate metadata for this type, traversing up the hierarchy to
	 * supertypes if necessary
	 *
	 * @param type
	 * @return
	 */
	protected ClassMetadata findMetadata(Class type) throws MetadataNotFoundException
	{
		ClassMetadata metaData = hibernateSessionSource.getSessionFactory().getClassMetadata(type);
		if (metaData != null)
		{
			return metaData;
		}
		if (!type.equals(Object.class))
		{
			return findMetadata(type.getSuperclass());
		} else
		{
			throw new MetadataNotFoundException("Failed to find metadata.");
		}
	}

	private boolean isFormula(Property mappingProperty)
	{
		for (Iterator iter = mappingProperty.getColumnIterator(); iter.hasNext(); )
		{
			Selectable selectable = (Selectable) iter.next();
			if (selectable.isFormula())
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks to see if a property descriptor is in a component mapping
	 *
	 * @param componentMapping
	 * @param propertyDescriptor
	 * @return true if the propertyDescriptor property is in componentMapping
	 */
	protected boolean notAHibernateProperty(Component componentMapping, TynamoPropertyDescriptor propertyDescriptor)
	{
		for (Iterator iter = componentMapping.getPropertyIterator(); iter.hasNext(); )
		{
			Property property = (Property) iter.next();
			if (property.getName().equals(propertyDescriptor.getName()))
			{
				return false;
			}
		}
		return true;
	}

	private boolean isLarge(Property mappingProperty)
	{
		// Hack to avoid setting large property if length
		// is exactly equal to Hibernate default column length
		return findColumnLength(mappingProperty) != Column.DEFAULT_LENGTH
				&& findColumnLength(mappingProperty) > largeColumnLength;
	}

	private int findColumnLength(Property mappingProperty)
	{
		int length = 0;
		for (Iterator iter = mappingProperty.getColumnIterator(); iter.hasNext(); )
		{
			Column column = (Column) iter.next();
			length += column.getLength();
		}
		return length;
	}

	/**
	 * @param classMetaData
	 * @param descriptor
	 * @return
	 */
	protected boolean notAHibernateProperty(ClassMetadata classMetaData, final TynamoPropertyDescriptor descriptor)
	{
		return F.flow(classMetaData.getPropertyNames()).filter(new Predicate<String>()
		{
			public boolean accept(String propertyName)
			{
				return descriptor.getName().equals(propertyName);
			}
		}).isEmpty();
	}

	/**
	 * @param beanType
	 * @param descriptor
	 * @param parentClassDescriptor
	 * @return
	 */
	private IdentifierDescriptor createIdentifierDescriptor(Class beanType, TynamoPropertyDescriptor descriptor)
	{
		IdentifierDescriptor identifierDescriptor;
		PersistentClass mapping = getMapping(beanType);

		/**
		 * fix for TRAILS-92
		 */
		if (mapping.getProperty(descriptor.getName()).getType() instanceof ComponentType)
		{
			EmbeddedDescriptor embeddedDescriptor = buildEmbeddedDescriptor(beanType, mapping.getProperty(descriptor.getName()), descriptor);
			embeddedDescriptor.setIdentifier(true);
			identifierDescriptor = embeddedDescriptor;
		} else
		{
			identifierDescriptor = new IdentifierDescriptorImpl(beanType, descriptor);
		}

		if (((SimpleValue) mapping.getIdentifier()).getIdentifierGeneratorStrategy().equals("assigned"))
		{
			identifierDescriptor.setGenerated(false);
		}

		return identifierDescriptor;
	}

	/**
	 * @param type
	 * @return
	 */
	protected PersistentClass getMapping(Class type)
	{
		Configuration cfg = hibernateSessionSource.getConfiguration();

		return cfg.getClassMapping(type.getName());
	}

	/**
	 * @param beanType
	 * @param descriptor
	 */
	private CollectionDescriptor decorateCollectionDescriptor(Class beanType, TynamoPropertyDescriptor descriptor)
	{
		try
		{
			CollectionDescriptor collectionDescriptor = new CollectionDescriptor(beanType, descriptor);
			org.hibernate.mapping.Collection collectionMapping = findCollectionMapping(beanType, descriptor.getName());
			// It is a child relationship if it has delete-orphan specified in
			// the mapping
			collectionDescriptor.setChildRelationship(collectionMapping.hasOrphanDelete());
			CollectionMetadata collectionMetaData = hibernateSessionSource.getSessionFactory().getCollectionMetadata(
					collectionMapping.getRole());

			collectionDescriptor.setElementType(collectionMetaData.getElementType().getReturnedClass());

			collectionDescriptor.setOneToMany(collectionMapping.isOneToMany());

			decorateOneToManyCollection(beanType, collectionDescriptor, collectionMapping);

			return collectionDescriptor;

		} catch (HibernateException e)
		{
			throw new TynamoRuntimeException(e);
		}
	}

	public TynamoPropertyDescriptor decorateAssociationDescriptor(final Class beanType,
	                                                              final Property mappingProperty,
	                                                              final TynamoPropertyDescriptor descriptor)
	{
		Type hibernateType = mappingProperty.getType();

		ObjectReferenceDescriptor descriptorReference = new ObjectReferenceDescriptor(beanType,
				descriptor,
				hibernateType.getReturnedClass());

		try
		{
			Field propertyField = beanType.getDeclaredField(descriptor.getName());

			PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(beanType).getPropertyDescriptors();

			PropertyDescriptor beanPropDescriptor = F.flow(propertyDescriptors).filter(new Predicate<PropertyDescriptor>()
			{
				public boolean accept(PropertyDescriptor propertyDescriptor)
				{
					return propertyDescriptor.getName().equals(descriptor.getName());
				}
			}).first();

			Method readMethod = beanPropDescriptor.getReadMethod();

			// Start by checking for and retrieving mappedBy attribute inside the annotation
			String inverseProperty = "";
			if (readMethod.isAnnotationPresent(javax.persistence.OneToOne.class))
			{
				inverseProperty = readMethod.getAnnotation(javax.persistence.OneToOne.class).mappedBy();
			} else if (propertyField.isAnnotationPresent(javax.persistence.OneToOne.class))
			{
				inverseProperty = propertyField.getAnnotation(javax.persistence.OneToOne.class).mappedBy();
			} else
			{
				// If there is none then just return the ObjectReferenceDescriptor
				return descriptorReference;
			}

			if ("".equals(inverseProperty))
			{
				// http://forums.hibernate.org/viewtopic.php?t=974287&sid=12d018b08dffe07e263652190cfc4e60
				// Caution... this does not support multiple
				// class references across the OneToOne relationship
				Class returnType = readMethod.getReturnType();
				for (int i = 0; i < returnType.getDeclaredMethods().length; i++)
				{
					if (returnType.getDeclaredMethods()[i].getReturnType().equals(propertyField.getDeclaringClass()))
					{
						Method theProperty = returnType.getDeclaredMethods()[i];
						/* strips preceding 'get' */
						inverseProperty = theProperty.getName().substring(3).toLowerCase();
						break;
					}
				}
			}

		} catch (SecurityException e)
		{
			logger.error("Could not decorate association.", e);
		} catch (NoSuchFieldException e)
		{
			logger.error("Could not decorate association.", e);
		} catch (IntrospectionException e)
		{
			logger.error("Could not decorate association.", e);
		}
		return descriptorReference;
	}

	/**
	 * I couldn't find a way to get the "mappedBy" value from the collection
	 * metadata, so I'm getting it from the OneToMany annotation.
	 */
	private void decorateOneToManyCollection(final Class beanType,
	                                         final CollectionDescriptor collectionDescriptor,
	                                         org.hibernate.mapping.Collection collectionMapping)
	{
		if (collectionDescriptor.isOneToMany() && collectionMapping.isInverse())
		{
			try
			{

				Field propertyField = beanType.getDeclaredField(collectionDescriptor.getName());

				PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(beanType).getPropertyDescriptors();

				PropertyDescriptor beanPropDescriptor = F.flow(propertyDescriptors).filter(new Predicate<PropertyDescriptor>()
				{
					public boolean accept(PropertyDescriptor propertyDescriptor)
					{
						return propertyDescriptor.getName().equals(collectionDescriptor.getName());
					}
				}).first();

				Method readMethod = beanPropDescriptor.getReadMethod();
				String mappedBy = "";
				if (readMethod.isAnnotationPresent(javax.persistence.OneToMany.class))
				{
					mappedBy = readMethod.getAnnotation(javax.persistence.OneToMany.class).mappedBy();
				} else if (propertyField.isAnnotationPresent(javax.persistence.OneToMany.class))
				{
					mappedBy = propertyField.getAnnotation(javax.persistence.OneToMany.class).mappedBy();
				}

				if (!"".equals(mappedBy))
				{
					collectionDescriptor.setInverseProperty(mappedBy);
				}

			} catch (SecurityException e)
			{
				logger.error(e.getMessage());
				e.printStackTrace();
			} catch (NoSuchFieldException e)
			{
				logger.error(e.getMessage());
				e.printStackTrace();
			} catch (IntrospectionException e)
			{
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	protected org.hibernate.mapping.Collection findCollectionMapping(Class type, String name)
	{
		String roleName = type.getName() + "." + name;
		org.hibernate.mapping.Collection collectionMapping = hibernateSessionSource.getConfiguration()
				.getCollectionMapping(roleName);
		if (collectionMapping != null)
		{
			return collectionMapping;
		} else if (!type.equals(Object.class))
		{
			return findCollectionMapping(type.getSuperclass(), name);
		} else
		{
			throw new MetadataNotFoundException("Metadata not found.");
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tynamo.descriptor.PropertyDescriptorService#getIdentifierProperty(java.lang.Class)
	 */

	public String getIdentifierProperty(Class type)
	{
		try
		{
			return hibernateSessionSource.getSessionFactory().getClassMetadata(type).getIdentifierPropertyName();
		} catch (HibernateException e)
		{
			throw new TynamoRuntimeException(e);
		}
	}
}
