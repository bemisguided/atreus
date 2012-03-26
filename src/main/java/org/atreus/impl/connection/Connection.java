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

package org.atreus.impl.connection;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.atreus.AtreusConnectionException;
import org.atreus.AtreusNetworkException;
import org.atreus.AtreusUnknownException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Connection {

	private static final Logger logger = LoggerFactory.getLogger(Connection.class);

	private static final AtomicLong counter = new AtomicLong();

	private final long id;

	private final Client client;

	private final String host;

	private final String keyspace;

	private boolean open = true;

	private final int port;

	private final TTransport transport;

	Connection(String host, int port, String keyspace, int timeout) {
		this.id = counter.incrementAndGet();
		this.transport = new TFramedTransport(new TSocket(host, port, timeout));
		TProtocol protocol = new TBinaryProtocol(transport);
		this.client = new Cassandra.Client(protocol);
		this.host = host;
		this.port = port;
		this.keyspace = keyspace;
	}

	public void close() {
		open = false;
		transport.close();
		if (logger.isDebugEnabled()) {
			logger.debug("Connection [" + id + "] for host [" + host + "] closed");
		}
	}

	public Client getClient() {
		return client;
	}

	public String getHost() {
		return host;
	}

	public String getKeyspace() {
		return keyspace;
	}

	public int getPort() {
		return port;
	}

	public boolean isOpen() {
		return open;
	}

	public boolean isValid() {
		try {
			getClient().describe_cluster_name();
			return true;
		} catch (TException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Connection [" + id + "] for host [" + host + "] failed validation", e);
			}
			open = false;
			return false;
		}
	}

	public void open() {
		try {
			transport.open();
		} catch (TTransportException e) {
			throw new AtreusNetworkException("Unable to open transport to Cassandra cluster", e);
		}
		try {
			client.set_keyspace(keyspace);
		} catch (InvalidRequestException e) {
			throw new AtreusConnectionException("Unable to connect to Cassandra cluster keyspace [" + keyspace + "]", e);
		} catch (TException e) {
			throw new AtreusUnknownException("Unable to connect to Cassandra cluster keyspace  [" + keyspace + "]", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Connection [" + id + "] for host [" + host + "] opened and connected to keyspace [" + keyspace + "]");
		}
	}
}
