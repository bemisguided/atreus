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
package org.atreus.v0.impl.connection;

import java.util.concurrent.atomic.AtomicLong;

import org.atreus.v0.AtreusConfiguration;
import org.atreus.v0.AtreusConnectionException;
import org.atreus.v0.AtreusNetworkException;
import org.atreus.v0.AtreusUnknownException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConnectionBase implements Connection {

	public enum ConnectionResult {
		OK, NETWORK_ERROR, CONNECTION_ERROR, UNKNOWN
	}

	private static final Logger logger = LoggerFactory.getLogger(ConnectionBase.class);

	private static final AtomicLong counter = new AtomicLong();

	private final long id;

	private final String host;

	private final String keyspace;

	private Exception lastException;

	private boolean open = true;

	private final int port;

	protected ConnectionBase(String host, int port, String keyspace, AtreusConfiguration config) {
		this.host = host;
		this.port = port;
		this.keyspace = keyspace;
		this.id = counter.incrementAndGet();
	}

	@Override
	public void close() {
		doClose();
		open = false;
		if (logger.isDebugEnabled()) {
			logger.debug("Connection [" + id + "] for host [" + host + "] closed");
		}
	}

	protected abstract void doClose();

	protected abstract ConnectionResult doOpen();

	protected abstract boolean doValidation();

	@Override
	public final String getHost() {
		return host;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public final String getKeyspace() {
		return keyspace;
	}

	protected Exception getLastException() {
		return lastException;
	}

	@Override
	public final int getPort() {
		return port;
	}

	@Override
	public final boolean isOpen() {
		return open;
	}

	@Override
	public final boolean isValid() {
		boolean result = doValidation();
		if (!result) {
			if (logger.isDebugEnabled()) {
				logger.debug("Connection [" + id + "] for host [" + host + "] failed validation", getLastException());
			}
			open = false;
		}
		return result;
	}

	@Override
	public void open() {
		ConnectionResult result = doOpen();
		switch (result) {
		case OK:
			open = true;
			if (logger.isDebugEnabled()) {
				logger.debug("Connection [" + id + "] for host [" + host + "] opened and connected to keyspace [" + keyspace + "]");
			}
			break;

		case NETWORK_ERROR:
			counter.decrementAndGet();
			throw new AtreusNetworkException("Unable to open transport to Cassandra cluster", getLastException());

		case CONNECTION_ERROR:
			counter.decrementAndGet();
			throw new AtreusConnectionException("Unable to connect to Cassandra cluster keyspace [" + keyspace + "]", getLastException());

		default:
			counter.decrementAndGet();
			throw new AtreusUnknownException("Unable to connect to Cassandra cluster keyspace  [" + keyspace + "]", getLastException());
		}
	}

	protected void setLastException(Exception lastException) {
		this.lastException = lastException;
	}

}
