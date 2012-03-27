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

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.thrift.transport.TTransportException;
import org.atreus.AtreusClusterUnavailableException;
import org.atreus.AtreusCommandException;
import org.atreus.AtreusConfiguration;
import org.atreus.AtreusConnectionException;
import org.atreus.AtreusException;
import org.atreus.AtreusNetworkException;
import org.atreus.AtreusUnknownException;
import org.atreus.impl.commands.DescribeSchemaVersionsCommand;
import org.atreus.impl.commands.ReadCommand;
import org.atreus.impl.commands.WriteCommand;
import org.atreus.impl.utils.AssertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionManager {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

	private final AtreusConfiguration config;

	private final NodeManager nodeManager = new NodeManager();

	private GenericObjectPool<Connection> pool;

	public ConnectionManager(AtreusConfiguration config) {
		this.config = config;
		configureHosts();
	}

	protected void configureHosts() {
		for (String host : config.getHosts()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Adding host [" + host + "] to the  Cassandra cluster host list");
			}
			makeNodeAvailable(host);
		}
	}

	public void connect() {
		pool = new GenericObjectPool<Connection>(new ConnectionFactory(this));
		pool.setTestWhileIdle(true);
		pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		pool.setTestOnBorrow(true);
		pool.setTimeBetweenEvictionRunsMillis(config.getPoolValidationFrequency());
		pool.setMaxActive(config.getPoolMaxiumumSize());
		pool.setMinIdle(config.getPoolMinimumIdleSize());

		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				if (logger.isDebugEnabled()) {
					logger.debug("Pool stats active [" + pool.getNumActive() + "] inactive [" + pool.getNumIdle() + "]");
				}
				try {
					logger.debug("Refreshing Cassandra cluster host list");
					refreshHostList();
				} catch (Exception e) {
					logger.warn("Exception while attempting to refresh Cassandra cluster host list", e);
				}
			}
		}, config.getClusterPollFrequency(), config.getClusterPollFrequency());
		testConnectivity();
	}

	public void disconnect() {
		try {
			pool.close();
		} catch (Exception e) {
			throw new AtreusUnknownException("Unexpected exception on disconnect", e);
		}
	}

	public Object execute(ReadCommand command) {
		Connection conn = retrieveConnection();
		String host = conn.getHost();
		boolean killConnection = false;
		try {
			Object result = command.execute(conn.getClient());
			return result;
		} catch (InvalidRequestException e) {
			throw new AtreusCommandException("Read command supplied was invalid [" + command + "]", e);
		} catch (TTransportException e) {
			killConnection = true;
			throw new AtreusNetworkException("Transport exception on host [" + host + "] while executing read command [" + command + "]", e);
		} catch (UnavailableException e) {
			throw new AtreusClusterUnavailableException("Cassandra cluster unavailable to execute read command [" + command + "] (review Consitency Level)", e);
		} catch (TimedOutException e) {
			killConnection = true;
			throw new AtreusNetworkException("Timeout on host [" + host + "] while executing read command [" + command + "]", e);
		} catch (Exception e) {
			throw new AtreusUnknownException("Exception while executing read command [" + command + "]", e);
		} finally {
			if (killConnection) {
				killConnection(conn);
			} else {
				returnConnection(conn);
			}
		}
	}

	public void execute(WriteCommand command) {
		Connection conn = retrieveConnection();
		String host = conn.getHost();
		boolean killConnection = false;
		try {
			command.execute(conn.getClient());
		} catch (InvalidRequestException e) {
			throw new AtreusCommandException("Write command supplied was invalid [" + command + "]", e);
		} catch (TTransportException e) {
			killConnection = true;
			throw new AtreusNetworkException("Transport exception on host [" + host + "] while executing write command [" + command + "]", e);
		} catch (UnavailableException e) {
			throw new AtreusClusterUnavailableException("Cassandra cluster unavailable to execute write command [" + command + "] (review Consitency Level)", e);
		} catch (TimedOutException e) {
			killConnection = true;
			throw new AtreusNetworkException("Timeout on host [" + host + "] while executing write command [" + command + "]", e);
		} catch (Exception e) {
			throw new AtreusUnknownException("Exception while executing write command [" + command + "]", e);
		} finally {
			if (killConnection) {
				killConnection(conn);
			} else {
				returnConnection(conn);
			}
		}
	}

	public int getConnectionTimeout() {
		return config.getConnectionTimeout();
	}

	public String getKeyspace() {
		return config.getKeyspace();
	}

	public int getPort() {
		return config.getPort();
	}

	public boolean isConnected() {
		return !pool.isClosed();
	}

	protected void killConnection(Connection connection) {
		try {
			if (logger.isWarnEnabled()) {
				logger.warn("Removing Connection for host [" + connection.getHost() + "] from pool");
			}
			pool.invalidateObject(connection);
		} catch (Exception e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Exception while invalidating Connection", e);
			}
		}
	}

	public void makeNodeAvailable(String host) {
		AssertUtils.hasText(host, "Host is a required parameter");
		makeNodeAvailable(host, false);
	}

	public void makeNodeAvailable(String host, boolean seed) {
		AssertUtils.hasText(host, "Host is a required parameter");
		nodeManager.nodeAvailable(host);
		nodeManager.nodeMarkSeed(host);
	}

	public void makeNodeUnavailable(String host) {
		AssertUtils.hasText(host, "Host is a required parameter");
		nodeManager.nodeUnavailable(host);
	}

	protected Connection openConnection() {
		int hostCount = nodeManager.getHosts().size();
		Exception cause = null;
		for (int i = 0; i < hostCount; i++) {
			String host = nodeManager.nextHost();
			Connection conn = new Connection(host, getPort(), getKeyspace(), getConnectionTimeout());
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Attemping to open connection to host [" + host + "]");
				}
				conn.open();
				if (logger.isDebugEnabled()) {
					logger.debug("Successully opened connection to host [" + host + "]");
				}
				return conn;
			} catch (AtreusConnectionException e) {
				throw e;
			} catch (Exception e) {
				cause = e;
				nodeManager.nodeUnavailable(host);
			}
		}
		throw new AtreusConnectionException("No Cassandra cluster hosts available", cause);
	}

	@SuppressWarnings("unchecked")
	public void refreshHostList() {
		Set<String> currentHostList = nodeManager.getHosts();
		Map<String, List<String>> result = (Map<String, List<String>>) execute(new DescribeSchemaVersionsCommand());
		for (String schema : result.keySet()) {
			if ("UNREACHABLE".equals(schema)) {
				for (String host : result.get(schema)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Node host [" + host + "] is unreachable");
					}
					makeNodeUnavailable(host);
					currentHostList.remove(host);
				}
			} else {
				for (String host : result.get(schema)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Node host [" + host + "] is available for schema [" + schema + "]");
					}
					makeNodeAvailable(host);
					currentHostList.remove(host);
				}
			}
		}
		for (String host : currentHostList) {
			if (logger.isDebugEnabled()) {
				logger.debug("Node host [" + host + "] not on schema list, assuming sis unreachable");
			}
			makeNodeUnavailable(host);
		}
	}

	protected Connection retrieveConnection() {
		try {
			return pool.borrowObject();
		} catch (Exception e) {
			if (e instanceof NoSuchElementException) {
				throw new AtreusConnectionException("Connection pool exhausted", e);
			}
			if (e instanceof AtreusException) {
				throw (AtreusException) e;
			}
			throw new AtreusUnknownException("Unexpected exception on attempting to retrieve a Connection", e);
		}
	}

	protected void returnConnection(Connection connection) {
		try {
			pool.returnObject(connection);
		} catch (Exception e) {
			if (e instanceof AtreusException) {
				throw (AtreusException) e;
			}
			throw new AtreusUnknownException("Unexpected exception on disconnect", e);
		}
	}

	public void testConnectivity() {
		returnConnection(retrieveConnection());
	}

	protected boolean validateConnection(Connection connection) {
		String host = connection.getHost();
		if (!nodeManager.isNodeAvailable(host)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Host [" + host + "] is marked unavailable");
			}
			return false;
		}
		if (!connection.isValid()) {
			return false;
		}
		return true;
	}
}
