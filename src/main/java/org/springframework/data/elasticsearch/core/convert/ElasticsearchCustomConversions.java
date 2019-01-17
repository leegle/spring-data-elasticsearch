/*
 * Copyright 2018-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.elasticsearch.core.convert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchSimpleTypes;
import org.springframework.util.NumberUtils;

/**
 * Elasticsearch specific {@link CustomConversions}.
 *
 * @author Christoph Strobl
 * @since 3.2
 */
public class ElasticsearchCustomConversions extends CustomConversions {

	private static final StoreConversions STORE_CONVERSIONS;
	private static final List<Object> STORE_CONVERTERS;

	static {

		List<Object> converters = new ArrayList<>();
		converters.addAll(GeoConverters.getConvertersToRegister());
		converters.add(new StringToUUIDConverter());
		converters.add(new UUIDToStringConverter());
		converters.add(new BigDecimalToDoubleConverter());
		converters.add(new DoubleToBigDecimalConverter());

		STORE_CONVERTERS = Collections.unmodifiableList(converters);
		STORE_CONVERSIONS = StoreConversions.of(ElasticsearchSimpleTypes.HOLDER, STORE_CONVERTERS);
	}

	/**
	 * Creates a new {@link CustomConversions} instance registering the given converters.
	 *
	 * @param converters must not be {@literal null}.
	 */
	public ElasticsearchCustomConversions(Collection<?> converters) {
		super(STORE_CONVERSIONS, converters);
	}

	@ReadingConverter
	static class StringToUUIDConverter implements Converter<String, UUID> {

		@Override
		public UUID convert(String source) {
			return UUID.fromString(source);
		}
	}

	@WritingConverter
	static class UUIDToStringConverter implements Converter<UUID, String> {

		@Override
		public String convert(UUID source) {
			return source.toString();
		}
	}

	@ReadingConverter
	static class DoubleToBigDecimalConverter implements Converter<Double, BigDecimal> {

		@Override
		public BigDecimal convert(Double source) {
			return NumberUtils.convertNumberToTargetClass(source, BigDecimal.class);
		}
	}

	@WritingConverter
	static class BigDecimalToDoubleConverter implements Converter<BigDecimal, Double> {

		@Override
		public Double convert(BigDecimal source) {
			return NumberUtils.convertNumberToTargetClass(source, Double.class);
		}
	}
}
