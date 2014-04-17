/**
 * The MIT License
 *
 * Copyright (c) 2012 Martin Crawford and contributors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.atreus.v0.impl.converters;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.atreus.v0.AtreusTypeConverterException;
import org.atreus.v0.AtreusTypeConverter;
import org.springframework.util.Assert;

public class TypeConverterRegistry {

	private Map<Class<?>, AtreusTypeConverter> converterCache = new HashMap<Class<?>, AtreusTypeConverter>();

	private List<AtreusTypeConverter> converters = new LinkedList<AtreusTypeConverter>();

	public void addConverter(AtreusTypeConverter converter) {
		converters.add(converter);
	}

	public void addDefaultConverters() {
		converters.add(new ByteArrayTypeConverter());
		converters.add(new BooleanTypeConverter());
		converters.add(new ByteTypeConverter());
		converters.add(new ShortTypeConverter());
		converters.add(new IntegerTypeConverter());
		converters.add(new LongTypeConverter());
		converters.add(new CharacterTypeConverter());
		converters.add(new StringTypeConverter());
		converters.add(new CalendarTypeConverter());
		converters.add(new DateTypeConverter());
		converters.add(new EnumTypeConverter());
	}

	public AtreusTypeConverter findConverter(Class<?> type) {
		Assert.notNull(type, "Class cannot be null");
		AtreusTypeConverter converter = converterCache.get(type);
		if (converter != null) {
			return converter;
		}
		return resolve(type);
	}

	public AtreusTypeConverter findConverter(Object value) {
		Assert.notNull(value, "Object cannot be null");
		return findConverter(value.getClass());
	}

	@SuppressWarnings("unchecked")
	public <T> T fromBytes(Class<T> type, byte[] bytes) {
		Assert.notNull(type, "Class cannot be null");
		if (bytes == null) {
			return null;
		}
		AtreusTypeConverter converter = findConverter(type);
		return (T) converter.fromBytes(type, bytes);
	}

	public List<AtreusTypeConverter> getConverters() {
		return converters;
	}

	public void removeConverter(AtreusTypeConverter converter) {
		converters.remove(converter);
	}

	private AtreusTypeConverter resolve(Class<?> type) {
		for (AtreusTypeConverter converter : converters) {
			if (converter.isSupported(type)) {
				converterCache.put(type, converter);
				return converter;
			}
		}
		throw new AtreusTypeConverterException("Cannot find a suitable type converter", type);
	}

	public void setConverters(List<AtreusTypeConverter> converters) {
		this.converters = converters;
	}

	public byte[] toBytes(Object value) {
		AtreusTypeConverter converter = findConverter(value);
		return converter.toBytes(value);
	}

}