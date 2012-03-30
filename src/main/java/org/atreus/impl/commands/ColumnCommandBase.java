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
package org.atreus.impl.commands;

import java.nio.ByteBuffer;

public abstract class ColumnCommandBase implements ColumnCommand {

	private final String columnFamily;

	private final ByteBuffer columnName;

	private final ByteBuffer rowKey;

	private final ByteBuffer subColumnName;

	public ColumnCommandBase(String columnFamily, byte[] rowKey, byte[] columnName, byte[] subColumnName) {
		this.columnFamily = columnFamily;
		if (rowKey != null) {
			this.rowKey = ByteBuffer.wrap(rowKey);
		} else {
			this.rowKey = null;
		}
		if (columnName != null) {
			this.columnName = ByteBuffer.wrap(columnName);
		} else {
			this.columnName = null;
		}
		if (subColumnName != null) {
			this.subColumnName = ByteBuffer.wrap(subColumnName);
		} else {
			this.subColumnName = null;
		}
	}

	public String getColumnFamily() {
		return columnFamily;
	}

	public ByteBuffer getColumnName() {
		return columnName;
	}

	public ByteBuffer getRowKey() {
		return rowKey;
	}

	public ByteBuffer getSubColumnName() {
		return subColumnName;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [columnFamily=" + columnFamily + ", columnName=" + columnName + ", rowKey=" + rowKey + ", subColumnName=" + subColumnName + "]";
	}
}
