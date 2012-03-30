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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandBatch {

	class BatchReference {

		private final String columnFamily;

		private final ByteBuffer rowKey;

		private final Class<?> type;

		BatchReference(String columnFamily, ByteBuffer rowKey, Class<?> type) {
			this.columnFamily = columnFamily;
			this.rowKey = rowKey;
			this.type = type;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BatchReference other = (BatchReference) obj;
			if (columnFamily == null) {
				if (other.columnFamily != null)
					return false;
			} else if (!columnFamily.equals(other.columnFamily))
				return false;
			if (rowKey == null) {
				if (other.rowKey != null)
					return false;
			} else if (!rowKey.equals(other.rowKey))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((columnFamily == null) ? 0 : columnFamily.hashCode());
			result = prime * result + ((rowKey == null) ? 0 : rowKey.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

	}

	private Map<BatchReference, List<BatchableCommand>> batchList = new HashMap<CommandBatch.BatchReference, List<BatchableCommand>>();

	private boolean open = false;

	public void addCommand(BatchableCommand command) {
		BatchReference ref = new BatchReference(command.getColumnFamily(), command.getRowKey(), command.getClass());
		List<BatchableCommand> list = batchList.get(ref);
		if (list == null) {
			list = new LinkedList<BatchableCommand>();
			batchList.put(ref, list);
		}
		list.add(command);
		open = true;
	}

	public List<List<BatchableCommand>> getBatchList() {
		List<List<BatchableCommand>> result = new LinkedList<List<BatchableCommand>>();
		for (List<BatchableCommand> list : batchList.values()) {
			result.add(list);
		}
		return result;
	}

	public boolean isOpen() {
		return open;
	}

	@Override
	public String toString() {
		return "CommandBatch [batchList=" + batchList + ", open=" + open + "]";
	}
}
