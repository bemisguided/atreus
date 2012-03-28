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

import org.apache.cassandra.thrift.ConsistencyLevel;
import org.atreus.AtreusColumnMap;
import org.atreus.AtreusDisconnectedException;
import org.atreus.AtreusIllegalStateException;
import org.atreus.AtreusSession;
import org.atreus.AtreusSessionClosedException;
import org.atreus.impl.commands.Batch;
import org.atreus.impl.commands.BatchMutationCommand;
import org.atreus.impl.commands.DeleteColumnCommand;
import org.atreus.impl.commands.DeleteRowCommand;
import org.atreus.impl.commands.ReadColumnCommand;
import org.atreus.impl.commands.ReadCommand;
import org.atreus.impl.commands.ReadMultipleColumnsCommand;
import org.atreus.impl.commands.WriteColumnCommand;
import org.atreus.impl.commands.WriteCommand;
import org.atreus.impl.connection.ConnectionManager;
import org.atreus.impl.converters.TypeConverterRegistry;
import org.atreus.impl.utils.AssertUtils;

public class AtreusSessionImpl implements AtreusSession {

	private String columnFamily;

	private Batch batch;

	private boolean batchWriting;

	private boolean caching;

	private boolean eagerFetching;

	private boolean open = true;

	private ConsistencyLevel readConsistencyLevel;

	private Object rowKey;

	private final AtreusSessionFactoryImpl sessionFactory;

	private ConsistencyLevel writeConsistencyLevel;

	AtreusSessionImpl(AtreusSessionFactoryImpl sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.batch = new Batch();
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

		byte[] rowKey = toBytes(getRowKey());
		byte[] colNameBytes = toBytes(colName);
		execute(new DeleteColumnCommand(getColumnFamily(), rowKey, colNameBytes, null, getWriteConsistencyLevel()));
	}

	@Override
	public void deleteColumn(Object colName, Object subColName) {
		assertIsReady();
		assertFamilyAndKey();
		AssertUtils.notNull(colName, "Column Name is a required parameter");
		AssertUtils.notNull(subColName, "Sub Column Name is a required parameter");

		byte[] rowKey = toBytes(getRowKey());
		byte[] colNameBytes = toBytes(colName);
		byte[] subColNameBytes = toBytes(subColName);
		execute(new DeleteColumnCommand(getColumnFamily(), rowKey, colNameBytes, subColNameBytes, getWriteConsistencyLevel()));
	}

	@Override
	public void deleteRow() {
		assertIsReady();
		assertFamilyAndKey();

		deleteRow(getColumnFamily(), getRowKey());
	}

	@Override
	public void deleteRow(String colFamily, Object rowKey) {
		assertIsReady();
		AssertUtils.hasText(colFamily, "Column family is a required parameter");
		AssertUtils.notNull(rowKey, "Row key is a required parameter");

		byte[] rowKeyBytes = toBytes(rowKey);
		executeOrBatch(new DeleteRowCommand(getColumnFamily(), rowKeyBytes, getWriteConsistencyLevel()));
	}

	protected Object execute(ReadCommand command) {
		return getConnectionManager().execute(command);
	}

	protected void execute(WriteCommand command) {
		getConnectionManager().execute(command);
	}

	protected void executeOrBatch(WriteCommand command) {
		if (isBatchWriting()) {
			command.batch(batch);
			return;
		}
		execute(command);
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
			throw new AtreusIllegalStateException("Session is not set to batch writing");
		}
		execute(new BatchMutationCommand(batch, getWriteConsistencyLevel()));
		batch = new Batch();
	}

	protected <T> T fromBytes(Class<T> type, byte[] bytes) {
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
	public ConsistencyLevel getReadConsistencyLevel() {
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
	public ConsistencyLevel getWriteConsistencyLevel() {
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
			return new AtreusSuperColumnMapImpl(getTypeRegistry());
		}
		return new AtreusColumnMapImpl(getTypeRegistry());
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

		byte[] colNameBytes = toBytes(colName);
		byte[] rowKey = toBytes(getRowKey());
		return (byte[]) execute(new ReadColumnCommand(getColumnFamily(), rowKey, colNameBytes, null, getReadConsistencyLevel()));
	}

	@Override
	public byte[] readColumnAsBytes(Object colName, Object subColName) {
		assertIsReady();
		assertFamilyAndKey();
		AssertUtils.notNull(colName, "Column Name is a required parameter");
		AssertUtils.notNull(subColName, "Sub Column Name is a required parameter");

		byte[] colNameBytes = toBytes(colName);
		byte[] rowKey = toBytes(getRowKey());
		byte[] subColNameBytes = toBytes(subColName);
		return (byte[]) execute(new ReadColumnCommand(getColumnFamily(), rowKey, colNameBytes, subColNameBytes, getReadConsistencyLevel()));
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

		byte[] rowKeyBytes = toBytes(rowKey);
		return (AtreusColumnMap) execute(new ReadMultipleColumnsCommand(sessionFactory.getTypeRegistry(), colFamily, rowKeyBytes, getReadConsistencyLevel()));
	}

	@Override
	public void setBatchWriting(boolean batchWriting) {
		assertIsReady();

		if (batch.isOpen() && !batchWriting) {
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
	public void setReadConsistencyLevel(ConsistencyLevel readConsistencyLevel) {
		assertIsReady();
		this.readConsistencyLevel = readConsistencyLevel;
	}

	@Override
	public void setRowKey(Object rowKey) {
		assertIsReady();
		this.rowKey = rowKey;
	}

	@Override
	public void setWriteConsistencyLevel(ConsistencyLevel writeConsistencyLevel) {
		assertIsReady();
		this.writeConsistencyLevel = writeConsistencyLevel;
	}

	protected byte[] toBytes(Object value) {
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
		byte[] colNameBytes = toBytes(colName);
		byte[] rowKeyBytes = toBytes(getRowKey());

		executeOrBatch(new WriteColumnCommand(getColumnFamily(), rowKeyBytes, colNameBytes, null, value, getWriteConsistencyLevel()));
	}

	@Override
	public void writeColumn(Object colName, Object value) {
		byte[] valueBytes = toBytes(value);
		writeColumn(colName, valueBytes);
	}

	@Override
	public void writeColumn(Object colName, Object subColName, byte[] value) {
		assertIsReady();
		assertFamilyAndKey();
		AssertUtils.notNull(colName, "Column Name is a required parameter");
		AssertUtils.notNull(subColName, "Sub Column Name is a required parameter");
		AssertUtils.notNull(value, "Value is a required parameter");
		byte[] colNameBytes = toBytes(colName);
		byte[] subColNameBytes = toBytes(subColName);
		byte[] rowKeyBytes = toBytes(getRowKey());

		executeOrBatch(new WriteColumnCommand(getColumnFamily(), rowKeyBytes, colNameBytes, subColNameBytes, value, getWriteConsistencyLevel()));
	}

	@Override
	public void writeColumn(Object colName, Object subColName, Object value) {
		byte[] valueBytes = toBytes(value);
		writeColumn(colName, subColName, valueBytes);
	}

}
