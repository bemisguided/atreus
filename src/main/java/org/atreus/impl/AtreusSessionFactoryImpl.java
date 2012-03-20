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

import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.commons.lang.NotImplementedException;
import org.atreus.AtreusSession;
import org.atreus.AtreusSessionFactory;
import org.atreus.AtreusTypeConverter;
import org.scale7.cassandra.pelops.Bytes;
import org.scale7.cassandra.pelops.Cluster;
import org.scale7.cassandra.pelops.Mutator;
import org.scale7.cassandra.pelops.RowDeletor;
import org.scale7.cassandra.pelops.Selector;
import org.scale7.cassandra.pelops.pool.CommonsBackedPool;
import org.scale7.cassandra.pelops.pool.IThriftPool;

public class AtreusSessionFactoryImpl implements AtreusSessionFactory {

	private boolean connected = false;

	private ConsistencyLevel defaultReaderConsistencyLevel = ConsistencyLevel.ONE;

	private ConsistencyLevel defaultWriterConsistencyLevel = ConsistencyLevel.ANY;

	private String host;

	private String keyspace;

	private IThriftPool pool;

	private int port;

	private TypeRegistry typeRegistry = new TypeRegistry();

	public AtreusSessionFactoryImpl() {
	}

	@Override
	public void addConverter(AtreusTypeConverter converter) {
		typeRegistry.addConverter(converter);
	}

	@Override
	public void connect(String url) {
		// TODO implement connection url
		throw new NotImplementedException();
	}

	@Override
	public void connect(String host, int port, String keyspace) {
		this.host = host;
		this.port = port;
		this.keyspace = keyspace;

		Cluster cluster = new Cluster(host, port);
		pool = new CommonsBackedPool(cluster, keyspace);
		connected = true;
		typeRegistry.addDefaultConverters();
	}

	protected Mutator createMutator() {
		return pool.createMutator();
	}

	protected RowDeletor createRowDeleter() {
		return pool.createRowDeletor();
	}

	protected Selector createSelector() {
		return pool.createSelector();
	}

	@Override
	public void disconnect() {
		if (isConnected()) {
			pool.shutdown();
			connected = false;
		}
	}

	protected <T> T fromBytes(Class<T> type, byte[] bytes) {
		return typeRegistry.fromBytes(type, bytes);
	}

	@Override
	public ConsistencyLevel getDefaultReadConsistencyLevel() {
		return defaultReaderConsistencyLevel;
	}

	@Override
	public ConsistencyLevel getDefaultWriteConsistencyLevel() {
		return defaultWriterConsistencyLevel;
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public String getKeyspace() {
		return keyspace;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public AtreusSession openSession() {
		AtreusSessionImpl session = new AtreusSessionImpl(this);
		session.setReadConsistencyLevel(defaultReaderConsistencyLevel);
		session.setWriteConsistencyLevel(defaultWriterConsistencyLevel);
		return session;
	}

	@Override
	public void removeConverter(AtreusTypeConverter converter) {
		typeRegistry.removeConverter(converter);
	}

	@Override
	public void setDefaultReadConsistencyLevel(ConsistencyLevel defaultReadConsistencyLevel) {
		this.defaultReaderConsistencyLevel = defaultReadConsistencyLevel;
	}

	@Override
	public void setDefaultWriteConsistencyLevel(ConsistencyLevel defaultWriteConsistencyLevel) {
		this.defaultWriterConsistencyLevel = defaultWriteConsistencyLevel;
	}

	protected Bytes toBytes(Object value) {
		return typeRegistry.toBytes(value);
	}

}
