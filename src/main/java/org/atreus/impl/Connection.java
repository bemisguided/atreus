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

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.atreus.AtreusException;
import org.atreus.impl.commands.ReadCommand;
import org.atreus.impl.commands.WriteCommand;

public class Connection {

	private final Client client;

	private final String host;

	private final String keyspace;

	private final int port;

	private final TTransport transport;

	Connection(String host, int port, String keyspace) {
		this.transport = new TFramedTransport(new TSocket(host, port, 1500));
		TProtocol protocol = new TBinaryProtocol(transport);
		client = new Cassandra.Client(protocol);
		try {
			transport.open();
			client.set_keyspace(keyspace);
		} catch (Exception e) {
			throw new AtreusException(e) {
			};
		}
		this.host = host;
		this.port = port;
		this.keyspace = keyspace;
	}

	protected Client getClient() {
		return client;
	}

	public void execute(WriteCommand command) {
		try {
			command.execute(getClient());
		} catch (InvalidRequestException e) {
			throw new AtreusException(e) {
			};
		} catch (UnavailableException e) {
			throw new AtreusException(e) {
			};
		} catch (TimedOutException e) {
			throw new AtreusException(e) {
			};
		} catch (TException e) {
			throw new AtreusException(e) {
			};
		} catch (Exception e) {
			throw new AtreusException(e) {
			};
		}
	}

	public Object execute(ReadCommand command) {
		try {
			return command.execute(getClient());
		} catch (IllegalStateException e) {
			throw new AtreusException(e) {
			};
		} catch (InvalidRequestException e) {
			throw new AtreusException(e) {
			};
		} catch (UnavailableException e) {
			throw new AtreusException(e) {
			};
		} catch (TimedOutException e) {
			throw new AtreusException(e) {
			};
		} catch (TException e) {
			throw new AtreusException(e) {
			};
		} catch (Exception e) {
			throw new AtreusException(e) {
			};
		}
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

	public void close() {
		transport.close();
	}
}
