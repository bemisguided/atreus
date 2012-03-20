/*
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

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.atreus.AtreusDisconnectedException;
import org.atreus.AtreusSession;
import org.atreus.AtreusSessionClosedException;
import org.atreus.impl.utils.AssertUtils;
import org.scale7.cassandra.pelops.Bytes;
import org.scale7.cassandra.pelops.Mutator;
import org.scale7.cassandra.pelops.RowDeletor;
import org.scale7.cassandra.pelops.Selector;
import org.scale7.cassandra.pelops.exceptions.NotFoundException;

public class AtreusSessionImpl implements AtreusSession {

	private String columnFamily;

	private Mutator mutator;

	private boolean open = true;

	private ConsistencyLevel readConsistencyLevel;

	private RowDeletor rowDeletor;

	private Object rowKey;

	private Selector selector;

	private final AtreusSessionFactoryImpl sessionFactory;

	private ConsistencyLevel writeConsistencyLevel;

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
	public void deleteColumn(String colName) {
		assertIsReady();
		// TODO Auto-generated method stub

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
		Bytes rowKeyBytes = toBytes(rowKey);
		getRowDeletor().deleteRow(colFamily, rowKeyBytes, getWriteConsistencyLevel());
	}

	@Override
	public boolean existsColumn(Object colName) {
		assertIsReady();
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean existsColumn(Object colName, Object subColName) {
		assertIsReady();
		// TODO Auto-generated method stub
		return false;
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
		getMutator().execute(getWriteConsistencyLevel());
	}

	protected <T> T fromBytes(Class<T> type, byte[] bytes) {
		return sessionFactory.fromBytes(type, bytes);
	}

	@Override
	public String getColumnFamily() {
		return columnFamily;
	}

	protected Mutator getMutator() {
		if (mutator == null) {
			mutator = sessionFactory.createMutator();
		}
		return mutator;
	}

	@Override
	public ConsistencyLevel getReadConsistencyLevel() {
		return readConsistencyLevel;
	}

	protected RowDeletor getRowDeletor() {
		if (rowDeletor == null) {
			rowDeletor = sessionFactory.createRowDeleter();
		}
		return rowDeletor;
	}

	@Override
	public Object getRowKey() {
		return rowKey;
	}

	protected Selector getSelector() {
		if (selector == null) {
			selector = sessionFactory.createSelector();
		}
		return selector;
	}

	@Override
	public ConsistencyLevel getWriteConsistencyLevel() {
		return writeConsistencyLevel;
	}

	@Override
	public boolean isBatchWriting() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCaching() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEagerFetching() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public <T> T readColumn(Object colName, Class<T> type) {
		assertIsReady();
		try {
			Bytes colNameBytes = toBytes(colName);
			Bytes rowKeyBytes = toBytes(getRowKey());
			Column col = getSelector().getColumnFromRow(getColumnFamily(), rowKeyBytes, colNameBytes, getReadConsistencyLevel());
			byte[] value = col.getValue();
			return fromBytes(type, value);
		} catch (NotFoundException e) {
			// Catch and do nothing, null for not found, empty for emptys
			return null;
		}
	}

	@Override
	public <T> T readColumn(Object colName, Object subColName, Class<T> type) {
		assertIsReady();
		try {
			Bytes colNameBytes = toBytes(colName);
			Bytes subColNameBytes = toBytes(subColName);
			Bytes rowKeyBytes = toBytes(getRowKey());
			Column col = getSelector().getSubColumnFromRow(getColumnFamily(), rowKeyBytes, colNameBytes, subColNameBytes, getReadConsistencyLevel());
			byte[] value = col.getValue();
			return fromBytes(type, value);
		} catch (NotFoundException e) {
			// Catch and do nothing, null for not found, empty for emptys
			return null;
		}
	}

	@Override
	public void setBatchWriting(boolean batchWriting) {
		assertIsReady();
		// TODO Auto-generated method stub

	}

	@Override
	public void setCaching(boolean caching) {
		assertIsReady();
		// TODO Auto-generated method stub

	}

	@Override
	public void setEagerFetching(boolean eagerFetching) {
		assertIsReady();
		// TODO Auto-generated method stub

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

	protected Bytes toBytes(Object value) {
		return sessionFactory.toBytes(value);
	}

	private void writeColum(Object colName, Bytes value) {
		assertIsReady();
		assertFamilyAndKey();
		AssertUtils.notNull(colName, "Column Name is a required parameter");
		AssertUtils.notNull(value, "Value is a required parameter");
		Bytes colNameBytes = toBytes(colName);
		Bytes rowKeyBytes = toBytes(getRowKey());
		Column col = getMutator().newColumn(colNameBytes, value);
		getMutator().writeColumn(getColumnFamily(), rowKeyBytes, col);
	}

	@Override
	public void writeColumn(Object colName) {
		writeColumn(colName, new byte[0]);
	}

	@Override
	public void writeColumn(Object colName, byte[] value) {
		Bytes valueBytes = Bytes.fromByteArray(value);
		writeColum(colName, valueBytes);
	}

	@Override
	public void writeColumn(Object colName, Object value) {
		Bytes valueBytes = toBytes(value);
		writeColum(colName, valueBytes);
	}

	@Override
	public void writeColumn(Object colName, Object subColName, byte[] value) {
		Bytes valueBytes = Bytes.fromByteArray(value);
		writeColumn(colName, subColName, valueBytes);

	}

	private void writeColumn(Object colName, Object subColName, Bytes value) {
		assertIsReady();
		assertFamilyAndKey();
		AssertUtils.notNull(colName, "Column Name is a required parameter");
		AssertUtils.notNull(subColName, "Sub Column Name is a required parameter");
		AssertUtils.notNull(value, "Value is a required parameter");
		Bytes colNameBytes = toBytes(colName);
		Bytes subColNameBytes = toBytes(subColName);
		Bytes rowKeyBytes = toBytes(getRowKey());
		Column col = getMutator().newColumn(subColNameBytes, value);
		getMutator().writeSubColumn(getColumnFamily(), rowKeyBytes, colNameBytes, col);
	}

	@Override
	public void writeColumn(Object colName, Object subColName, Object value) {
		Bytes valueBytes = toBytes(value);
		writeColumn(colName, subColName, valueBytes);
	}

}
