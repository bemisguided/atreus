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
package org.atreus.v0.impl.connection.thrift;

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
import org.atreus.v0.AtreusConfiguration;
import org.atreus.v0.impl.connection.ConnectionBase;

class ThriftConnection extends ConnectionBase {

	private final Client client;

	protected final TTransport transport;

	ThriftConnection(String host, int port, String keyspace, AtreusConfiguration config) {
		super(host, port, keyspace, config);
		this.transport = new TFramedTransport(new TSocket(host, port, config.getConnectionTimeout()));
		TProtocol protocol = new TBinaryProtocol(transport);
		this.client = new Cassandra.Client(protocol);
	}

	@Override
	protected void doClose() {
		transport.close();
	}

	@Override
	protected ConnectionResult doOpen() {
		try {
			transport.open();
		} catch (TTransportException e) {
			setLastException(e);
			return ConnectionResult.NETWORK_ERROR;
		}
		try {
			client.set_keyspace(getKeyspace());
		} catch (InvalidRequestException e) {
			setLastException(e);
			return ConnectionResult.CONNECTION_ERROR;
		} catch (TException e) {
			setLastException(e);
			return ConnectionResult.UNKNOWN;
		}
		return ConnectionResult.OK;
	}

	@Override
	protected boolean doValidation() {
		try {
			getClient().describe_cluster_name();
			return true;
		} catch (TException e) {
			setLastException(e);
			return false;
		}
	}

	public Client getClient() {
		return client;
	}

}
