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

import java.util.HashMap;
import java.util.Map;

import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.transport.TTransportException;
import org.atreus.v0.AtreusClusterUnavailableException;
import org.atreus.v0.AtreusCommandException;
import org.atreus.v0.AtreusConfiguration;
import org.atreus.v0.AtreusConsistencyLevel;
import org.atreus.v0.AtreusNetworkException;
import org.atreus.v0.AtreusUnknownException;
import org.atreus.v0.impl.commands.BatchCommand;
import org.atreus.v0.impl.commands.Command;
import org.atreus.v0.impl.commands.CqlCommand;
import org.atreus.v0.impl.commands.CqlWriteCommand;
import org.atreus.v0.impl.commands.DeleteColumnCommand;
import org.atreus.v0.impl.commands.DeleteRowCommand;
import org.atreus.v0.impl.commands.DescribeSchemaCommand;
import org.atreus.v0.impl.commands.ReadColumnCommand;
import org.atreus.v0.impl.commands.ReadMultipleColumnsCommand;
import org.atreus.v0.impl.commands.WriteColumnCommand;
import org.atreus.v0.impl.commands.WriteSubColumnCommand;
import org.atreus.v0.impl.connection.ClusterDetector;
import org.atreus.v0.impl.connection.Connection;
import org.atreus.v0.impl.connection.ConnectionProvider;
import org.atreus.v0.impl.connection.thrift.executors.BatchExecutor;
import org.atreus.v0.impl.connection.thrift.executors.CqlExecutor;
import org.atreus.v0.impl.connection.thrift.executors.DeleteColumnExecutor;
import org.atreus.v0.impl.connection.thrift.executors.DeleteRowExecutor;
import org.atreus.v0.impl.connection.thrift.executors.DescribeSchemaExecutor;
import org.atreus.v0.impl.connection.thrift.executors.ReadColumnExecutor;
import org.atreus.v0.impl.connection.thrift.executors.ReadMultipleColumnsExecutor;
import org.atreus.v0.impl.connection.thrift.executors.ThriftCommandExecutor;
import org.atreus.v0.impl.connection.thrift.executors.WriteColumnExecutor;
import org.atreus.v0.impl.connection.thrift.executors.WriteSubColumnExecutor;

public class ThriftConnectionProvider implements ConnectionProvider {

	private final Map<Class<?>, ThriftCommandExecutor> executors = new HashMap<Class<?>, ThriftCommandExecutor>();

	public ThriftConnectionProvider() {
		executors.put(DeleteColumnCommand.class, new DeleteColumnExecutor());
		executors.put(DeleteRowCommand.class, new DeleteRowExecutor());
		executors.put(ReadColumnCommand.class, new ReadColumnExecutor());
		executors.put(ReadMultipleColumnsCommand.class, new ReadMultipleColumnsExecutor());
		executors.put(WriteColumnCommand.class, new WriteColumnExecutor());
		executors.put(WriteSubColumnCommand.class, new WriteSubColumnExecutor());
		executors.put(DescribeSchemaCommand.class, new DescribeSchemaExecutor());
		executors.put(CqlCommand.class, new CqlExecutor());
		executors.put(CqlWriteCommand.class, new CqlExecutor());
		executors.put(BatchCommand.class, new BatchExecutor());
	}

	@Override
	public Object execute(Command command, Connection connection, AtreusConsistencyLevel consistencyLevel) {
		ThriftConnection thriftConn = (ThriftConnection) connection;
		ConsistencyLevel thriftLevel = ConsistencyLevel.valueOf(consistencyLevel.toString());

		ThriftCommandExecutor executor = getExecutor(command);
		if (executor == null) {
			throw new IllegalArgumentException("Thrift Executor could not be found for command [" + command + "]");
		}
		try {
			return executor.execute(thriftConn.getClient(), command, thriftLevel);
		} catch (InvalidRequestException e) {
			throw new AtreusCommandException("Command supplied was invalid [" + command + "]", e);
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