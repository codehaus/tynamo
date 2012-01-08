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
package org.tynamo.jdo;

import java.util.Collection;
import org.tynamo.jdo.internal.CommitAfterWorker;
import org.tynamo.jdo.internal.EntityPersistentFieldStrategy;
import org.tynamo.jdo.internal.JDOEntityValueEncoder;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.LoggerSource;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.services.*;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.spi.JDOImplHelper;

/**
 * Supplements the services defined by {@link eu.cuetech.tapestry.jdo.JDOCoreModule}
 * with additional services and configuration specific to Tapestry web
 * application.
 */
@SuppressWarnings({"JavaDoc"})
public class JDOModule {

    public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
        configuration.add(JDOSymbols.PROVIDE_ENTITY_VALUE_ENCODERS, "true");
    }

    /**
     * Contributes the following: <dl> <dt>entity</dt> <dd>Stores the id of the
     * entity and reloads from the {@link PersistenceManager}</dd> </dl>
     */
    public static void contributePersistentFieldManager(
            MappedConfiguration<String, PersistentFieldStrategy> configuration) {
        configuration.addInstance("entity", EntityPersistentFieldStrategy.class);
    }

    /**
     * Adds the CommitAfter annotation work, to process the
     * {@link org.tynamo.jdo.annotations.CommitAfter} annotation.
     */
    public static void contributeComponentClassTransformWorker(
            OrderedConfiguration<ComponentClassTransformWorker> configuration) {
        // If logging is enabled, we want logging to be the first advice, wrapping around the commit
        // advice.

        configuration.addInstance("CommitAfter", CommitAfterWorker.class, "after:Log");
    }

    /**
     * Contributes {@link ValueEncoderFactory}s for all JDO entity classes.
     * Encoding and decoding are based on the id property value of the entity
     * using type coercion. Hence, if the id can be coerced to a String and back
     * then the entity can be coerced.
     */
    @SuppressWarnings("unchecked")
    public static void contributeValueEncoderSource(MappedConfiguration<Class, ValueEncoderFactory> configuration,
            @Symbol(JDOSymbols.PROVIDE_ENTITY_VALUE_ENCODERS) boolean provideEncoders,
            final JDOPersistenceManagerSource pms, final PersistenceManager pm, final TypeCoercer typeCoercer,
            final PropertyAccess propertyAccess, final LoggerSource loggerSource) {
        if (!provideEncoders) {
            return;
        }

        pms.create();
        PersistenceManagerFactory pmf = pms.getPersistenceManagerFactory();

        Collection<Class> pcClasses = JDOImplHelper.getInstance().getRegisteredClasses();


        for (Class<?> pcClz : pcClasses) {

            final Class pcClass = pcClz;

            ValueEncoderFactory factory = new ValueEncoderFactory() {

                public ValueEncoder create(Class type) {
                    return new JDOEntityValueEncoder(pcClass, pm, propertyAccess, typeCoercer, loggerSource.getLogger(pcClass));
                }
            };
            configuration.add(pcClass, factory);
        }

    }
}
