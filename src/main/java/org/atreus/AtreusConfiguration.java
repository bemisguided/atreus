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

package org.atreus;

import org.apache.cassandra.thrift.ConsistencyLevel;

public class AtreusConfiguration {

	private int connectionTimeout = 5000;

	private boolean defaultBatchWriting = true;

	private boolean defaultCaching = false;

	private boolean defaultEagerFetching = false;

	private ConsistencyLevel defaultReadConsistencyLevel = ConsistencyLevel.ONE;

	private ConsistencyLevel defaultWriteConsistencyLevel = ConsistencyLevel.ANY;

	private String[] hosts;

	private String keyspace;

	private int poolMaxiumumSize = 5;

	private int poolMaximumIdleSize = 2;

	private int port;

	private long poolValidationFrequency = 60000;

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

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public ConsistencyLevel getDefaultReadConsistencyLevel() {
		return defaultReadConsistencyLevel;
	}

	public ConsistencyLevel getDefaultWriteConsistencyLevel() {
		return defaultWriteConsistencyLevel;
	}

	public String[] getHosts() {
		return hosts;
	}

	public String getKeyspace() {
		return keyspace;
	}

	public int getPoolMaximumIdleSize() {
		return poolMaximumIdleSize;
	}

	public int getPoolMaxiumumSize() {
		return poolMaxiumumSize;
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

	public void setDefaultReadConsistencyLevel(ConsistencyLevel defaultReadConsistencyLevel) {
		this.defaultReadConsistencyLevel = defaultReadConsistencyLevel;
	}

	public void setDefaultWriteConsistencyLevel(ConsistencyLevel defaultWriteConsistencyLevel) {
		this.defaultWriteConsistencyLevel = defaultWriteConsistencyLevel;
	}

	public void setHosts(String[] hosts) {
		this.hosts = hosts;
	}

	public void setKeyspace(String keyspace) {
		this.keyspace = keyspace;
	}

	public void setPoolMaximumIdleSize(int poolMaximumIdleSize) {
		this.poolMaximumIdleSize = poolMaximumIdleSize;
	}

	public void setPoolMaxiumumSize(int poolMaxiumumSize) {
		this.poolMaxiumumSize = poolMaxiumumSize;
	}

	public void setPoolValidationFrequency(long poolValidationFrequency) {
		this.poolValidationFrequency = poolValidationFrequency;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
