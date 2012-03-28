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
package org.atreus.impl;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.atreus.AtreusColumnMap;
import org.atreus.AtreusIllegalStateException;
import org.atreus.impl.converters.TypeConverterRegistry;

public abstract class AtreusColumnMapBase implements AtreusColumnMap {

	private boolean immutable;

	private final TypeConverterRegistry typeRegistry;

	protected AtreusColumnMapBase(TypeConverterRegistry typeRegistry) {
		this.typeRegistry = typeRegistry;
	}

	protected final void assertMutable() {
		if (immutable) {
			throw new AtreusIllegalStateException("Atreus Column Map is immutable");
		}
	}

	@Override
	public final Set<byte[]> columnNameSet() {
		Set<byte[]> result = new HashSet<byte[]>(getMap().size());
		for (ByteBuffer buffer : getMap().keySet()) {
			result.add(buffer.array());
		}
		return result;
	}

	@Override
	public final <T> Set<T> columnNameSet(Class<T> type) {
		Set<T> result = new HashSet<T>(getMap().size());
		for (ByteBuffer buffer : getMap().keySet()) {
			byte[] byteValue = buffer.array();
			result.add(fromBytes(type, byteValue));
		}
		return result;
	}

	protected AtreusIllegalStateException exceptionNotSupported() {
		if (hasSubColumns()) {
			return new AtreusIllegalStateException("Method not supported for an Atreus Column Map with Super Columns");
		}
		return new AtreusIllegalStateException("Method not supported for an Atreus Column Map without Super Columns");
	}

	@Override
	public final boolean existsColumn(byte[] columnName) {
		return getMap().containsKey(ByteBuffer.wrap(columnName));
	}

	@Override
	public final boolean existsColumn(Object columnName) {
		byte[] byteColumnName = toBytes(columnName);
		return existsColumn(byteColumnName);
	}

	@Override
	public final boolean existsValue(Object value) {
		byte[] byteValue = toBytes(value);
		return existsValue(byteValue);
	}

	protected final <T> T fromBytes(Class<T> type, byte[] bytes) {
		return typeRegistry.fromBytes(type, bytes);
	}

	@Override
	public final <T> T get(byte[] columnName, Class<T> type) {
		byte[] byteValue = getAsBytes(columnName);
		return fromBytes(type, byteValue);
	}

	@Override
	public final AtreusColumnMap get(Object columnName) {
		byte[] byteColumnName = toBytes(columnName);
		return get(byteColumnName);
	}

	@Override
	public final <T> T get(Object columnName, Class<T> type) {
		byte[] byteColumnName = toBytes(columnName);
		return get(byteColumnName, type);
	}

	@Override
	public final byte[] getAsBytes(Object columnName) {
		byte[] byteColumnName = toBytes(columnName);
		return getAsBytes(byteColumnName);
	}

	protected abstract Map<ByteBuffer, ?> getMap();

	protected TypeConverterRegistry getTypeRegistry() {
		return typeRegistry;
	}

	public final boolean isImmutable() {
		return immutable;
	}

	@Override
	public final void put(byte[] columnName, byte[] subColumnName, Object value) {
		byte[] byteValue = toBytes(value);
		put(columnName, subColumnName, byteValue);
	}

	@Override
	public final void put(byte[] columnName, Object value) {
		byte[] byteValue = toBytes(value);
		put(columnName, byteValue);
	}

	@Override
	public final void put(byte[] columnName, Object subColumnName, byte[] value) {
		byte[] byteSubColumn = toBytes(subColumnName);
		put(columnName, byteSubColumn, value);
	}

	@Override
	public final void put(byte[] columnName, Object subColumnName, Object value) {
		byte[] byteValue = toBytes(value);
		byte[] byteSubColumn = toBytes(subColumnName);
		put(columnName, byteSubColumn, byteValue);
	}

	@Override
	public final void put(Object columnName, byte[] value) {
		byte[] byteValue = toBytes(value);
		byte[] byteColumnName = toBytes(columnName);
		put(byteColumnName, byteValue);
	}

	@Override
	public final void put(Object columnName, byte[] subColumnName, byte[] value) {
		byte[] byteColumn = toBytes(columnName);
		put(byteColumn, subColumnName, value);
	}

	@Override
	public final void put(Object columnName, byte[] subColumnName, Object value) {
		byte[] byteValue = toBytes(value);
		byte[] byteColumnName = toBytes(columnName);
		put(byteColumnName, subColumnName, byteValue);
	}

	@Override
	public final void put(Object columnName, Object value) {
		byte[] byteValue = toBytes(value);
		byte[] byteColumnName = toBytes(columnName);
		put(byteColumnName, byteValue);
	}

	@Override
	public final void put(Object columnName, Object subColumnName, byte[] value) {
		byte[] byteColumnName = toBytes(columnName);
		byte[] byteSubColumn = toBytes(subColumnName);
		put(byteColumnName, byteSubColumn, value);
	}

	@Override
	public final void put(Object columnName, Object subColumnName, Object value) {
		byte[] byteValue = toBytes(value);
		byte[] byteColumnName = toBytes(columnName);
		byte[] byteSubColumn = toBytes(subColumnName);
		put(byteColumnName, byteSubColumn, byteValue);
	}

	public final void setImmutable(boolean immutable) {
		this.immutable = immutable;
	}

	@Override
	public final int size() {
		return getMap().size();
	}

	protected final byte[] toBytes(Object value) {
		return typeRegistry.toBytes(value);
	}
}
