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
package org.atreus;

import java.util.Set;

public interface AtreusColumnMap {

	public Set<byte[]> columnNameSet();

	public <T> Set<T> columnNameSet(Class<T> type);

	public boolean existsColumn(Object columnName);

	public boolean existsValue(Object value);

	public <T> T get(byte[] columnName, Class<T> type);

	public AtreusColumnMap get(Object columnName);

	public <T> T get(Object columnName, Class<T> type);

	public byte[] getAsBytes(Object columnName);

	public <T> T getRowKey(Class<T> type);

	public boolean hasSubColumns();

	public void put(Object columnName, byte[] value);

	public void put(Object columnName, Object value);

	public void put(Object columnName, Object subColumnName, byte[] value);

	public void put(Object columnName, Object subColumnName, Object value);

	public void setRowKey(Object rowKey);

	public int size();

}
