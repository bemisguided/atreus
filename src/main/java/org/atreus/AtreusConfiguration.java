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
package org.atreus;

import org.atreus.impl.connection.thrift.ThriftConnectionProvider;

public class AtreusConfiguration {

	private int clusterPollFrequency = 2 * 60 * 1000;

	private Class<?> connectionProvider = ThriftConnectionProvider.class;

	private int connectionTimeout = 5 * 1000;

	private boolean defaultBatchWriting = true;

	private boolean defaultCaching = false;

	private boolean defaultEagerFetching = false;

	private AtreusConsistencyLevel defaultReadConsistencyLevel = AtreusConsistencyLevel.ONE;

	private AtreusConsistencyLevel defaultWriteConsistencyLevel = AtreusConsistencyLevel.ANY;

	private String[] hosts;

	private String keyspace;

	private int poolMaxiumumSize = 5;

	private int poolMinimumIdleSize = 2;

	private long poolValidationFrequency = 1 * 60 * 1000;

	private int port;

	public AtreusConfiguration() {
	}

	public AtreusConfiguration(String host, int port, String keyspace) {
		this.hosts = new String[] { host };
		this.port = port;
		this.keyspace = keyspace;
	}

	public AtreusConfiguration(String[] hosts, int port, String keyspace) {
		this.hosts = hosts;
		this.port = port;
		this.keyspace = keyspace;
	}

	public int getClusterPollFrequency() {
		return clusterPollFrequency;
	}

	public Class<?> getConnectionProvider() {
		return connectionProvider;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public AtreusConsistencyLevel getDefaultReadConsistencyLevel() {
		return defaultReadConsistencyLevel;
	}

	public AtreusConsistencyLevel getDefaultWriteConsistencyLevel() {
		return defaultWriteConsistencyLevel;
	}

	public String[] getHosts() {
		return hosts;
	}

	public String getKeyspace() {
		return keyspace;
	}

	public int getPoolMaxiumumSize() {
		return poolMaxiumumSize;
	}

	public int getPoolMinimumIdleSize() {
		return poolMinimumIdleSize;
	}

	public long getPoolValidationFrequency() {
		return poolValidationFrequency;
	}

	public int getPort() {
		return port;
	}

	public boolean isDefaultBatchWriting() {
		return defaultBatchWriting;
	}

	public boolean isDefaultCaching() {
		return defaultCaching;
	}

	public boolean isDefaultEagerFetching() {
		return defaultEagerFetching;
	}

	public void setClusterPollFrequency(int clusterPollFrequency) {
		this.clusterPollFrequency = clusterPollFrequency;
	}

	public void setConnectionProvider(Class<?> connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setDefaultBatchWriting(boolean defaultBatchWriting) {
		this.defaultBatchWriting = defaultBatchWriting;
	}

	public void setDefaultCaching(boolean defaultCaching) {
		this.defaultCaching = defaultCaching;
	}

	public void setDefaultEagerFetching(boolean defaultEagerFetching) {
		this.defaultEagerFetching = defaultEagerFetching;
	}

	public void setDefaultReadConsistencyLevel(AtreusConsistencyLevel defaultReadConsistencyLevel) {
		this.defaultReadConsistencyLevel = defaultReadConsistencyLevel;
	}

	public void setDefaultWriteConsistencyLevel(AtreusConsistencyLevel defaultWriteConsistencyLevel) {
		this.defaultWriteConsistencyLevel = defaultWriteConsistencyLevel;
	}

	public void setHosts(String[] hosts) {
		this.hosts = hosts;
	}

	public void setKeyspace(String keyspace) {
		this.keyspace = keyspace;
	}

	public void setPoolMaxiumumSize(int poolMaxiumumSize) {
		this.poolMaxiumumSize = poolMaxiumumSize;
	}

	public void setPoolMinimumIdleSize(int poolMaximumIdleSize) {
		this.poolMinimumIdleSize = poolMaximumIdleSize;
	}

	public void setPoolValidationFrequency(long poolValidationFrequency) {
		this.poolValidationFrequency = poolValidationFrequency;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
