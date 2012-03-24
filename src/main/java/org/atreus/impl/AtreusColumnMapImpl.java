/*
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

package org.atreus.impl;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.atreus.AtreusColumnMap;
import org.atreus.AtreusIllegalStateException;
import org.atreus.impl.converters.TypeConverterRegistry;

public class AtreusColumnMapImpl implements AtreusColumnMap {

	private boolean immutable;

	private final Map<ByteBuffer, ByteBuffer> map;

	private final TypeConverterRegistry typeRegistry;

	public AtreusColumnMapImpl(TypeConverterRegistry typeRegistry) {
		this.map = new HashMap<ByteBuffer, ByteBuffer>();
		this.typeRegistry = typeRegistry;
	}

	private void assertMutable() {
		if (immutable) {
			throw new AtreusIllegalStateException("Atreus Column Map is immutable");
		}
	}

	@Override
	public Set<byte[]> columnNameSet() {
		Set<byte[]> result = new HashSet<byte[]>(map.size());
		for (ByteBuffer buffer : map.keySet()) {
			result.add(buffer.array());
		}
		return result;
	}

	@Override
	public <T> Set<T> columnNameSet(Class<T> type) {
		Set<T> result = new HashSet<T>(map.size());
		for (ByteBuffer buffer : map.keySet()) {
			byte[] byteValue = buffer.array();
			result.add(fromBytes(type, byteValue));
		}
		return result;
	}

	@Override
	public boolean existsColumn(byte[] columnName) {
		return map.containsKey(ByteBuffer.wrap(columnName));
	}

	@Override
	public boolean existsColumn(Object columnName) {
		byte[] byteColumnName = toBytes(columnName);
		return existsColumn(byteColumnName);
	}

	@Override
	public boolean existsValue(byte[] value) {
		return map.containsValue(ByteBuffer.wrap(value));
	}

	@Override
	public boolean existsValue(Object value) {
		byte[] byteValue = toBytes(value);
		return existsValue(byteValue);
	}

	protected <T> T fromBytes(Class<T> type, byte[] bytes) {
		return typeRegistry.fromBytes(type, bytes);
	}

	@Override
	public <T> T get(byte[] columnName, Class<T> type) {
		byte[] byteValue = getAsBytes(columnName);
		return fromBytes(type, byteValue);
	}

	@Override
	public <T> T get(Object columnName, Class<T> type) {
		byte[] byteColumnName = toBytes(columnName);
		return get(byteColumnName, type);
	}

	@Override
	public byte[] getAsBytes(byte[] columnName) {
		ByteBuffer buffer = map.get(ByteBuffer.wrap(columnName));
		if (buffer == null) {
			return null;
		}
		return buffer.array();
	}

	@Override
	public byte[] getAsBytes(Object columnName) {
		byte[] byteColumnName = toBytes(columnName);
		return getAsBytes(byteColumnName);
	}

	@Override
	public boolean hasSubColumns() {
		return false;
	}

	public boolean isImmutable() {
		return immutable;
	}

	@Override
	public void put(byte[] columnName, byte[] value) {
		assertMutable();
		map.put(ByteBuffer.wrap(columnName), ByteBuffer.wrap(value));
	}

	@Override
	public void put(byte[] columnName, Object value) {
		assertMutable();
		byte[] byteValue = toBytes(value);
		put(columnName, byteValue);
	}

	@Override
	public void put(Object columnName, byte[] value) {
		assertMutable();
		byte[] byteColumnName = toBytes(columnName);
		put(byteColumnName, value);
	}

	@Override
	public void put(Object columnName, Object value) {
		assertMutable();
		byte[] byteColumnName = toBytes(columnName);
		byte[] byteValue = toBytes(value);
		put(byteColumnName, byteValue);
	}

	public void setImmutable(boolean immutable) {
		this.immutable = immutable;
	}

	@Override
	public int size() {
		return map.size();
	}

	protected byte[] toBytes(Object value) {
		return typeRegistry.toBytes(value);
	}

}
