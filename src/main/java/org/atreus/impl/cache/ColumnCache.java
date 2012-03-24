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

package org.atreus.impl.cache;

import java.util.HashMap;
import java.util.Map;

public class ColumnCache {

	private final Map<ColumnReference, CacheResult> columnCache = new HashMap<ColumnReference, CacheResult>();

	public ColumnCache() {
	}

	public CacheResult get(String columnFamily, byte[] rowKey, byte[] columnName) {
		return get(columnFamily, rowKey, columnName, null);
	}

	public CacheResult get(String columnFamily, byte[] rowKey, byte[] columnName, byte[] subColumnName) {
		ColumnReference reference = new ColumnReference(columnFamily, rowKey, columnName, subColumnName);
		return columnCache.get(reference);
	}

	public void put(String columnFamily, byte[] rowKey, byte[] columnName, byte[] subColumnName, byte[] value) {
		ColumnReference reference = new ColumnReference(columnFamily, rowKey, columnName, subColumnName);
		// columnCache.put(reference, );
	}
}
