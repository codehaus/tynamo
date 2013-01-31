package org.tynamo.seedentity.hibernate.services;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.apache.tapestry5.ioc.annotations.EagerLoad;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.NaturalId;
import org.hibernate.criterion.Example;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.slf4j.Logger;
import org.tynamo.seedentity.SeedEntityIdentifier;
import org.tynamo.seedentity.SeedEntityUpdater;

@EagerLoad
public class SeedEntityImpl implements SeedEntity {
	@SuppressWarnings("unchecked")
	private Map<Class, SeedEntityIdentifier> typeIdentifiers = new HashMap<Class, SeedEntityIdentifier>();
	private SessionFactory sessionFactory;
	private Logger logger;
	// track newly added entities so you know to update only those ones and otherwise ignore by default
	private List<Object> newlyAddedEntities = new ArrayList<Object>();

	public SeedEntityImpl(Logger logger, HibernateSessionSource sessionSource, List<Object> entities) {
		// Create a new session for this rather than participate in the existing session (through SessionManager)
		// since we need to manage transactions ourselves
		this.logger = logger;
		sessionFactory = sessionSource.getSessionFactory();
		if (entities != null && entities.size() > 0) {
			Session session = sessionSource.create();
			seed(session, entities);
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	void seed(Session session, List<Object> entities) {
		Transaction tx = session.beginTransaction();
		for (Object object : entities) {
			Object entity;
			if (object instanceof SeedEntityUpdater) {
				SeedEntityUpdater entityUpdater = (SeedEntityUpdater) object;
				if (!newlyAddedEntities.contains(entityUpdater.getOriginalEntity())) {
					if (!entityUpdater.isForceUpdate()) {
						logger.info("Entity '" + entityUpdater.getUpdatedEntity() + "' of type "
								+ entityUpdater.getUpdatedEntity().getClass().getSimpleName() + " was not newly added, ignoring update");
						continue;
					}
				}
				if (!entityUpdater.getOriginalEntity().getClass().equals(entityUpdater.getUpdatedEntity().getClass()))
					throw new ClassCastException("The type of original entity doesn't match with the updated entity");
				ClassMetadata metadata = sessionFactory.getClassMetadata(entityUpdater.getOriginalEntity().getClass());
				Serializable identifier = metadata.getIdentifier(entityUpdater.getOriginalEntity(), EntityMode.POJO);
				if (identifier == null)
					throw new IllegalStateException("Cannot make an update to the entity '" + entityUpdater.getUpdatedEntity() + " of type "
							+ entityUpdater.getUpdatedEntity().getClass().getSimpleName() + " because the identifier of the original entity is not set");
				metadata.setIdentifier(entityUpdater.getUpdatedEntity(), identifier, EntityMode.POJO);
				tx.commit();
				session.evict(entityUpdater.getOriginalEntity());
				tx = session.beginTransaction();
				session.update(entityUpdater.getUpdatedEntity());

				continue;
			}

			String uniquelyIdentifyingProperty = null;
			if (object instanceof SeedEntityIdentifier) {
				// SeedEntityIdentifier interface can be used for setting identifier for specific entity only
				// or for all enties of the same type
				SeedEntityIdentifier entityIdentifier = (SeedEntityIdentifier) object;
				if (entityIdentifier.getEntity() instanceof Class) {
					typeIdentifiers.put((Class) entityIdentifier.getEntity(), entityIdentifier);
					continue;
				} else {
					uniquelyIdentifyingProperty = entityIdentifier.getUniquelyIdentifyingProperty();
					entity = entityIdentifier.getEntity();
				}
			} else entity = object;

			if (entity.getClass().getAnnotation(Entity.class) == null) {
				logger.warn("Contributed object '" + entity + "' is not an entity, cannot be used a seed");
				continue;
			}

			if (typeIdentifiers.containsKey(object.getClass()))
				uniquelyIdentifyingProperty = typeIdentifiers.get(object.getClass()).getUniquelyIdentifyingProperty();

			// Note that using example ignores identifier - so seed entities with manually set ids will be re-seeded

			// First find all bean properties
			// This is a little backwards since Example doesn't support .include() but only exclude
			// but I wanted to get it done in as few lines as possible (since the previous implementation
			// based on Trails descriptors and ognl accomplished this in just a few lines) and
			// wasn't really interested in creating the criteria using only unique attributes from scratch

			// TODO it'd be nice if we could use Hibernate ClassMetadata rather than BeanUtils for this
			// but I don't know how to find unique properties by using Hibernate API only
			PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(entity.getClass());
			Set<String> nonUniqueProperties = new HashSet<String>(descriptors.length);
			for (PropertyDescriptor descriptor : descriptors)
				nonUniqueProperties.add(descriptor.getName());

			Set<Set<String>> setsOfUniqueProperties;
			if (uniquelyIdentifyingProperty == null)
				setsOfUniqueProperties = findPossiblePropertiesWithUniqueColumnAnnotation(entity, descriptors);
			else {
				setsOfUniqueProperties = new HashSet<Set<String>>();
				setsOfUniqueProperties.add(new HashSet<String>(Arrays.asList(uniquelyIdentifyingProperty)));
			}
			
			boolean entityWithSameValuesFound = false;
			for (Set<String> uniqueProperties : setsOfUniqueProperties) {
				HashSet<String> nonUniquePropertiesCopy = new HashSet<String>(nonUniqueProperties);
				nonUniquePropertiesCopy.removeAll(uniqueProperties);
				Example example = Example.create(entity);
				for (String nonUniqueProperty : nonUniquePropertiesCopy)
					example.excludeProperty(nonUniqueProperty);
				List<Object> results = session.createCriteria(entity.getClass()).add(example).list();

				if (results.size() > 0) {
					logger.info("At least one existing entity with same unique properties as '" + entity + "' of type '"
						+ entity.getClass().getSimpleName() + "' already exists, skipping seeding this entity");
					// Need to set the id to the seed bean so a new seed entity with a relationship to existing seed entity can be
					// saved.
					Object existingObject = results.get(0);
					// Always evict though it's only needed if existing objects are updated
					session.evict(existingObject);

					// Results should include only one object and we don't know any better which is the right object anyway
					// so use the first one
					ClassMetadata metadata = sessionFactory.getClassMetadata(entity.getClass());
					metadata.setIdentifier(entity, metadata.getIdentifier(existingObject, EntityMode.POJO), EntityMode.POJO);

					entityWithSameValuesFound = true;
					break;
				}
			}
			if (!entityWithSameValuesFound) {
				session.save(entity);
				newlyAddedEntities.add(entity);
			}
		}
		tx.commit();
		newlyAddedEntities.clear();
	}

	private Set<Set<String>> findPossiblePropertiesWithUniqueColumnAnnotation(Object entity, PropertyDescriptor[] descriptors) {
		Set<Set<String>> uniqueProperties = new HashSet<Set<String>>();

		Class<?> entityClass = entity.getClass();
		for(;;) {
			if (entityClass.isAnnotationPresent(Table.class)) {
				Table annotation = entityClass.getAnnotation(Table.class);
				if (annotation.uniqueConstraints() != null) {
					for (UniqueConstraint uniqueConstraint : annotation.uniqueConstraints())
							uniqueProperties.add(new HashSet<String>(Arrays.asList(uniqueConstraint.columnNames())));
				}
			}

			Method[] methods = entityClass.getDeclaredMethods();
			for (Method method : methods) {
				if (!method.isAnnotationPresent(NaturalId.class) && !method.isAnnotationPresent(Column.class))
					continue;
				Column columnAnnotation = method.getAnnotation(Column.class);
				if (columnAnnotation != null && !columnAnnotation.unique())
					continue;
				PropertyDescriptor descriptor = findPropertyForMethod(method, descriptors);
				if (descriptor != null)
					uniqueProperties.add(new HashSet<String>(Arrays.asList(descriptor.getName())));
			}

			// Fields
			Field[] fields = entityClass.getDeclaredFields();
			for (Field currentField : fields) {
				currentField.setAccessible(true);
				if (!currentField.isAnnotationPresent(NaturalId.class) && !currentField.isAnnotationPresent(Column.class))
					continue;
				Column columnAnnotation = currentField.getAnnotation(Column.class);
				if (columnAnnotation != null && !columnAnnotation.unique())
					continue;
				uniqueProperties.add(new HashSet<String>(Arrays.asList(currentField.getName())));
			}
			ClassMetadata superClassMetadata = sessionFactory.getClassMetadata(entityClass.getSuperclass());
			if(!(superClassMetadata instanceof SingleTableEntityPersister)) break;
			entityClass = entityClass.getSuperclass();
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
