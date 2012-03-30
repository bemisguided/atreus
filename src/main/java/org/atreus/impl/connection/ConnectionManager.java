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
package org.atreus.impl.connection;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.atreus.AtreusClusterUnavailableException;
import org.atreus.AtreusConfiguration;
import org.atreus.AtreusConnectionException;
import org.atreus.AtreusConnectionPoolCapacityException;
import org.atreus.AtreusConsistencyLevel;
import org.atreus.AtreusException;
import org.atreus.AtreusNetworkException;
import org.atreus.AtreusUnknownException;
import org.atreus.impl.commands.Command;
import org.atreus.impl.commands.CommandBatch;
import org.atreus.impl.utils.AssertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionManager {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

	private final AtreusConfiguration config;

	private final ConnectionProvider provider;

	private final NodeManager nodeManager = new NodeManager();

	private GenericObjectPool<Connection> pool;

	public ConnectionManager(AtreusConfiguration config) {
		this.config = config;
		try {
			this.provider = (ConnectionProvider) config.getConnectionProvider().newInstance();
		} catch (Exception e) {
			throw new AtreusConnectionException("Connection Provider could not be instantiated", e);
		}
		configureHosts();
	}

	ConnectionManager(AtreusConfiguration config, ConnectionProvider provider) {
		this.config = config;
		this.provider = provider;
		configureHosts();
	}

	private void assertConnected() {
		if (!isConnected()) {
			throw new AtreusConnectionException("Connection manager is in a disconnected state");
		}
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
		pool = new GenericObjectPool<Connection>(new ConnectionPoolableObjectFactory(this));
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
					scanCluster();
				} catch (Exception e) {
					logger.warn("Exception while attempting to refresh Cassandra cluster host list", e);
				}
			}
		}, config.getClusterPollFrequency(), config.getClusterPollFrequency());
	}

	public void disconnect() {
		try {
			pool.close();
		} catch (Exception e) {
			throw new AtreusUnknownException("Unexpected exception on disconnect", e);
		}
	}

	public Object execute(Command command, AtreusConsistencyLevel consistencyLevel) {
		assertConnected();
		Connection conn = retrieveConnection();
		boolean killConnection = false;
		try {
			return provider.execute(command, conn, consistencyLevel);
		} catch (AtreusNetworkException e) {
			killConnection = true;
			throw new AtreusNetworkException(e);
		} finally {
			if (killConnection) {
				killConnection(conn);
			} else {
				returnConnection(conn);
			}
		}
	}

	public void executeBatch(CommandBatch batch, AtreusConsistencyLevel consistencyLevel) {
		assertConnected();
		Connection conn = retrieveConnection();
		boolean killConnection = false;
		try {
			provider.executeBatch(batch, conn, consistencyLevel);
		} catch (AtreusNetworkException e) {
			killConnection = true;
			throw new AtreusNetworkException(e);
		} finally {
			if (killConnection) {
				killConnection(conn);
			} else {
				returnConnection(conn);
			}
		}
	}

	public int getConnectionsActive() {
		return pool.getNumActive();
	}

	public int getConnectionsIdle() {
		return pool.getNumIdle();
	}

	public String getKeyspace() {
		return config.getKeyspace();
	}

	public Set<String> getNodeList() {
		return nodeManager.getHosts();
	}

	NodeManager getNodeManager() {
		return nodeManager;
	}

	GenericObjectPool<Connection> getPool() {
		return pool;
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
			Connection conn = provider.newConnection(host, getPort(), getKeyspace(), config);
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Attemping to open connection to host [" + host + "]");
				}
				conn.open();
				if (logger.isDebugEnabled()) {
					logger.debug("Successully opened connection to host [" + host + "]");
				}
				makeNodeAvailable(host);
				return conn;
			} catch (AtreusConnectionException e) {
				if (logger.isErrorEnabled()) {
					logger.error("Unrecovered connection exception occured, shutting down the Connection Manager", e);
				}
				disconnect();
				throw e;
			} catch (Exception e) {
				cause = e;
				makeNodeUnavailable(host);
			}
		}
		throw new AtreusClusterUnavailableException("No Cassandra cluster hosts available", cause);
	}

	protected Connection retrieveConnection() {
		try {
			return pool.borrowObject();
		} catch (Exception e) {
			if (e instanceof NoSuchElementException) {
				throw new AtreusConnectionPoolCapacityException("Connection pool exhausted", e);
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

	public void scanCluster() {
		provider.newClusterDetector().scanCluster(this);
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
