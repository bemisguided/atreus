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
package org.atreus.v0.impl.connection.thrift.executors;

import java.util.List;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.atreus.v0.impl.AtreusColumnMapBase;
import org.atreus.v0.impl.AtreusColumnMapImpl;
import org.atreus.v0.impl.AtreusSuperColumnMapImpl;
import org.atreus.v0.impl.commands.Command;
import org.atreus.v0.impl.commands.ReadMultipleColumnsCommand;

public class ReadMultipleColumnsExecutor implements ThriftCommandExecutor {

	@Override
	public Object execute(Client client, Command command, ConsistencyLevel consistencyLevel) throws InvalidRequestException, UnavailableException, TimedOutException,
			TTransportException, TException {
		ReadMultipleColumnsCommand readMultipleColumns = (ReadMultipleColumnsCommand) command;
		ColumnParent parent = new ColumnParent();
		parent.setColumn_family(readMultipleColumns.getColumnFamily());
		SlicePredicate predicate = new SlicePredicate();
		SliceRange range = new SliceRange();
		range.setStart(new byte[0]);
		range.setFinish(new byte[0]);
		predicate.setSlice_range(range);
		List<ColumnOrSuperColumn> list = client.get_slice(readMultipleColumns.getRowKeyAsByteBuffer(), parent, predicate, consistencyLevel);

		AtreusColumnMapBase result = new AtreusColumnMapImpl(readMultipleColumns.getSession());
		if (list.size() > 0 && list.get(0).isSetSuper_column()) {
			result = new AtreusSuperColumnMapImpl(readMultipleColumns.getSession());
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
		result.setRowKey(readMultipleColumns.getRowKeyAsBytes());
		result.setImmutable(true);
		return result;
	}

}
