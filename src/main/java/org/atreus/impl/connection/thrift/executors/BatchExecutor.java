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
import java.util.List;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Deletion;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.atreus.impl.commands.BatchCommand;
import org.atreus.impl.commands.BatchableCommand;
import org.atreus.impl.commands.Command;
import org.atreus.impl.commands.DeleteColumnCommand;
import org.atreus.impl.commands.DeleteRowCommand;
import org.atreus.impl.commands.WriteColumnCommand;
import org.atreus.impl.commands.WriteSubColumnCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchExecutor implements ThriftCommandExecutor {
	private static final Logger logger = LoggerFactory.getLogger(BatchExecutor.class);

	public Object execute(Client client, Command command, ConsistencyLevel consistencyLevel) throws InvalidRequestException, UnavailableException, TimedOutException,
			TTransportException, TException {
		BatchCommand batch = (BatchCommand) command;

		if (logger.isDebugEnabled()) {
			logger.debug("Executing batch [" + batch + "]");
		}
		ThriftMutations mutations = new ThriftMutations();
		long timeStamp = System.currentTimeMillis();
		for (List<BatchableCommand> subBatch : batch.getBatchList()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Dividing into sub-batch [" + subBatch + "]");
			}
			handleCommands(subBatch, mutations, timeStamp);
		}
		client.batch_mutate(mutations.getMutations(), consistencyLevel);
		return null;
	}

	private void handleCommands(List<BatchableCommand> batch, ThriftMutations mutations, long timeStamp) {
		BatchableCommand firstCommand = batch.get(0);
		if (firstCommand instanceof DeleteColumnCommand) {
			handleDeleteColumnCommand(batch, mutations, timeStamp);
			return;
		}
		if (firstCommand instanceof DeleteRowCommand) {
			handleDeleteRowCommand(batch, mutations, timeStamp);
			return;
		}
		if (firstCommand instanceof WriteColumnCommand) {
			handleWriteCommand(batch, mutations, timeStamp);
			return;
		}
		if (firstCommand instanceof WriteSubColumnCommand) {
			handleWriteSubCommand(batch, mutations, timeStamp);
			return;
		}
		throw new IllegalArgumentException("Unidentified Batchable Command type [" + firstCommand.getClass() + "]");
	}

	private void handleDeleteColumnCommand(List<BatchableCommand> batch, ThriftMutations mutations, long timeStamp) {
		BatchableCommand firstCommand = batch.get(0);
		String columnFamily = firstCommand.getColumnFamily();
		ByteBuffer rowKey = firstCommand.getRowKey();

		Deletion deletion = new Deletion();
		deletion.setTimestamp(timeStamp);
		SlicePredicate predicate = new SlicePredicate();
		for (BatchableCommand command : batch) {
			DeleteColumnCommand deleteColumn = (DeleteColumnCommand) command;
			predicate.addToColumn_names(deleteColumn.getColumnName());
		}
		deletion.setPredicate(predicate);
		Mutation mutation = new Mutation();
		mutation.setDeletion(deletion);
		mutations.add(columnFamily, rowKey, mutation);
	}

	private void handleDeleteRowCommand(List<BatchableCommand> batch, ThriftMutations mutations, long timeStamp) {
		BatchableCommand firstCommand = batch.get(0);
		String columnFamily = firstCommand.getColumnFamily();
		ByteBuffer rowKey = firstCommand.getRowKey();

		Deletion deletion = new Deletion();
		deletion.setTimestamp(timeStamp);
		Mutation mutation = new Mutation();
		mutation.setDeletion(deletion);
		mutations.add(columnFamily, rowKey, mutation);
	}

	private void handleWriteCommand(List<BatchableCommand> batch, ThriftMutations mutations, long timeStamp) {
		WriteColumnCommand firstCommand = (WriteColumnCommand) batch.get(0);
		String columnFamily = firstCommand.getColumnFamily();
		ByteBuffer rowKey = firstCommand.getRowKey();

		for (BatchableCommand command : batch) {
			WriteColumnCommand writeColumn = (WriteColumnCommand) command;
			ColumnOrSuperColumn colOrSuperCol = new ColumnOrSuperColumn();
			Column column = new Column();
			column.setName(writeColumn.getColumnName());
			column.setValue(writeColumn.getValue());
			column.setTimestamp(timeStamp);
			colOrSuperCol.setColumn(column);
			Mutation mutation = new Mutation();
			mutation.setColumn_or_supercolumn(colOrSuperCol);
			mutations.add(columnFamily, rowKey, mutation);
		}

	}

	private void handleWriteSubCommand(List<BatchableCommand> batch, ThriftMutations mutations, long timeStamp) {
		WriteSubColumnCommand firstCommand = (WriteSubColumnCommand) batch.get(0);
		String columnFamily = firstCommand.getColumnFamily();
		ByteBuffer rowKey = firstCommand.getRowKey();

		for (BatchableCommand command : batch) {
			WriteSubColumnCommand writeColumn = (WriteSubColumnCommand) command;
			ColumnOrSuperColumn colOrSuperCol = new ColumnOrSuperColumn();
			SuperColumn superColumn = new SuperColumn();
			superColumn.setName(writeColumn.getColumnName());
			Column column = new Column();
			column.setName(writeColumn.getSubColumnName());
			column.setValue(writeColumn.getValue());
			column.setTimestamp(timeStamp);
			superColumn.addToColumns(column);
			colOrSuperCol.setSuper_column(superColumn);
			Mutation mutation = new Mutation();
			mutation.setColumn_or_supercolumn(colOrSuperCol);
			mutations.add(columnFamily, rowKey, mutation);
		}

	}
}
