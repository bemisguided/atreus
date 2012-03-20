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

import java.util.Arrays;

public class ColumnReference {

	private final String columnFamily;

	private final byte[] rowKey;

	private final byte[] columnName;

	private final byte[] subColumnName;

	public ColumnReference(String columnFamily, byte[] rowKey, byte[] columnName) {
		this.columnFamily = columnFamily;
		this.rowKey = rowKey;
		this.columnName = columnName;
		this.subColumnName = null;
	}

	public ColumnReference(String columnFamily, byte[] rowKey, byte[] columnName, byte[] subColumnName) {
		this.columnFamily = columnFamily;
		this.rowKey = rowKey;
		this.columnName = columnName;
		this.subColumnName = subColumnName;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnReference other = (ColumnReference) obj;
		if (columnFamily == null) {
			if (other.columnFamily != null)
				return false;
		} else if (!columnFamily.equals(other.columnFamily))
			return false;
		if (!Arrays.equals(columnName, other.columnName))
			return false;
		if (!Arrays.equals(rowKey, other.rowKey))
			return false;
		if (!Arrays.equals(subColumnName, other.subColumnName))
			return false;
		return true;
	}

	public String getColumnFamily() {
		return columnFamily;
	}

	public byte[] getColumnName() {
		return columnName;
	}

	public byte[] getRowKey() {
		return rowKey;
	}

	public byte[] getSubColumnName() {
		return subColumnName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columnFamily == null) ? 0 : columnFamily.hashCode());
		result = prime * result + Arrays.hashCode(columnName);
		result = prime * result + Arrays.hashCode(rowKey);
		result = prime * result + Arrays.hashCode(subColumnName);
		return result;
	}

}
