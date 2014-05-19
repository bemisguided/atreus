/**
 * The MIT License
 *
 * Copyright (c) 2014 Martin Crawford and contributors.
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
package org.atreus.core.impl.core;

import com.datastax.driver.core.ConsistencyLevel;
import junit.framework.Assert;
import org.atreus.core.*;
import org.atreus.core.tests.entities.common.TestEntity;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit tests for the Atreus Session Builder.
 *
 * @author Martin Crawford
 */
public class AtreusManagerBuilderTests extends BaseCassandraTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(AtreusManagerBuilderTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Test
  public void testBuildFactory() {
    LOG.info("Running testBuildFactory");
    AtreusManager manager = AtreusManagerBuilder.newInstance(CLUSTER_HOST_NAME, CLUSTER_PORT, DEFAULT_KEY_SPACE, TestEntity.class.getPackage().getName()).build();
    Assert.assertEquals(CLUSTER_HOST_NAME, manager.getHosts()[0]);
    Assert.assertEquals(CLUSTER_PORT, manager.getPort());
    Assert.assertEquals(DEFAULT_KEY_SPACE, manager.getKeySpace());
    Assert.assertTrue("Expected manager.isConnected to be true", manager.isConnected());
  }

  @Test
  public void testBuildFactoryWithMultipleHosts() {
    LOG.info("Running testBuildFactoryWithMultipleHosts");
    String[] hosts = new String[]{CLUSTER_HOST_NAME, "127.0.0.1"};
    AtreusManager manager = AtreusManagerBuilder.newInstance(hosts, CLUSTER_PORT, DEFAULT_KEY_SPACE, TestEntity.class.getPackage().getName()).build();
    Assert.assertEquals(hosts, manager.getHosts());
    Assert.assertEquals(CLUSTER_PORT, manager.getPort());
    Assert.assertEquals(DEFAULT_KEY_SPACE, manager.getKeySpace());
    Assert.assertTrue("Expected manager.isConnected to be true", manager.isConnected());
  }

  @Test
  public void testBuildFactoryWithConfiguration() {
    LOG.info("Running testBuildFactoryWithConfiguration");
    AtreusConfiguration configuration = new AtreusConfiguration();
    configuration.setHosts(CLUSTER_HOST_NAME);
    configuration.setPort(CLUSTER_PORT);
    configuration.setKeySpace( DEFAULT_KEY_SPACE);
    configuration.setScanPaths(TestEntity.class.getPackage().getName());
    configuration.setDefaultWriteBatch(false);
    configuration.setDefaultReadConsistencyLevel(ConsistencyLevel.EACH_QUORUM);
    configuration.setDefaultWriteConsistencyLevel(ConsistencyLevel.QUORUM);
    AtreusManager manager = AtreusManagerBuilder.newInstance(configuration).build();
    Assert.assertEquals(CLUSTER_HOST_NAME, manager.getHosts()[0]);
    Assert.assertEquals(CLUSTER_PORT, manager.getPort());
    Assert.assertEquals(DEFAULT_KEY_SPACE, manager.getKeySpace());
    Assert.assertTrue("Expected manager.isConnected to be true", manager.isConnected());
  }

  @Test(expected = AtreusInitialisationException.class)
  public void testMisconfigurationHost() {
    LOG.info("Running testMisconfigurationHost");
    try {
      AtreusManagerBuilder.newInstance(new String[0], 0, null, DEFAULT_SCAN_PATH).build();
    }
    catch (AtreusInitialisationException e) {
      org.junit.Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_MISCONFIGURATION_AT_LEAST_ONE_HOST_REQUIRED, e.getErrorCode());
      throw e;
    }
  }

  @Test(expected = AtreusInitialisationException.class)
  public void testMisconfigurationPort() {
    LOG.info("Running testMisconfigurationPort");
    try {
      AtreusManagerBuilder.newInstance(CLUSTER_HOST_NAME, 0, null, DEFAULT_SCAN_PATH).build();
    }
    catch (AtreusInitialisationException e) {
      org.junit.Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_MISCONFIGURATION_PORT_REQUIRED, e.getErrorCode());
      throw e;
    }
  }

  @Test(expected = AtreusInitialisationException.class)
  public void testMisconfigurationKeySpace() {
    LOG.info("Running testMisconfigurationKeySpace");
    try {
      AtreusManagerBuilder.newInstance(CLUSTER_HOST_NAME, CLUSTER_PORT, null, DEFAULT_SCAN_PATH).build();
    }
    catch (AtreusInitialisationException e) {
      org.junit.Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_MISCONFIGURATION_KEY_SPACE_REQUIRED, e.getErrorCode());
      throw e;
    }
  }

  @Test(expected = AtreusInitialisationException.class)
  public void testMisconfigurationScanPath() {
    LOG.info("Running testMisconfigurationScanPath");
    try {
      AtreusManagerBuilder.newInstance(CLUSTER_HOST_NAME, CLUSTER_PORT, DEFAULT_KEY_SPACE).build();
    }
    catch (AtreusInitialisationException e) {
      org.junit.Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_MISCONFIGURATION_AT_LEAST_ONE_SCAN_PATH_REQUIRED, e.getErrorCode());
      throw e;
    }
  }

  @Test(expected = AtreusClusterConnectivityException.class)
  public void testConnectFailure() {
    LOG.info("Running testConnectFailure");
    AtreusManagerBuilder.newInstance(CLUSTER_HOST_NAME, 1234, DEFAULT_KEY_SPACE, DEFAULT_SCAN_PATH).build();
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class