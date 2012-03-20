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

package org.atreus;

import org.apache.cassandra.thrift.ConsistencyLevel;

public interface AtreusSession {

	public void close();

	public void deleteColumn(String colName);

	public void deleteRow();

	public void deleteRow(String colFamily, Object rowKey);

	public void flush();

	public String getColumnFamily();

	public ConsistencyLevel getReadConsistencyLevel();

	public Object getRowKey();

	public ConsistencyLevel getWriteConsistencyLevel();

	public boolean isBatchWriting();

	public boolean isCaching();

	public boolean isEagerFetching();

	public boolean isOpen();

	public boolean existsRow();

	public boolean existsRow(Object rowKey);

	public boolean existsColumn(Object colName);

	public boolean existsColumn(Object colName, Object subColName);

	public <T> T readColumn(Object colName, Class<T> type);

	public <T> T readColumn(Object colName, Object subColName, Class<T> type);

	public void setBatchWriting(boolean batchWriting);

	public void setCaching(boolean caching);

	public void setEagerFetching(boolean eagerFetching);

	public void setRowKey(Object rowKey);

	public void setFamilyAndKey(String colFamily, Object rowKey);

	public void setReadConsistencyLevel(ConsistencyLevel readConsistencyLevel);

	public void setWriteConsistencyLevel(ConsistencyLevel writeConsistencyLevel);

	public void writeColumn(Object colName);

	public void writeColumn(Object colName, byte[] value);

	public void writeColumn(Object colName, Object value);

	public void writeColumn(Object colName, Object subColName, byte[] value);

	public void writeColumn(Object colName, Object subColName, Object value);

}
