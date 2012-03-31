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

import org.atreus.AtreusClusterUnavailableException;
import org.atreus.AtreusCommandException;
import org.atreus.AtreusConfiguration;
import org.atreus.AtreusConsistencyLevel;
import org.atreus.AtreusNetworkException;
import org.atreus.AtreusUnknownException;
import org.atreus.impl.commands.Command;

public interface ConnectionProvider {

	public Object execute(Command command, Connection connection, AtreusConsistencyLevel consistencyLevel) throws AtreusCommandException, AtreusClusterUnavailableException,
			AtreusNetworkException, AtreusUnknownException;

	public ClusterDetector newClusterDetector();

	public Connection newConnection(String host, int port, String keyspace, AtreusConfiguration config);

}
