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
package org.atreus.support.spring;

import org.atreus.AtreusColumnMap;
import org.atreus.AtreusConsistencyLevel;
import org.atreus.AtreusRowList;
import org.atreus.AtreusSession;

public class AtreusSpringSessionImpl implements AtreusSpringSession {

	private AtreusSession delegate;

	AtreusSpringSessionImpl(AtreusSession delegate) {
		this.delegate = delegate;
	}

	@Override
	public void close() {
		throw new IllegalArgumentException("Spring-managed Atreus Session to be closed using the Atreus Spring Session Factory");
	}

	@Override
	public void deleteColumn(Object colName) {
		delegate.deleteColumn(colName);
	}

	@Override
	public void deleteColumn(Object colName, Object subColName) {
		delegate.deleteColumn(colName, subColName);
	}

	@Override
	public void deleteRow() {
		delegate.deleteRow();
	}

	@Override
	public void deleteRow(String colFamily, Object rowKey) {
		delegate.deleteRow(colFamily, rowKey);
	}

	@Override
	public void execute(String cql) {
		delegate.execute(cql);
	}

	@Override
	public boolean existsColumn(Object colName) {
		return delegate.existsColumn(colName);
	}

	@Override
	public boolean existsColumn(Object colName, Object subColName) {
		return delegate.existsColumn(colName, subColName);
	}

	@Override
	public boolean existsRow() {
		return delegate.existsRow();
	}

	@Override
	public boolean existsRow(Object rowKey) {
		return delegate.existsRow(rowKey);
	}

	@Override
	public void flush() {
		delegate.flush();
	}

	@Override
	public String getColumnFamily() {
		return delegate.getColumnFamily();
	}

	@Override
	public AtreusSession getDelegate() {
		return delegate;
	}

	@Override
	public AtreusConsistencyLevel getReadConsistencyLevel() {
		return delegate.getReadConsistencyLevel();
	}

	@Override
	public Object getRowKey() {
		return delegate.getRowKey();
	}

	@Override
	public AtreusConsistencyLevel getWriteConsistencyLevel() {
		return delegate.getWriteConsistencyLevel();
	}

	@Override
	public boolean isBatchWriting() {
		return delegate.isBatchWriting();
	}

	@Override
	public boolean isCaching() {
		return delegate.isCaching();
	}

	@Override
	public boolean isEagerFetching() {
		return delegate.isEagerFetching();
	}

	@Override
	public boolean isOpen() {
		return delegate.isOpen();
	}

	@Override
	public AtreusColumnMap newColumnMap() {
		return delegate.newColumnMap();
	}

	@Override
	public AtreusColumnMap newColumnMap(boolean superColumns) {
		return delegate.newColumnMap(superColumns);
	}

	@Override
	public AtreusRowList query(String cql) {
		return delegate.query(cql);
	}

	@Override
	public <T> T readColumn(Object colName, Class<T> type) {
		return delegate.readColumn(colName, type);
	}

	@Override
	public <T> T readColumn(Object colName, Object subColName, Class<T> type) {
		return delegate.readColumn(colName, subColName, type);
	}

	@Override
	public byte[] readColumnAsBytes(Object colName) {
		return delegate.readColumnAsBytes(colName);
	}

	@Override
	public byte[] readColumnAsBytes(Object colName, Object subColName) {
		return delegate.readColumnAsBytes(colName, subColName);
	}

	@Override
	public AtreusColumnMap readColumns() {
		return delegate.readColumns();
	}

	@Override
	public AtreusColumnMap readColumns(String colFamily, Object rowKey) {
		return delegate.readColumns(colFamily, rowKey);
	}

	@Override
	public void setBatchWriting(boolean batchWriting) {
		delegate.setBatchWriting(batchWriting);
	}

	@Override
	public void setCaching(boolean caching) {
		delegate.setCaching(caching);
	}

	@Override
	public void setColumnFamily(String colFamily) {
		delegate.setColumnFamily(colFamily);
	}

	@Override
	public void setEagerFetching(boolean eagerFetching) {
		delegate.setEagerFetching(eagerFetching);
	}

	@Override
	public void setFamilyAndKey(String colFamily, Object rowKey) {
		delegate.setFamilyAndKey(colFamily, rowKey);
	}

	@Override
	public void setReadConsistencyLevel(AtreusConsistencyLevel readConsistencyLevel) {
		delegate.setReadConsistencyLevel(readConsistencyLevel);
	}

	@Override
	public void setRowKey(Object rowKey) {
		delegate.setRowKey(rowKey);
	}

	@Override
	public void setWriteConsistencyLevel(AtreusConsistencyLevel writeConsistencyLevel) {
		delegate.setWriteConsistencyLevel(writeConsistencyLevel);
	}

	@Override
	public void writeColumn(Object colName) {
		delegate.writeColumn(colName);
	}

	@Override
	public void writeColumn(Object colName, byte[] value) {
		delegate.writeColumn(colName, value);
	}

	@Override
	public void writeColumn(Object colName, Object value) {
		delegate.writeColumn(colName, value);
	}

	@Override
	public void writeSubColumn(Object colName, Object subColName) {
		delegate.writeSubColumn(colName, subColName);
	}

	@Override
	public void writeSubColumn(Object colName, Object subColName, byte[] value) {
		delegate.writeSubColumn(colName, subColName, value);
	}

	@Override
	public void writeSubColumn(Object colName, Object subColName, Object value) {
		delegate.writeSubColumn(colName, subColName, value);
	}
}
