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
package org.atreus.impl;

import org.atreus.AtreusColumnMap;
import org.atreus.AtreusConsistencyLevel;
import org.atreus.AtreusDisconnectedException;
import org.atreus.AtreusIllegalStateException;
import org.atreus.AtreusRowList;
import org.atreus.AtreusSession;
import org.atreus.AtreusSessionClosedException;
import org.atreus.impl.commands.BatchCommand;
import org.atreus.impl.commands.BatchableCommand;
import org.atreus.impl.commands.Command;
import org.atreus.impl.commands.CqlCommand;
import org.atreus.impl.commands.CqlQueryCommand;
import org.atreus.impl.commands.DeleteColumnCommand;
import org.atreus.impl.commands.DeleteRowCommand;
import org.atreus.impl.commands.ReadColumnCommand;
import org.atreus.impl.commands.ReadMultipleColumnsCommand;
import org.atreus.impl.commands.WriteColumnCommand;
import org.atreus.impl.commands.WriteSubColumnCommand;
import org.atreus.impl.connection.ConnectionManager;
import org.atreus.impl.converters.TypeConverterRegistry;
import org.atreus.impl.utils.AssertUtils;

public class AtreusSessionImpl implements AtreusSession {

	private String columnFamily;

	private boolean batchWriting;

	private BatchCommand commandBatch;

	private boolean caching;

	private boolean eagerFetching;

	private boolean open = true;

	private AtreusConsistencyLevel readConsistencyLevel;

	private Object rowKey;

	private final AtreusSessionFactoryImpl sessionFactory;

	private AtreusConsistencyLevel writeConsistencyLevel;

	AtreusSessionImpl(AtreusSessionFactoryImpl sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private void assertFamilyAndKey() {
		AssertUtils.hasText(getColumnFamily(), "No column family defined");
		AssertUtils.notNull(getRowKey(), "No row key defined");
	}

	private void assertIsReady() {
		if (!isOpen()) {
			throw new AtreusSessionClosedException();
		}
		if (!sessionFactory.isConnected()) {
			throw new AtreusDisconnectedException();
		}
	}

	@Override
	public void close() {
		assertIsReady();
		open = false;
	}

	@Override
	public void deleteColumn(Object colName) {
		assertIsReady();
		assertFamilyAndKey();
		AssertUtils.notNull(colName, "Column Name is a required parameter");

		DeleteColumnCommand command = new DeleteColumnCommand(this);
		command.setColumnFamily(getColumnFamily());
		command.setRowKey(getRowKey());
		command.setColumnName(colName);
		doExecuteOrBatch(command, getWriteConsistencyLevel());
	}

	@Override
	public void deleteColumn(Object colName, Object subColName) {
		assertIsReady();
		assertFamilyAndKey();
		AssertUtils.notNull(colName, "Column Name is a required parameter");
		AssertUtils.notNull(subColName, "Sub Column Name is a required parameter");

		DeleteColumnCommand command = new DeleteColumnCommand(this);
		command.setColumnFamily(getColumnFamily());
		command.setRowKey(getRowKey());
		command.setColumnName(colName);
		command.setSubColumnName(subColName);
		doExecuteOrBatch(command, getWriteConsistencyLevel());
	}

	@Override
	public void deleteRow() {
		assertFamilyAndKey();

		deleteRow(getColumnFamily(), getRowKey());
	}

	@Override
	public void deleteRow(String colFamily, Object rowKey) {
		assertIsReady();
		AssertUtils.hasText(colFamily, "Column family is a required parameter");
		AssertUtils.notNull(rowKey, "Row key is a required parameter");

		DeleteRowCommand command = new DeleteRowCommand(this);
		command.setColumnFamily(colFamily);
		command.setRowKey(rowKey);
		doExecuteOrBatch(command, getWriteConsistencyLevel());
	}

	protected Object doExecute(Command command, AtreusConsistencyLevel consistencyLevel) {
		return getConnectionManager().execute(command, consistencyLevel);
	}

	protected void doExecuteOrBatch(BatchableCommand command, AtreusConsistencyLevel consistencyLevel) {
		if (isBatchWriting()) {
			if (commandBatch == null) {
				commandBatch = new BatchCommand();
			}
			commandBatch.addCommand(command);
			return;
		}
		doExecute(command, consistencyLevel);
	}

	@Override
	public void execute(String cql) {
		assertIsReady();
		AssertUtils.hasText(cql, "CQL parameter is required");

		CqlCommand command = new CqlCommand(this);
		command.setCqlStatement(cql);
		doExecute(command, getWriteConsistencyLevel());
	}

	@Override
	public boolean existsColumn(Object colName) {
		assertIsReady();
		byte[] value = readColumnAsBytes(colName);
		return value != null;
	}

	@Override
	public boolean existsColumn(Object colName, Object subColName) {
		assertIsReady();
		byte[] value = readColumnAsBytes(colName, subColName);
		return value != null;
	}

	@Override
	public boolean existsRow() {
		assertIsReady();
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean existsRow(Object rowKey) {
		assertIsReady();
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void flush() {
		assertIsReady();
		if (!isBatchWriting()) {
			return;
		}
		doExecute(commandBatch, getWriteConsistencyLevel());
		commandBatch = null;
	}

	public <T> T fromBytes(Class<T> type, byte[] bytes) {
		return sessionFactory.fromBytes(type, bytes);
	}

	@Override
	public String getColumnFamily() {
		return columnFamily;
	}

	private ConnectionManager getConnectionManager() {
		return sessionFactory.getConnectionManager();
	}

	@Override
	public AtreusConsistencyLevel getReadConsistencyLevel() {
		return readConsistencyLevel;
	}

	@Override
	public Object getRowKey() {
		return rowKey;
	}

	protected TypeConverterRegistry getTypeRegistry() {
		return sessionFactory.getTypeRegistry();
	}

	@Override
	public AtreusConsistencyLevel getWriteConsistencyLevel() {
		return writeConsistencyLevel;
	}

	@Override
	public boolean isBatchWriting() {
		return batchWriting;
	}

	@Override
	public boolean isCaching() {
		return caching;
	}

	@Override
	public boolean isEagerFetching() {
		return eagerFetching;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public AtreusColumnMap newColumnMap() {
		return newColumnMap(false);
	}

	@Override
	public AtreusColumnMap newColumnMap(boolean superColumns) {
		if (superColumns) {
			return new AtreusSuperColumnMapImpl(this);
		}
		return new AtreusColumnMapImpl(this);
	}

	@Override
	public AtreusRowList query(String cql) {
		assertIsReady();
		AssertUtils.hasText(cql, "CQL parameter is required");
		CqlQueryCommand command = new CqlQueryCommand(this);
		command.setCqlStatement(cql);
		return (AtreusRowList) doExecute(command, getReadConsistencyLevel());
	}

	@Override
	public <T> T readColumn(Object colName, Class<T> type) {
		AssertUtils.notNull(type, "Type is a required parameter");
		byte[] value = readColumnAsBytes(colName);
		return fromBytes(type, value);
	}

	@Override
	public <T> T readColumn(Object colName, Object subColName, Class<T> type) {
		byte[] value = readColumnAsBytes(colName, subColName);
		return fromBytes(type, value);
	}

	@Override
	public byte[] readColumnAsBytes(Object colName) {
		assertIsReady();
		AssertUtils.notNull(colName, "Column Name is a required parameter");

		ReadColumnCommand command = new ReadColumnCommand(this);
		command.setColumnFamily(getColumnFamily());
		command.setRowKey(getRowKey());
		command.setColumnName(colName);
		return (byte[]) doExecute(command, getReadConsistencyLevel());
	}

	@Override
	public byte[] readColumnAsBytes(Object colName, Object subColName) {
		assertIsReady();
		assertFamilyAndKey();
		AssertUtils.notNull(colName, "Column Name is a required parameter");
		AssertUtils.notNull(subColName, "Sub Column Name is a required parameter");

		ReadColumnCommand command = new ReadColumnCommand(this);
		command.setColumnFamily(getColumnFamily());
		command.setRowKey(getRowKey());
		command.setColumnName(colName);
		command.setSubColumnName(subColName);
		return (byte[]) doExecute(command, getReadConsistencyLevel());
	}

	@Override
	public AtreusColumnMap readColumns() {
		assertIsReady();
		assertFamilyAndKey();
		return readColumns(getColumnFamily(), getRowKey());
	}

	@Override
	public AtreusColumnMap readColumns(String colFamily, Object rowKey) {
		assertIsReady();
		AssertUtils.hasText(colFamily, "Column family is a required parameter");
		AssertUtils.notNull(rowKey, "Row key is a required parameter");

		ReadMultipleColumnsCommand command = new ReadMultipleColumnsCommand(this);
		command.setColumnFamily(getColumnFamily());
		command.setRowKey(rowKey);
		return (AtreusColumnMap) doExecute(command, getReadConsistencyLevel());
	}

	@Override
	public void setBatchWriting(boolean batchWriting) {
		assertIsReady();
		if (commandBatch != null && !batchWriting) {
			throw new AtreusIllegalStateException("Session has an open batch that must be flushed before turning off batch write");
		}
		this.batchWriting = batchWriting;
	}

	@Override
	public void setCaching(boolean caching) {
		assertIsReady();
		this.caching = caching;
	}

	@Override
	public void setColumnFamily(String colFamily) {
		assertIsReady();
		this.columnFamily = colFamily;
	}

	@Override
	public void setEagerFetching(boolean eagerFetching) {
		assertIsReady();
		this.eagerFetching = eagerFetching;
	}

	@Override
	public void setFamilyAndKey(String colFamily, Object rowKey) {
		assertIsReady();
		this.columnFamily = colFamily;
		this.rowKey = rowKey;
	}

	@Override
	public void setReadConsistencyLevel(AtreusConsistencyLevel readConsistencyLevel) {
		assertIsReady();
		this.readConsistencyLevel = readConsistencyLevel;
	}

	@Override
	public void setRowKey(Object rowKey) {
		assertIsReady();
		this.rowKey = rowKey;
	}

	@Override
	public void setWriteConsistencyLevel(AtreusConsistencyLevel writeConsistencyLevel) {
		assertIsReady();
		this.writeConsistencyLevel = writeConsistencyLevel;
	}

	public byte[] toBytes(Object value) {
		return sessionFactory.toBytes(value);
	}

	@Override
	public void writeColumn(Object colName) {
		writeColumn(colName, new byte[0]);
	}

	@Override
	public void writeColumn(Object colName, byte[] value) {
		assertIsReady();
		assertFamilyAndKey();
		AssertUtils.notNull(colName, "Column Name is a required parameter");
		AssertUtils.notNull(value, "Value is a required parameter");

		WriteColumnCommand command = new WriteColumnCommand(this);
		command.setColumnFamily(getColumnFamily());
		command.setRowKey(getRowKey());
		command.setColumnName(colName);
		command.setValue(value);
		doExecuteOrBatch(command, getWriteConsistencyLevel());
	}

	@Override
	public void writeColumn(Object colName, Object value) {
		assertIsReady();
		assertFamilyAndKey();
		AssertUtils.notNull(colName, "Column Name is a required parameter");
		AssertUtils.notNull(value, "Value is a required parameter");

		WriteColumnCommand command = new WriteColumnCommand(this);
		command.setColumnFamily(getColumnFamily());
		command.setRowKey(getRowKey());
		command.setColumnName(colName);
		command.setValue(value);
		doExecuteOrBatch(command, getWriteConsistencyLevel());
	}

	@Override
	public void writeSubColumn(Object colName, Object subColName) {
		writeSubColumn(colName, subColName, new byte[0]);
	}

	@Override
	public void writeSubColumn(Object colName, Object subColName, byte[] value) {
		assertIsReady();
		assertFamilyAndKey();
		AssertUtils.notNull(colName, "Column Name is a required parameter");
		AssertUtils.notNull(subColName, "Sub Column Name is a required parameter");
		AssertUtils.notNull(value, "Value is a required parameter");

		WriteSubColumnCommand command = new WriteSubColumnCommand(this);
		command.setColumnFamily(getColumnFamily());
		command.setRowKey(getRowKey());
		command.setColumnName(colName);
		command.setSubColumnName(subColName);
		command.setValue(value);
		doExecuteOrBatch(command, getWriteConsistencyLevel());
	}

	@Override
	public void writeSubColumn(Object colName, Object subColName, Object value) {
		assertIsReady();
		assertFamilyAndKey();
		AssertUtils.notNull(colName, "Column Name is a required parameter");
		AssertUtils.notNull(subColName, "Sub Column Name is a required parameter");
		AssertUtils.notNull(value, "Value is a required parameter");

		WriteSubColumnCommand command = new WriteSubColumnCommand(this);
		command.setColumnFamily(getColumnFamily());
		command.setRowKey(getRowKey());
		command.setColumnName(colName);
		command.setSubColumnName(subColName);
		command.setValue(value);
		doExecuteOrBatch(command, getWriteConsistencyLevel());
	}

}
