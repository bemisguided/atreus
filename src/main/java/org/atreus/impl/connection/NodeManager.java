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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class NodeManager {

	private static final Logger logger = LoggerFactory.getLogger(NodeManager.class);

	private int countUnavailable = 0;

	private final Queue<Node> queue = new LinkedBlockingQueue<Node>();

	private final Map<String, Node> nodes = new HashMap<String, Node>();

	private synchronized boolean allUnavailable() {
		return countUnavailable == nodes.size();
	}

	private synchronized void decrementUnavailable() {
		if (countUnavailable > 0) {
			countUnavailable--;
		}
	}

	public synchronized Set<String> getHosts() {
		Set<String> result = new HashSet<String>();
		for (String host : nodes.keySet()) {
			result.add(host);
		}
		return result;
	}

	private synchronized void incrementUnavailable() {
		if (countUnavailable < nodes.size()) {
			countUnavailable++;
		}
	}

	public boolean isNodeAvailable(String host) {
		Node node = nodes.get(host);
		if (node == null) {
			return false;
		}
		return node.isAvailable();
	}

	public synchronized String nextHost() {
		Node startNode = queue.poll();
		if (startNode == null) {
			return null;
		}
		Node node = startNode;
		boolean first = true;
		while (first || !node.equals(startNode)) {
			first = false;
			queue.add(node);
			if (node.isAvailable() || (node.isSeed() && allUnavailable())) {
				node.setAvailable(true);
				return node.getHost();
			}
			node = queue.poll();
		}
		return null;
	}

	public void nodeAvailable(String host) {
		Node node;
		decrementUnavailable();
		if (nodes.containsKey(host)) {
			node = nodes.get(host);
			if (logger.isInfoEnabled() && !node.isAvailable()) {
				logger.info("Host [" + host + "] seed [" + node.isSeed() + "] is marked available");
			}
			node.setAvailable(true);
			return;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Host [" + host + "] seed [false] is marked available");
		}
		node = new Node(host, false, true);
		queue.add(node);
		nodes.put(host, node);
	}

	public void nodeMarkSeed(String host) {
		if (nodes.containsKey(host)) {
			Node node = nodes.get(host);
			if (logger.isInfoEnabled() && !node.isSeed()) {
				logger.info("Host [" + host + "] is marked as a seed");
			}
			node.setSeed(true);
		}
	}

	public void nodeUnavailable(String host) {
		Node node;
		incrementUnavailable();
		if (nodes.containsKey(host)) {
			node = nodes.get(host);
			if (logger.isInfoEnabled() && node.isAvailable()) {
				logger.info("Host [" + host + "] seed [" + node.isSeed() + "] is marked available");
			}
			node.setAvailable(false);
			return;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Host [" + host + "] seed [false] is marked unavailable");
		}
		node = new Node(host, false, false);
		queue.add(node);
		nodes.put(host, node);
	}

	public void nodeUnmarkSeed(String host) {
		if (nodes.containsKey(host)) {
			Node node = nodes.get(host);
			if (logger.isInfoEnabled() && node.isSeed()) {
				logger.info("Host [" + host + "] is unmarked as a seed");
			}
			node.setSeed(false);
		}
	}

}
