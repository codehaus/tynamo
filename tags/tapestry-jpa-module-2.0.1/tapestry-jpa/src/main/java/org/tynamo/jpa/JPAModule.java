// Copyright 2007, 2008 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.tynamo.jpa;

import org.tynamo.jpa.internal.CommitAfterWorker;
import org.tynamo.jpa.internal.EntityPersistentFieldStrategy;
import org.tynamo.jpa.internal.JPAEntityValueEncoder;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.LoggerSource;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.services.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

/**
 * Supplements the services defined by {@link eu.cuetech.tapestry.jpa.JPACoreModule} with additional
 * services and configuration specific to Tapestry web application.
 */
@SuppressWarnings({"JavaDoc"})
public class JPAModule {
	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(JPASymbols.PROVIDE_ENTITY_VALUE_ENCODERS, "true");
	}

	/**
	 * Contributes the package "&lt;root&gt;.entities" to the configuration, so that it will be
	 * scanned for annotated entity classes.
	 * <p/>
	 * public static void contributeHibernateEntityPackageManager(Configuration<String>
	 * configuration,
	 *
	 * @Inject
	 * @Symbol(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM) String appRootPackage) {
	 * configuration.add(appRootPackage +
	 * ".entities"); }
	 */

	public static void contributeAlias(Configuration<AliasContribution> configuration, @JPACore EntityManager em) {
		configuration.add(AliasContribution.create(EntityManager.class, em));
	}

	/**
	 * Contributes the following:
	 * <dl>
	 * <dt>entity</dt>
	 * <dd>Stores the id of the entity and reloads from the {@link EntityManager}</dd>
	 * </dl>
	 */
	public static void contributePersistentFieldManager(
			MappedConfiguration<String, PersistentFieldStrategy> configuration) {
		configuration.addInstance("entity", EntityPersistentFieldStrategy.class);
	}

	/**
	 * Adds the CommitAfter annotation work, to process the
	 * {@link eu.cuetech.tapestry.jpa.annotations.CommitAfter} annotation.
	 */
	public static void contributeComponentClassTransformWorker(
			OrderedConfiguration<ComponentClassTransformWorker> configuration) {
		// If logging is enabled, we want logging to be the first advice, wrapping around the commit
		// advice.

		configuration.addInstance("CommitAfter", CommitAfterWorker.class, "after:Log");
	}

	/**
	 * Contribution to the {@link org.apache.tapestry5.services.ComponentClassResolver} service
	 * configuration.
	 */
	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping("jpa", "org.tynamo.tapestry.jpa"));
	}

	/**
	 * Contributes {@link ValueEncoderFactory}s for all registered JPA entity classes. Encoding and
	 * decoding are based on the id property value of the entity using type coercion. Hence, if the
	 * id can be coerced to a String and back then the entity can be coerced.
	 */
	@SuppressWarnings("unchecked")
	public static void contributeValueEncoderSource(MappedConfiguration<Class, ValueEncoderFactory> configuration,
													@Symbol(JPASymbols.PROVIDE_ENTITY_VALUE_ENCODERS) boolean provideEncoders,
													final JPAEntityManagerSource ems, final EntityManager em, final TypeCoercer typeCoercer,
													final PropertyAccess propertyAccess, final LoggerSource loggerSource) {
		if (!provideEncoders)
			return;

		ems.create(); // create
		EntityManagerFactory emf = ems.getEntityManagerFactory();
		Metamodel metamodel = emf.getMetamodel();
		for (EntityType<?> et : metamodel.getEntities()) {

			final EntityType<?> etype = et;
			ValueEncoderFactory factory = new ValueEncoderFactory() {
				public ValueEncoder create(Class type) {
					return new JPAEntityValueEncoder(etype.getJavaType(), em, propertyAccess, typeCoercer, loggerSource
							.getLogger(etype.getJavaType()));
				}
			};

			configuration.add(etype.getJavaType(), factory);
		}

	}

}
