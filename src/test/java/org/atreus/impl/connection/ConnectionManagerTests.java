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

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import org.atreus.AtreusClusterUnavailableException;
import org.atreus.AtreusConfiguration;
import org.atreus.AtreusConnectionException;
import org.atreus.AtreusNetworkException;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;

public class ConnectionManagerTests {

	private static final String SINGLE_HOST = "host";

	private static final int SINGLE_PORT = 50;

	private static final String SINGLE_KEYSPACE = "keyspace";

	private AtreusConfiguration config;

	private Connection connection;

	private ConnectionProvider provider;

	private void buildSingleHostMocks(IMocksControl controller) {
		provider = controller.createMock(ConnectionProvider.class);
		connection = controller.createMock(Connection.class);
		config = new AtreusConfiguration(SINGLE_HOST, SINGLE_PORT, SINGLE_KEYSPACE);
		expect(provider.newConnection(SINGLE_HOST, SINGLE_PORT, SINGLE_KEYSPACE, config)).andReturn(connection);
	}

	@Test
	public void openConnectionTest() throws Exception {
		IMocksControl controller = createStrictControl();
		buildSingleHostMocks(controller);
		connection.open();
		controller.replay();

		ConnectionManager manager = new ConnectionManager(config, provider);
		manager.connect();
		manager.openConnection();
		Assert.assertTrue("Connection Manager should be connected", manager.isConnected());
		controller.verify();
	}

	@Test
	public void openConnectionNetworkExceptionTest() throws Exception {
		IMocksControl controller = createStrictControl();
		buildSingleHostMocks(controller);
		connection.open();
		Exception throwMe = new AtreusNetworkException("Test exception");
		expectLastCall().andThrow(throwMe);
		controller.replay();

		ConnectionManager manager = new ConnectionManager(config, provider);
		manager.connect();
		try {
			manager.openConnection();
			Assert.fail("Exepected AtreusNetworkException to be thrown");
		} catch (AtreusClusterUnavailableException e) {
			Assert.assertTrue("Connection Manager should remain connected", manager.isConnected());
			Assert.assertEquals(throwMe, e.getCause());
		}
		controller.verify();
	}

	@Test
	public void openConnectionConnectionExceptionTest() throws Exception {
		IMocksControl controller = createStrictControl();
		buildSingleHostMocks(controller);
		connection.open();
		expectLastCall().andThrow(new AtreusConnectionException("Test exception"));
		controller.replay();

		ConnectionManager manager = new ConnectionManager(config, provider);
		manager.connect();
		try {
			manager.openConnection();
			Assert.fail("Exepected AtreusConnectionException to be thrown");
		} catch (AtreusConnectionException e) {
			Assert.assertFalse("Connection Manager should be disconnected", manager.isConnected());
		}
		controller.verify();
	}
}
