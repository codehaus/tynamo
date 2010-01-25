package org.tynamo.seedentity.jpa.services;

import org.apache.tapestry5.ioc.annotations.EagerLoad;
import org.slf4j.Logger;
import org.tynamo.jpa.JPAEntityManagerSource;
import org.tynamo.jpa.JPATransactionManager;
import org.tynamo.seedentity.jpa.SeedEntityIdentifier;
import org.tynamo.seedentity.jpa.tools.BeanUtil;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

@EagerLoad
public class SeedEntityImpl implements SeedEntity {
	@SuppressWarnings("unchecked")
	public SeedEntityImpl(Logger logger, JPAEntityManagerSource entityManagerSource, JPATransactionManager transactionManager, List<Object> entities) throws InvocationTargetException, NoSuchMethodException {
		EntityManager em = transactionManager.getEntityManager();
		EntityManagerFactory sessionFactory = entityManagerSource.getEntityManagerFactory();
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

			Metamodel metamodel = em.getMetamodel();
			EntityType entityType = metamodel.entity(entity.getClass());
			Set<SingularAttribute> singularAttributes = entityType.getSingularAttributes();

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<?> query = cb.createQuery(entity.getClass());
			Root<?> root = query.from(entityType);

			for (SingularAttribute a : singularAttributes) {
				query.where(cb.equal(root.get(a), BeanUtil.getProperty(object, a.getName())));
			}
			List results = em.createQuery(query).getResultList();

			if (results.size() > 0) {
				logger.info("At least one existing entity with same unique properties as '" + entity + "' of type '"
							+ entity.getClass().getSimpleName() + "' already exists, skipping seeding this entity");
				// Need to set the id to the seed bean so a new seed entity with a relationship to existing seed entity can be
				// saved.

				// Results should include only one object and we don't know any better which is the right object anyway
				// so use the first one

				Type idType = entityType.getIdType();
				SingularAttribute idAttr = entityType.getId(idType.getJavaType());
				BeanUtil.setProperty(entity, idAttr.getName(), BeanUtil.getProperty(results.get(0), idAttr.getName()));

				continue;
			}
			em.persist(entity);
		}
		transactionManager.commit();
	}
}
