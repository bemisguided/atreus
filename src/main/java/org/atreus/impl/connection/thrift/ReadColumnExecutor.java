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

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.atreus.impl.commands.Command;
import org.atreus.impl.commands.ReadColumnCommand;

public class ReadColumnExecutor implements ThriftCommandExecutor {

	@Override
	public Object execute(Client client, Command command, ConsistencyLevel consistencyLevel) throws InvalidRequestException, UnavailableException, TimedOutException,
			TTransportException, TException {
		ReadColumnCommand readColumn = (ReadColumnCommand) command;

		ColumnPath path = new ColumnPath(readColumn.getColumnFamily());
		if (readColumn.getSubColumnName() == null) {
			path.setColumn(readColumn.getColumnName());
		} else {
			path.setSuper_column(readColumn.getColumnName());
			path.setColumn(readColumn.getSubColumnName());
		}
		try {
			ColumnOrSuperColumn result = client.get(readColumn.getRowKey(), path, consistencyLevel);
			return result.getColumn().getValue();
		} catch (NotFoundException e) {
			// Not found for now return null
		}
		return null;
	}

}