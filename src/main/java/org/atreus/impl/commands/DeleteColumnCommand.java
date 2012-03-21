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

package org.atreus.impl.commands;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;

public class DeleteColumnCommand extends ColumnCommandBase implements WriteCommand {

	public DeleteColumnCommand(String columnFamily, byte[] rowKey, byte[] columnName, byte[] subColumnName, ConsistencyLevel consistencyLevel) {
		super(columnFamily, rowKey, columnName, subColumnName, consistencyLevel);
	}

	@Override
	public Mutation buildMutation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(Client client) throws Exception {
		ColumnPath path = new ColumnPath(getColumnFamily());
		if (getSubColumnName() != null) {
			path.setSuper_column(getColumnName());
			path.setColumn(getSubColumnName());
		} else {
			path.setColumn(getColumnName());
		}
		client.remove(getRowKey(), path, System.currentTimeMillis(), getConsistencyLevel());
	}

}
