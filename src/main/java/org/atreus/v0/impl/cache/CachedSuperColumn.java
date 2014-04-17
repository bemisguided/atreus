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
package org.atreus.v0.impl.cache;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class CachedSuperColumn extends CachedColumn {

	private final Map<ByteBuffer, CachedColumn> columns = new HashMap<ByteBuffer, CachedColumn>();

	public CachedSuperColumn(CachedRow parent, byte[] name) {
		super(parent, name);
	}

	@Override
	protected Iterable<? extends CachedReference> getChildren() {
		return columns.values();
	}

	public CachedColumn getColumn(byte[] name) {
		ByteBuffer nameBuffer = ByteBuffer.wrap(name);
		CachedColumn column = columns.get(nameBuffer);
		if (column == null) {
			column = new CachedColumn(this, name);
			columns.put(nameBuffer, column);
		}
		return column;
	}

	public Iterable<CachedColumn> getColumns() {
		return columns.values();
	}
}