package org.tynamo.seedentity.services;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.apache.tapestry5.ioc.annotations.EagerLoad;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.tynamo.seedentity.SeedEntityIdentifier;

@EagerLoad
public class SeedEntityImpl implements SeedEntity {
	@SuppressWarnings("unchecked")
	public SeedEntityImpl(Logger logger, HibernateSessionSource sessionSource, HibernateSessionManager sessionManager, List<Object> entities) {
		Session session = sessionManager.getSession();
		SessionFactory sessionFactory = sessionSource.getSessionFactory();
		for (Object object : entities) {
			String uniquelyIdentifyingProperty = null;
			Object entity;
			if (object instanceof SeedEntityIdentifier) {
				uniquelyIdentifyingProperty = ((SeedEntityIdentifier) object).getUniquelyIdentifyingProperty();
				entity = ((SeedEntityIdentifier) object).getEntity();
			} else entity = object;

			if (entity.getClass().getAnnotation(Entity.class) == null) {
				logger.warn("Contributed object '" + entity + "' is not an entity, cannot be used a seed");
				continue;
			}

			// Note that using example ignores identifier - so seed entities with manually set ids will be re-seeded

			// First find all bean properties
			// This is a little backwards since Example doesn't support .include() but only exclude
			// but I wanted to get it done in as few lines as possible (since the previous implementation
			// based on Trails descriptors and ognl accomplished this in just a few lines) and
			// wasn't really interested in creating the criteria using only unique attributes from scratch

			// FIXME see if you can use Hibernate ClassMetadata rather than BeanUtils for this
			PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(entity.getClass());
			Set<String> nonUniqueProperties = new HashSet<String>(descriptors.length);
			for (PropertyDescriptor descriptor : descriptors)
				nonUniqueProperties.add(descriptor.getName());

			// FIXME If there are no uniquely identifying properties, the implementation should always save the seed entry

			if (uniquelyIdentifyingProperty == null) {
				Set<String> uniqueProperties = findPossiblePropertiesWithUniqueColumnAnnotation(entity, descriptors);
				for (String uniqueProperty : uniqueProperties) {
					nonUniqueProperties.remove(uniqueProperty);
				}
			} else nonUniqueProperties.remove(uniquelyIdentifyingProperty);

			Example example = Example.create(entity);
			for (String nonUniqueProperty : nonUniqueProperties)
				example.excludeProperty(nonUniqueProperty);
			List<Object> results = session.createCriteria(entity.getClass()).add(example).list();

			if (results.size() > 0) {
				logger.info("At least one existing entity with same unique properties as '" + entity + "' of type '"
						+ entity.getClass().getSimpleName() + "' already exists, skipping seeding this entity");
				// Need to set the id to the seed bean so a new seed entity with a relationship to existing seed entity can be
				// saved.

				// Results should include only one object and we don't know any better which is the right object anyway
				// so use the first one
				ClassMetadata metadata = sessionFactory.getClassMetadata(entity.getClass());
				metadata.setIdentifier(entity, metadata.getIdentifier(results.get(0), EntityMode.POJO), EntityMode.POJO);

				continue;
			}
			session.save(entity);
		}
		sessionManager.commit();
	}

	private Set<String> findPossiblePropertiesWithUniqueColumnAnnotation(Object entity, PropertyDescriptor[] descriptors) {
		Set<String> uniqueProperties = new HashSet<String>();

		if (entity.getClass().isAnnotationPresent(Table.class)) {
			Table annotation = entity.getClass().getAnnotation(Table.class);
			if (annotation.uniqueConstraints() != null) {
				for (UniqueConstraint uniqueConstraint : annotation.uniqueConstraints())
					for (String uniqueColumn : uniqueConstraint.columnNames())
						uniqueProperties.add(uniqueColumn);
			}
		}

		Class<? extends Object> aClass = entity.getClass();
		Method[] methods = aClass.getDeclaredMethods();
		for (Method method : methods) {
			// if (!method.isAccessible()) continue;
			if (!method.isAnnotationPresent(Column.class)) continue;
			Column annotation = method.getAnnotation(Column.class);
			if (!annotation.unique()) continue;
			PropertyDescriptor descriptor = findPropertyForMethod(method, descriptors);
			if (descriptor != null) uniqueProperties.add(descriptor.getName());
		}

		// Fields
		Field[] fields = aClass.getDeclaredFields();
		for (Field currentField : fields) {
			currentField.setAccessible(true);
			if (!currentField.isAnnotationPresent(Column.class)) continue;
			Column annotation = currentField.getAnnotation(Column.class);
			if (!annotation.unique()) continue;
			uniqueProperties.add(currentField.getName());
		}

		return uniqueProperties;

	}

	public static PropertyDescriptor findPropertyForMethod(Method method, PropertyDescriptor[] descriptors) {
		for (PropertyDescriptor pd : descriptors) {
			if (method.equals(pd.getReadMethod()) || method.equals(pd.getWriteMethod())) { return pd; }
		}
		return null;
	}

}
