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

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.Compression;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.CqlResult;
import org.apache.cassandra.thrift.CqlResultType;
import org.apache.cassandra.thrift.CqlRow;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.SchemaDisagreementException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.atreus.AtreusCommandException;
import org.atreus.impl.AtreusColumnMapImpl;
import org.atreus.impl.AtreusRowListImpl;
import org.atreus.impl.commands.Command;
import org.atreus.impl.commands.CqlCommand;

public class CqlExecutor implements ThriftCommandExecutor {

	protected CqlResult doExecutes(Client client, CqlCommand command) throws InvalidRequestException, UnavailableException, TimedOutException, TTransportException, TException {
		try {
			return client.execute_cql_query(command.getCqlStatementAsByteBuffer(), Compression.NONE);
		} catch (SchemaDisagreementException e) {
			throw new AtreusCommandException("CQL statement could not be executed", e);
		}
	}

	@Override
	public Object execute(Client client, Command command, ConsistencyLevel consistencyLevel) throws InvalidRequestException, UnavailableException, TimedOutException,
			TTransportException, TException {
		CqlCommand cql = (CqlCommand) command;
		CqlResult cqlResult = doExecutes(client, cql);

		// Does the command request a result set?
		if (!cql.isResultSet()) {
			// No, then just return null
			return null;
		}

		// Does the result have rows?
		if (!CqlResultType.ROWS.equals(cqlResult.getType())) {
			// No then just return an empty row list
			return new AtreusRowListImpl(0);
		}

		// Otherwise build the row list results
		AtreusRowListImpl rowList = new AtreusRowListImpl(cqlResult.getRowsSize());

		for (CqlRow cqlRow : cqlResult.getRows()) {
			AtreusColumnMapImpl columnMap = new AtreusColumnMapImpl(cql.getSession());
			rowList.addColumnMap(columnMap);
			columnMap.setRowKey(cqlRow.getKey());
			for (Column column : cqlRow.getColumns()) {
				columnMap.put(column.getName(), column.getValue());
			}
			columnMap.setImmutable(true);
		}
		return rowList;

	}

}
