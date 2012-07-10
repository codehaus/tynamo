package org.tynamo.seedentity.jpa.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.apache.tapestry5.ioc.annotations.EagerLoad;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.jpa.EntityManagerManager;
import org.slf4j.Logger;
import org.tynamo.seedentity.SeedEntityIdentifier;
import org.tynamo.seedentity.SeedEntityUpdater;

@EagerLoad
public class SeedEntityImpl implements SeedEntity {
	@SuppressWarnings("unchecked")
	private Map<Class, SeedEntityIdentifier> typeIdentifiers = new HashMap<Class, SeedEntityIdentifier>();
	private Logger logger;
	// track newly added entities so you know to update only those ones and otherwise ignore by default
	private List<Object> newlyAddedEntities = new ArrayList<Object>();
	private PropertyAccess propertyAccess;

	public SeedEntityImpl(Logger logger, PropertyAccess propertyAccess, EntityManagerManager entityManagerManager,
		@Inject @Symbol(SeedEntity.PERSISTENCEUNIT) String persistenceUnitName, List<Object> entities) {
		// Create a new session for this rather than participate in the existing session (through SessionManager)
		// since we need to manage transactions ourselves
		this.logger = logger;
		this.propertyAccess = propertyAccess;

		EntityManager entityManager = null;
		if (persistenceUnitName.isEmpty()) {
			if (entityManagerManager.getEntityManagers().size() != 1)
				throw new IllegalArgumentException(
					"You have to specify the persistenceunit for seedentity if multiple persistence units are configured in the system. Contribute a value for SeedEntity.PERSISTENCEUNIT");
			entityManager = entityManagerManager.getEntityManagers().values().iterator().next();
		} else {
			entityManager = entityManagerManager.getEntityManager(persistenceUnitName);
			if (entityManager == null)
				throw new IllegalArgumentException(
					"Persistence unit '"
						+ persistenceUnitName
						+ "' is configured for seedentity, but it was not found. Check that the contributed name matches with persistenceunit configuration");
		}

		// Session session = sessionSource.create();
		seed(entityManager, entities);
		// session.close();
	}

	@SuppressWarnings("unchecked")
	void seed(EntityManager entityManager, List<Object> entities) {
		Metamodel metamodel = entityManager.getMetamodel();
		EntityTransaction tx = entityManager.getTransaction();
		tx.begin();
		for (Object object : entities) {
			Object entity;
			if (object instanceof String) {
				try {
					entityManager.createNativeQuery(object.toString()).executeUpdate();
					tx.commit();
					tx.begin();
				} catch (Exception e) {
					logger.info("Couldn't execute native seed query '" + object
						+ "', perhaps already executed? Rolling back all statements up to this point. Query failed with: "
						+ e.getMessage());
					tx.rollback();
					tx.begin();
				}
				continue;
			}
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

				EntityType entityType = metamodel.entity(entityUpdater.getOriginalEntity().getClass());
				Type idType = entityType.getIdType();
				SingularAttribute idAttr = entityType.getId(idType.getJavaType());

				Object identifier = propertyAccess.get(entityUpdater.getOriginalEntity(), idAttr.getName());
				if (identifier == null)
					throw new IllegalStateException("Cannot make an update to the entity '" + entityUpdater.getUpdatedEntity()
						+ " of type " + entityUpdater.getUpdatedEntity().getClass().getSimpleName()
						+ " because the identifier of the original entity is not set");
				propertyAccess.set(entityUpdater.getUpdatedEntity(), idAttr.getName(), identifier);
				entityManager.merge(entityUpdater.getUpdatedEntity());
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

			if (uniquelyIdentifyingProperty == null && typeIdentifiers.containsKey(object.getClass()))
				uniquelyIdentifyingProperty = typeIdentifiers.get(object.getClass()).getUniquelyIdentifyingProperty();

			// create a query using unique properties
			// Note that we ignore the identifier - so seed entities with manually set ids will be re-seeded
			EntityType entityType = metamodel.entity(entity.getClass());
			Set<SingularAttribute> singularAttributes = entityType.getSingularAttributes();

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<?> query = cb.createQuery(entity.getClass());
			Root<?> root = query.from(entityType);
			SingularAttribute idAttr = null;

			Set<Predicate> predicates = new HashSet<Predicate>();
			for (SingularAttribute a : singularAttributes) {
				if (a.isId()) {
					// FIXME doesn't deal with composite ids
					idAttr = a;
					continue;
				}
				if (uniquelyIdentifyingProperty == null && isUnique(entityManager, entity.getClass(), a))
					predicates.add(cb.equal(root.get(a.getName()), propertyAccess.get(entity, a.getName())));
			}
			if (uniquelyIdentifyingProperty != null)
				predicates.add(cb.equal(root.get(uniquelyIdentifyingProperty),
					propertyAccess.get(entity, uniquelyIdentifyingProperty)));

			// always re-seed if there are no unique attributes
			if (predicates.size() > 0) {
				query.where(cb.and(predicates.toArray(new Predicate[0])));

				List results = entityManager.createQuery(query).getResultList();

				if (results.size() > 0) {
					logger.info("At least one existing entity with the same unique properties as '" + entity + "' of type '"
						+ entity.getClass().getSimpleName() + "' already exists, skipping seeding this entity");
					// Need to set the id to the seed bean so a new seed entity with a relationship to existing seed entity can be
					// saved.
					// Results should include only one object and we don't know any better which is the right object anyway
					// so use the first one
					Object existingObject = results.get(0);
					// Always evict though it's only needed if existing objects are updated
					entityManager.detach(existingObject);
					propertyAccess.set(entity, idAttr.getName(), propertyAccess.get(existingObject, idAttr.getName()));
					continue;
				}
			}
			entityManager.persist(entity);
			newlyAddedEntities.add(entity);
		}
		tx.commit();
		newlyAddedEntities.clear();
	}

	private boolean isUnique(EntityManager entityManager, Class entityType, SingularAttribute attribute) {
		if (entityType.isAnnotationPresent(Table.class)) {
			Table annotation = (Table) entityType.getAnnotation(Table.class);
			if (annotation.uniqueConstraints() != null) {
				for (UniqueConstraint uniqueConstraint : annotation.uniqueConstraints())
					for (String uniqueColumn : uniqueConstraint.columnNames()) {
						String columnName = attribute.getName();
						// check customised name in @Column
						Column columnAnnotation = (Column) getAnnotation(attribute.getJavaMember(), Column.class);
						if (columnAnnotation != null && columnAnnotation.name() != null) columnName = columnAnnotation.name();

						// check @ManyToOne
						if (attribute.getPersistentAttributeType().equals(Attribute.PersistentAttributeType.MANY_TO_ONE)) {
							JoinColumn joinColumnAnnotation = (JoinColumn) getAnnotation(attribute.getJavaMember(), JoinColumn.class);
							if (joinColumnAnnotation != null && joinColumnAnnotation.name() != null) {
								columnName = joinColumnAnnotation.name();
							} else {
								// lookup the referenced @ManyToOne entity and find it's primary key to create the default generated FK
								// column name
								EntityType<?> referencedEntity = entityManager.getMetamodel().entity(attribute.getJavaType());
								for (SingularAttribute<?, ?> singularAttr : referencedEntity.getSingularAttributes()) {
									if (!singularAttr.isId()) continue;
									Column columnAnn = (Column) getAnnotation(singularAttr.getJavaMember(), Column.class);
									// according to JPA2 specification
									columnName = String.format("%s_%s", attribute.getName(), columnAnn == null ? singularAttr.getName()
										: columnAnn.name());
								}
							}
						}

						if (columnName.equals(uniqueColumn)) return true;
					}
			}
		}

		Column annotation = (Column) getAnnotation(attribute.getJavaMember(), Column.class);
		if (annotation != null && annotation.unique()) return true;
		return false;
	}

	private Annotation getAnnotation(Member member, Class annotationType) {
		return member instanceof Field ? ((Field) member).getAnnotation(annotationType)
			: member instanceof Method ? ((Method) member).getAnnotation(annotationType) : null;
	}

	// Metamodel metamodel = entityManager.getMetamodel();
	// EntityType entityType = metamodel.entity(entity.getClass());
	// Set<SingularAttribute> singularAttributes = entityType.getSingularAttributes();
	//
	// CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	// CriteriaQuery<?> query = cb.createQuery(entity.getClass());
	// Root<?> root = query.from(entityType);
	//
	// // FIXME this is wrong - we should only add the singular attributes that are marked as unique
	// // see how Hibernate seedentity does this
	// // and absolutely filter out id attribute
	//
	// // TODO see http://stackoverflow.com/questions/7077464/how-to-get-singularattribute-mapped-value-of-a-persistent-object
	// // how to use the metamodel api to do this without beanutil
	// for (SingularAttribute a : singularAttributes) {
	// query.where(cb.equal(root.get(a), propertyAccess.get(object, a.getName())));
	// }
	// List results = entityManager.createQuery(query).getResultList();
	//
	// if (results.size() > 0) {
	// logger.info("At least one existing entity with same unique properties as '" + entity + "' of type '"
	// + entity.getClass().getSimpleName() + "' already exists, skipping seeding this entity");
	// // Need to set the id to the seed bean so a new seed entity with a relationship to existing seed entity can be
	// // saved.
	//
	// // Results should include only one object and we don't know any better which is the right object anyway
	// // so use the first one
	//
	// Type idType = entityType.getIdType();
	// SingularAttribute idAttr = entityType.getId(idType.getJavaType());
	// propertyAccess.set(entity, idAttr.getName(), propertyAccess.get(results.get(0), idAttr.getName()));
	//
	// continue;
	// }
	// entityManager.persist(entity);
	// // FIXME need to flush for latter persist() to "see" the previous calls
	// //em.flush();
	// }
	// tx.commit();
	// }
}
