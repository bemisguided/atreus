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
package org.atreus.impl.connection.thrift;

import java.util.HashMap;
import java.util.Map;

import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.transport.TTransportException;
import org.atreus.AtreusClusterUnavailableException;
import org.atreus.AtreusCommandException;
import org.atreus.AtreusConfiguration;
import org.atreus.AtreusNetworkException;
import org.atreus.AtreusUnknownException;
import org.atreus.impl.commands.Command;
import org.atreus.impl.commands.DeleteColumnCommand;
import org.atreus.impl.commands.DeleteRowCommand;
import org.atreus.impl.commands.ReadColumnCommand;
import org.atreus.impl.commands.ReadMultipleColumnsCommand;
import org.atreus.impl.commands.WriteColumnCommand;
import org.atreus.impl.connection.ClusterDetector;
import org.atreus.impl.connection.Connection;
import org.atreus.impl.connection.ConnectionProvider;

public class ThriftConnectionProvider implements ConnectionProvider {

	private final Map<Class<?>, ThriftCommandExecutor> executors = new HashMap<Class<?>, ThriftCommandExecutor>();

	public ThriftConnectionProvider() {
		executors.put(DeleteColumnCommand.class, new DeleteColumnExecutor());
		executors.put(DeleteRowCommand.class, new DeleteRowExecutor());
		executors.put(ReadColumnCommand.class, new ReadColumnExecutor());
		executors.put(ReadMultipleColumnsCommand.class, new ReadMultipleColumnsExecutor());
		executors.put(WriteColumnCommand.class, new WriteColumnsExecutor());
		executors.put(DescribeSchemaCommand.class, new DescribeSchemaExecutor());
	}

	@Override
	public Object execute(Command command, Connection connection) {
		ThriftConnection thriftConn = (ThriftConnection) connection;
		try {
			ThriftCommandExecutor executor = getExecutor(command);
			return executor.execute(thriftConn.getClient(), command);
		} catch (InvalidRequestException e) {
			throw new AtreusCommandException("Read command supplied was invalid [" + command + "]", e);
		} catch (TTransportException e) {
			throw new AtreusNetworkException("Transport exception on host [" + connection.getHost() + "] while executing command [" + command + "]", e);
		} catch (UnavailableException e) {
			throw new AtreusClusterUnavailableException("Cassandra cluster unavailable to execute command [" + command + "] (review Consitency Level)", e);
		} catch (TimedOutException e) {
			throw new AtreusNetworkException("Timeout on host [" + connection.getHost() + "] while executing command [" + command + "]", e);
		} catch (Exception e) {
			throw new AtreusUnknownException("Exception while executing command [" + command + "]", e);
		}
	}

	private ThriftCommandExecutor getExecutor(Command command) {
		return executors.get(command.getClass());
	}

	@Override
	public ClusterDetector newClusterDetector() {
		return new ThriftClusterDetector();
	}

	@Override
	public Connection newConnection(String host, int port, String keyspace, AtreusConfiguration config) {
		return new ThriftConnection(host, port, keyspace, config);
	}

}
