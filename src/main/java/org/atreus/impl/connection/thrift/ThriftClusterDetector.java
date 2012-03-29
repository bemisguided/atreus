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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.atreus.AtreusConsistencyLevel;
import org.atreus.impl.connection.ClusterDetector;
import org.atreus.impl.connection.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThriftClusterDetector implements ClusterDetector {
	private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

	@SuppressWarnings("unchecked")
	@Override
	public void scanCluster(ConnectionManager connectionManager) {
		Set<String> currentHostList = connectionManager.getNodeList();
		Map<String, List<String>> result = (Map<String, List<String>>) connectionManager.execute(new DescribeSchemaCommand(), AtreusConsistencyLevel.ANY);
		for (String schema : result.keySet()) {
			if ("UNREACHABLE".equals(schema)) {
				for (String host : result.get(schema)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Node host [" + host + "] is unreachable");
					}
					connectionManager.makeNodeUnavailable(host);
					currentHostList.remove(host);
				}
			} else {
				for (String host : result.get(schema)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Node host [" + host + "] is available for schema [" + schema + "]");
					}
					connectionManager.makeNodeAvailable(host);
					currentHostList.remove(host);
				}
			}
		}
		for (String host : currentHostList) {
			if (logger.isDebugEnabled()) {
				logger.debug("Node host [" + host + "] not on schema list, assuming it is unreachable");
			}
			connectionManager.makeNodeUnavailable(host);
		}
	}

}
