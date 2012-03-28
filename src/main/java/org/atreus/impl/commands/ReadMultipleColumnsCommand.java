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

import java.util.List;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.SuperColumn;
import org.atreus.AtreusColumnMap;
import org.atreus.impl.AtreusColumnMapImpl;
import org.atreus.impl.AtreusSuperColumnMapImpl;
import org.atreus.impl.converters.TypeConverterRegistry;

public class ReadMultipleColumnsCommand extends ColumnCommandBase implements ReadCommand {

	private final TypeConverterRegistry typeRegistry;

	public ReadMultipleColumnsCommand(TypeConverterRegistry typeRegistry, String columnFamily, byte[] rowKey, ConsistencyLevel consistencyLevel) {
		super(columnFamily, rowKey, null, null, consistencyLevel);
		this.typeRegistry = typeRegistry;
	}

	@Override
	public Object execute(Client client) throws Exception {
		ColumnParent parent = new ColumnParent();
		parent.setColumn_family(getColumnFamily());
		SlicePredicate predicate = new SlicePredicate();
		SliceRange range = new SliceRange();
		range.setStart(new byte[0]);
		range.setFinish(new byte[0]);
		predicate.setSlice_range(range);
		List<ColumnOrSuperColumn> list = client.get_slice(getRowKey(), parent, predicate, getConsistencyLevel());

		AtreusColumnMap result = new AtreusColumnMapImpl(typeRegistry);
		if (list.size() > 0 && list.get(0).isSetSuper_column()) {
			result = new AtreusSuperColumnMapImpl(typeRegistry);
		}
		for (ColumnOrSuperColumn colOrSuper : list) {
			if (colOrSuper.isSetSuper_column()) {
				SuperColumn superColumn = colOrSuper.getSuper_column();
				for (Column column : superColumn.getColumns()) {
					result.put(superColumn.getName(), column.getName(), column.getValue());
				}
			} else {
				Column column = colOrSuper.getColumn();
				result.put(column.getName(), column.getValue());
			}
		}
		return result;
	}
}
