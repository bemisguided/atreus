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

import static org.easymock.EasyMock.createNiceControl;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import org.atreus.AtreusClusterUnavailableException;
import org.atreus.AtreusConfiguration;
import org.atreus.AtreusConnectionException;
import org.atreus.AtreusConsistencyLevel;
import org.atreus.AtreusNetworkException;
import org.atreus.AtreusUnknownException;
import org.atreus.impl.commands.Command;
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
		config.setPoolMaxiumumSize(1);
		config.setPoolMinimumIdleSize(1);
		expect(provider.newConnection(SINGLE_HOST, SINGLE_PORT, SINGLE_KEYSPACE, config)).andReturn(connection);
	}

	@Test
	public void executeNotConnectedTest() throws Exception {
		IMocksControl controller = createStrictControl();
		buildSingleHostMocks(controller);
		Command command = controller.createMock(Command.class);
		controller.replay();

		ConnectionManager manager = new ConnectionManager(config, provider);
		manager.connect();
		manager.disconnect();
		try {
			manager.execute(command, AtreusConsistencyLevel.EACH_QUORUM);
			Assert.fail("Expected failure due to being disconnected");
		} catch (AtreusConnectionException e) {
			Assert.assertFalse("Connection Manager should be disconnected", manager.isConnected());
		}
	}

	@Test
	public void executionNetworkExceptionTest() throws Exception {
		IMocksControl controller = createNiceControl();
		buildSingleHostMocks(controller);
		Command command = controller.createMock(Command.class);
		expect(connection.getHost()).andReturn(SINGLE_HOST).anyTimes();
		expect(connection.isValid()).andReturn(true).anyTimes();
		connection.open();
		Exception throwMe = new AtreusNetworkException("Test exception");
		expect(provider.execute(command, connection, AtreusConsistencyLevel.EACH_QUORUM)).andThrow(throwMe);

		controller.replay();

		ConnectionManager manager = new ConnectionManager(config, provider);
		manager.connect();
		try {
			manager.execute(command, AtreusConsistencyLevel.EACH_QUORUM);
			Assert.fail("Expected network failure");
		} catch (AtreusNetworkException e) {
			Assert.assertEquals(0, manager.getConnectionsActive());
			Assert.assertEquals(0, manager.getConnectionsIdle());
			controller.verify();
		}

	}

	@Test
	public void executionTest() throws Exception {
		IMocksControl controller = createNiceControl();
		buildSingleHostMocks(controller);
		Command command = controller.createMock(Command.class);
		expect(connection.getHost()).andReturn(SINGLE_HOST).anyTimes();
		expect(connection.isValid()).andReturn(true).anyTimes();
		connection.open();
		String result = "RESULT";
		expect(provider.execute(command, connection, AtreusConsistencyLevel.EACH_QUORUM)).andReturn(result);

		controller.replay();

		ConnectionManager manager = new ConnectionManager(config, provider);
		manager.connect();
		String assertResult = (String) manager.execute(command, AtreusConsistencyLevel.EACH_QUORUM);
		Assert.assertTrue("SINGLE_HOST should be still marked available", manager.getNodeManager().isNodeAvailable(SINGLE_HOST));
		Assert.assertEquals(result, assertResult);
		Assert.assertEquals(0, manager.getConnectionsActive());
		Assert.assertEquals(1, manager.getConnectionsIdle());
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
			Assert.assertFalse("SINGLE_HOST should be no longer be marked available", manager.getNodeManager().isNodeAvailable(SINGLE_HOST));
		}
		controller.verify();
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
		Assert.assertTrue("SINGLE_HOST should be still marked available", manager.getNodeManager().isNodeAvailable(SINGLE_HOST));
		controller.verify();
	}

	@Test
	public void openConnectionUnknownExceptionTest() throws Exception {
		IMocksControl controller = createStrictControl();
		buildSingleHostMocks(controller);
		connection.open();
		Exception throwMe = new AtreusUnknownException("Test exception");
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
			Assert.assertFalse("SINGLE_HOST should be no longer be marked available", manager.getNodeManager().isNodeAvailable(SINGLE_HOST));
		}
		controller.verify();
	}

	@Test
	public void validateConnectionTest() throws Exception {
		IMocksControl controller = createStrictControl();
		buildSingleHostMocks(controller);
		connection.open();
		controller.replay();

		ConnectionManager manager = new ConnectionManager(config, provider);
		manager.connect();
		manager.openConnection();
		Assert.assertTrue("Connection Manager should be connected", manager.isConnected());
		Assert.assertTrue("SINGLE_HOST should be still marked available", manager.getNodeManager().isNodeAvailable(SINGLE_HOST));
		controller.verify();
	}
}
