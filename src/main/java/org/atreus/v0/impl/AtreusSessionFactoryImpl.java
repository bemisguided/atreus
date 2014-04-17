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
package org.atreus.v0.impl;

import org.atreus.v0.AtreusConfiguration;
import org.atreus.v0.AtreusConsistencyLevel;
import org.atreus.v0.AtreusSession;
import org.atreus.v0.AtreusSessionFactory;
import org.atreus.v0.AtreusTypeConverter;
import org.atreus.v0.impl.connection.ConnectionManager;
import org.atreus.v0.impl.converters.TypeConverterRegistry;

public class AtreusSessionFactoryImpl implements AtreusSessionFactory {

	private final AtreusConfiguration config;

	private final ConnectionManager connectionManager;

	private String host;

	private int port;

	private TypeConverterRegistry typeRegistry = new TypeConverterRegistry();

	public AtreusSessionFactoryImpl(AtreusConfiguration config) {
		this.config = config;
		this.connectionManager = new ConnectionManager(config);
		typeRegistry.addDefaultConverters();

	}

	@Override
	public void addConverter(AtreusTypeConverter converter) {
		typeRegistry.addConverter(converter);
	}

	public void connect() {
		getConnectionManager().connect();
		getConnectionManager().testConnectivity();
	}

	@Override
	public void disconnect() {
		if (isConnected()) {
			getConnectionManager().disconnect();
		}
	}

	protected <T> T fromBytes(Class<T> type, byte[] bytes) {
		return typeRegistry.fromBytes(type, bytes);
	}

	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	@Override
	public AtreusConsistencyLevel getDefaultReadConsistencyLevel() {
		return config.getDefaultReadConsistencyLevel();
	}

	@Override
	public AtreusConsistencyLevel getDefaultWriteConsistencyLevel() {
		return config.getDefaultWriteConsistencyLevel();
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public String getKeyspace() {
		return config.getKeyspace();
	}

	@Override
	public int getPort() {
		return port;
	}

	protected TypeConverterRegistry getTypeRegistry() {
		return typeRegistry;
	}

	@Override
	public boolean isConnected() {
		return getConnectionManager().isConnected();
	}

	@Override
	public AtreusSession openSession() {
		AtreusSessionImpl session = new AtreusSessionImpl(this);
		session.setReadConsistencyLevel(getDefaultReadConsistencyLevel());
		session.setWriteConsistencyLevel(getDefaultWriteConsistencyLevel());
		session.setBatchWriting(config.isDefaultBatchWriting());
		session.setCaching(config.isDefaultCaching());
		session.setEagerFetching(config.isDefaultEagerFetching());
		return session;
	}

	@Override
	public void removeConverter(AtreusTypeConverter converter) {
		typeRegistry.removeConverter(converter);
	}

	protected byte[] toBytes(Object value) {
		return typeRegistry.toBytes(value);
	}

}
