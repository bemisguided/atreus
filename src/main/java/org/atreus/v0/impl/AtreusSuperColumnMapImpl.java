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
package org.atreus.v0.impl;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.atreus.v0.AtreusColumnMap;

public class AtreusSuperColumnMapImpl extends AtreusColumnMapBase {

	private final Map<ByteBuffer, AtreusColumnMap> map;

	public AtreusSuperColumnMapImpl(AtreusSessionImpl session) {
		super(session);
		this.map = new HashMap<ByteBuffer, AtreusColumnMap>();
	}

	@Override
	protected boolean existsValue(byte[] columnName) {
		throw exceptionNotSupported();
	}

	@Override
	protected AtreusColumnMap get(byte[] columnName) {
		ByteBuffer columnNameBuff = ByteBuffer.wrap(columnName);
		AtreusColumnMap result = map.get(columnNameBuff);
		if (result != null) {
			return result;
		}
		if (isImmutable()) {
			return null;
		}
		result = new AtreusColumnMapImpl(getSession());
		map.put(columnNameBuff, result);
		return result;
	}

	@Override
	protected byte[] getAsBytes(byte[] columnName) {
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
	protected void put(byte[] columnName, byte[] value) {
		throw exceptionNotSupported();
	}

	@Override
	protected void put(byte[] columnName, byte[] subColumnName, byte[] value) {
		assertMutable();
		AtreusColumnMap subColumns = get(columnName);
		subColumns.put(subColumnName, value);
	}

}