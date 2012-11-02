package org.tynamo.model.elasticsearch.mapping.impl;

import java.lang.reflect.Field;
import java.util.Collection;

import org.tynamo.model.elasticsearch.annotations.ElasticSearchEmbedded;
import org.tynamo.model.elasticsearch.mapping.FieldMapper;
import org.tynamo.model.elasticsearch.mapping.MapperFactory;
import org.tynamo.model.elasticsearch.mapping.MappingException;

/**
 * Factory for {@link FieldMapper}s
 */
public class DefaultMapperFactory implements MapperFactory {
	/**
	 * Gets a {@link FieldMapper} for the specified field
	 *
	 * @param <M>
	 *          the model type
	 * @param field
	 *          the field
	 * @throws MappingException
	 *           in case of mapping problems
	 * @return the field mapper
	 */
	public FieldMapper getMapper(Field field) throws MappingException {

		return getMapper(field, null);

	}

	/**
	 * Gets a {@link FieldMapper} for the specified field, using a prefix in the index
	 *
	 * @param <M>
	 *          the model type
	 * @param field
	 *          the field
	 * @throws MappingException
	 *           in case of mapping problems
	 * @return the field mapper
	 */
	public FieldMapper getMapper(Field field, String prefix) throws MappingException {

		if (Collection.class.isAssignableFrom(field.getType())) {
			return new CollectionFieldMapper(this, field, prefix);

		} else if (field.isAnnotationPresent(ElasticSearchEmbedded.class)) {
			return new EmbeddedFieldMapper(this, field, prefix);

		} else {
			return new SimpleFieldMapper(field, prefix);

		}

	}

}
