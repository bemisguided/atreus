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
package org.atreus.impl.connection.thrift.executors;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Mutation;

public class ThriftMutations {

	private Map<ByteBuffer, Map<String, List<Mutation>>> mutations = new HashMap<ByteBuffer, Map<String, List<Mutation>>>();

	public void add(String columnFamily, ByteBuffer rowKey, Mutation mutation) {
		Map<String, List<Mutation>> map = getRowKey(rowKey);
		List<Mutation> list = getColumnFamily(columnFamily, map);
		list.add(mutation);
	}

	private List<Mutation> getColumnFamily(String columnFamily, Map<String, List<Mutation>> map) {
		List<Mutation> result = map.get(columnFamily);
		if (result != null) {
			return result;
		}
		result = new LinkedList<Mutation>();
		map.put(columnFamily, result);
		return result;
	}

	protected Map<ByteBuffer, Map<String, List<Mutation>>> getMutations() {
		return mutations;
	}

	private Map<String, List<Mutation>> getRowKey(ByteBuffer rowKey) {
		Map<String, List<Mutation>> result = mutations.get(rowKey);
		if (result != null) {
			return result;
		}
		result = new HashMap<String, List<Mutation>>();
		mutations.put(rowKey, result);
		return result;
	}
}
