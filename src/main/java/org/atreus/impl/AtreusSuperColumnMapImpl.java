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
import java.util.HashMap;
import java.util.Map;

import org.atreus.AtreusColumnMap;
import org.atreus.impl.converters.TypeConverterRegistry;

public class AtreusSuperColumnMapImpl extends AtreusColumnMapBase {

	private final Map<ByteBuffer, AtreusColumnMap> map;

	public AtreusSuperColumnMapImpl(TypeConverterRegistry typeRegistry) {
		super(typeRegistry);
		this.map = new HashMap<ByteBuffer, AtreusColumnMap>();
	}

	@Override
	public boolean existsValue(byte[] columnName) {
		throw exceptionNotSupported();
	}

	@Override
	public AtreusColumnMap get(byte[] columnName) {
		ByteBuffer columnNameBuff = ByteBuffer.wrap(columnName);
		AtreusColumnMap result = map.get(columnNameBuff);
		if (result != null) {
			return result;
		}
		if (isImmutable()) {
			return null;
		}
		result = new AtreusColumnMapImpl(getTypeRegistry());
		map.put(columnNameBuff, result);
		return result;
	}

	@Override
	public byte[] getAsBytes(byte[] columnName) {
		throw exceptionNotSupported();
	}

	@Override
	protected Map<ByteBuffer, ?> getMap() {
		return map;
	}

	@Override
	public boolean hasSubColumns() {
		return true;
	}

	@Override
	public void put(byte[] columnName, byte[] value) {
		throw exceptionNotSupported();
	}

	@Override
	public void put(byte[] columnName, byte[] subColumnName, byte[] value) {
		assertMutable();
		AtreusColumnMap subColumns = get(columnName);
		subColumns.put(subColumnName, value);
	}

}
