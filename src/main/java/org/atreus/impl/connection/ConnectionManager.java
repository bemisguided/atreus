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

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.cassandra.thrift.TokenRange;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.atreus.AtreusConfiguration;
import org.atreus.AtreusConnectionException;
import org.atreus.AtreusException;
import org.atreus.AtreusUnknownException;
import org.atreus.impl.commands.DescribeRingCommand;
import org.atreus.impl.utils.AssertUtils;

public class ConnectionManager {

	private final AtreusConfiguration config;

	private List<String> hosts = new LinkedList<String>();

	private GenericObjectPool<Connection> pool;

	public ConnectionManager(AtreusConfiguration config) {
		this.config = config;
		configureHosts();
	}

	public void addHost(String host) {
		AssertUtils.hasText(host, "Host is a required parameter");
		if (!hosts.contains(host)) {
			hosts.add(host);
		}
	}

	private void configureHosts() {
		for (String host : config.getHosts()) {
			addHost(host);
		}
	}

	public void connect() {
		pool = new GenericObjectPool<Connection>(new ConnectionFactory(this));
		pool.setTestWhileIdle(true);
		pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		pool.setTimeBetweenEvictionRunsMillis(config.getPoolValidationFrequency());
		pool.setMaxActive(config.getPoolMaxiumumSize());
		pool.setMaxIdle(config.getPoolMaximumIdleSize());
		refreshNodeList();
	}

	public void disconnect() {
		try {
			pool.close();
		} catch (Exception e) {
			throw new AtreusUnknownException("Unexpected exception on disconnect", e);
		}
	}

	public Connection getConnection() {
		try {
			return pool.borrowObject();
		} catch (Exception e) {
			if (e instanceof NoSuchElementException) {
				throw new AtreusConnectionException("Connection pool exhausted");
			}
			if (e instanceof AtreusException) {
				throw (AtreusException) e;
			}
			throw new AtreusUnknownException("Unexpected exception on disconnect", e);
		}
	}

	public String[] getHosts() {
		synchronized (hosts) {
			String[] result = new String[hosts.size()];
			return hosts.toArray(result);
		}
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

	@SuppressWarnings("unchecked")
	public void refreshNodeList() {
		Connection conn = getConnection();
		List<TokenRange> result = (List<TokenRange>) conn.execute(new DescribeRingCommand(getKeyspace()));
		returnConnection(conn);
		synchronized (hosts) {
			hosts.clear();
			for (TokenRange range : result) {
				List<String> endPoints = range.getEndpoints();
				for (String endPoint : endPoints) {
					hosts.add(endPoint);
				}
			}
		}
	}

	public void removeHost(String host) {
		AssertUtils.hasText(host, "Host is a required parameter");
		hosts.remove(host);
	}

	public void returnConnection(Connection connection) {
		try {
			pool.returnObject(connection);
		} catch (Exception e) {
			if (e instanceof AtreusException) {
				throw (AtreusException) e;
			}
			throw new AtreusUnknownException("Unexpected exception on disconnect", e);
		}
	}
}
