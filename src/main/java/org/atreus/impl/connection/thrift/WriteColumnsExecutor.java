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
package org.atreus.impl.connection.thrift;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.atreus.impl.commands.Command;
import org.atreus.impl.commands.WriteColumnCommand;

public class WriteColumnsExecutor implements ThriftCommandExecutor {

	private Column buildColumn(WriteColumnCommand writeColumn) {
		Column column;
		if (writeColumn.getSubColumnName() != null) {
			column = new Column(writeColumn.getSubColumnName());
		} else {
			column = new Column(writeColumn.getColumnName());
		}
		column.setValue(writeColumn.getValue());
		column.setTimestamp(System.currentTimeMillis());
		return column;
	}

	private ColumnParent buildColumnParent(WriteColumnCommand writeColumn) {
		ColumnParent parent = new ColumnParent(writeColumn.getColumnFamily());
		if (writeColumn.getSubColumnName() != null) {
			parent.setSuper_column(writeColumn.getColumnName());
		}
		return parent;
	}

	@Override
	public Object execute(Client client, Command command, ConsistencyLevel consistencyLevel) throws InvalidRequestException, UnavailableException, TimedOutException,
			TTransportException, TException {
		WriteColumnCommand writeColumn = (WriteColumnCommand) command;
		ColumnParent parent = buildColumnParent(writeColumn);
		Column column = buildColumn(writeColumn);
		client.insert(writeColumn.getRowKey(), parent, column, consistencyLevel);
		return null;
	}

}