package org.tynamo.seedentity.jpa.services;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
import org.tynamo.seedentity.jpa.SeedEntityIdentifier;

@EagerLoad
public class SeedEntityImpl implements SeedEntity {
	@SuppressWarnings("unchecked")
	public SeedEntityImpl(Logger logger, PropertyAccess propertyAccess, EntityManagerManager entityManagerManager,  
		@Inject @Symbol(SeedEntity.PERSISTENCEUNIT) String persistenceUnitName,
		List<Object> entities) throws InvocationTargetException, NoSuchMethodException {
		
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
		
		// FIXME do we need to handle transactions properly
		EntityTransaction tx = entityManager.getTransaction();
		tx.begin();
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

			Metamodel metamodel = entityManager.getMetamodel();
			EntityType entityType = metamodel.entity(entity.getClass());
			Set<SingularAttribute> singularAttributes = entityType.getSingularAttributes();

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<?> query = cb.createQuery(entity.getClass());
			Root<?> root = query.from(entityType);

			// FIXME this is wrong - we should only add the singular attributes that are marked as unique
			// see how Hibernate seedentity does this
			// and absolutely filter out id attribute
			
			// TODO see http://stackoverflow.com/questions/7077464/how-to-get-singularattribute-mapped-value-of-a-persistent-object
			// how to use the metamodel api to do this without beanutil 
			for (SingularAttribute a : singularAttributes) {
				query.where(cb.equal(root.get(a), propertyAccess.get(object, a.getName())));
			}
			List results = entityManager.createQuery(query).getResultList();

			if (results.size() > 0) {
				logger.info("At least one existing entity with same unique properties as '" + entity + "' of type '"
							+ entity.getClass().getSimpleName() + "' already exists, skipping seeding this entity");
				// Need to set the id to the seed bean so a new seed entity with a relationship to existing seed entity can be
				// saved.

				// Results should include only one object and we don't know any better which is the right object anyway
				// so use the first one

				Type idType = entityType.getIdType();
				SingularAttribute idAttr = entityType.getId(idType.getJavaType());
				propertyAccess.set(entity, idAttr.getName(), propertyAccess.get(results.get(0), idAttr.getName()));

				continue;
			}
			entityManager.persist(entity);
			// FIXME need to flush for latter persist() to "see" the previous calls  
			//em.flush();
		}
		tx.commit();
	}
}
