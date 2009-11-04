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
import org.apache.tapestry5.ioc.annotations.EagerLoad;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;

@EagerLoad
public class SeedEntityImpl implements SeedEntity {
	@SuppressWarnings("unchecked")
	public SeedEntityImpl(Logger logger, Session session, List<Object> entities) {
		Transaction tx = session.beginTransaction();
		try {
			for (Object entity : entities) {
				if (entity.getClass().getAnnotation(Entity.class) == null) {
					logger.warn("Contributed object '" + entity + "' is not an entity, cannot be used a seed");
					continue;
				}

				// Note that using example ignore identifier - so seed entities with manually set ids will be re-seeded
				// TynamoPropertyDescriptor identifierDescriptor = classDescriptor.getIdentifierDescriptor();
				// Object id = null, savedObject = null;
				// String propertyName = identifierDescriptor.getName();
				// try {
				// id = Ognl.getValue(propertyName, entity);
				// } catch (OgnlException e) {
				// LOGGER.warn("Couldn't get the id of a seed bean " + entity + " because of: ", e);
				// }

				// FIXME first use findUniqueOnClass - if @Table annotation with unique constraints is
				// available, use that only - note that it needs to be negation - all fields but...

				// First find all bean properties
				// This is a little backwards since Example doesn't support .include() but only exclude
				// but I wanted to get it done in as few lines as possible (since the previous implementation
				// based on Trails descriptors and ognl accomplished this in just a few lines) and
				// wasn't really interested in creating the criteria using only unique attributes from scratch
				PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(entity.getClass());
				Set<String> nonUniqueProperties = new HashSet<String>(descriptors.length);
				for (PropertyDescriptor descriptor : descriptors)
					nonUniqueProperties.add(descriptor.getName());

				Set<String> uniqueProperties = findPossiblePropertiesWithUniqueColumnAnnotation(entity, descriptors);
				for (String uniqueProperty : uniqueProperties) {
					nonUniqueProperties.remove(uniqueProperty);
				}

				Example example = Example.create(entity);
				for (String nonUniqueProperty : nonUniqueProperties)
					example.excludeProperty(nonUniqueProperty);
				List<Object> results = session.createCriteria(entity.getClass()).add(example).list();

				if (results.size() > 0) {
					logger.info("Existing entities with same unique properties as '" + entity + "' of type '" + entity.getClass()
							+ "' already exist, skipping seeding this entity");
					continue;
				}
				session.save(entity);
			}
			tx.commit();
		} catch (RuntimeException e) {
			tx.rollback();
			throw e;
		}
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

		Class aClass = entity.getClass();
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

	private void findUniqueOnClass(Object obj) {
		// @Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "startDate" }) })

	}

	public static PropertyDescriptor findPropertyForMethod(Method method, PropertyDescriptor[] descriptors) {
		for (PropertyDescriptor pd : descriptors) {
			if (method.equals(pd.getReadMethod()) || method.equals(pd.getWriteMethod())) { return pd; }
		}
		return null;
	}

}
