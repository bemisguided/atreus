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
package org.atreus.core;

import org.atreus.impl.AtreusSessionFactoryImpl;
import org.atreus.impl.util.StringUtils;

/**
 * Atreus Session Factory Builder.
 *
 * @author Martin Crawford
 */
public class AtreusSessionFactoryBuilder {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  private AtreusSessionFactoryBuilder() {
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  /**
   * Builds a new Atreus Session Factory.
   *
   * @param configuration an Atreus Configuration object
   * @return a connected Atreus Session Factory
   */
  public static AtreusSessionFactory buildFactory(AtreusConfiguration configuration) {
    AtreusSessionFactoryImpl factory = new AtreusSessionFactoryImpl(configuration);

    // Validate the Cassandra configuration
    if (configuration.getHosts() == null || configuration.getHosts().length < 1) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_MISCONFIGURATION_AT_LEAST_ONE_HOST_REQUIRED);
    }

    if (configuration.getPort() < 1) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_MISCONFIGURATION_PORT_REQUIRED);
    }

    if (StringUtils.isNullOrEmpty(configuration.getKeySpace())) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_MISCONFIGURATION_KEY_SPACE_REQUIRED);
    }
    if (configuration.getScanPaths() == null || configuration.getScanPaths().length < 1) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_MISCONFIGURATION_AT_LEAST_ONE_SCAN_PATH_REQUIRED);
    }

    factory.init();
    return factory;
  }

  /**
   * Builds a new Atreus Session Factory.
   *
   * @param host      a node host name in the Cassandra cluster
   * @param port      port of the CQL protocol for the Cassandra cluster
   * @param keySpace  the common key space in the Cassandra cluster
   * @param scanPaths the package path to scan for configuration of entities
   * @return a connected Atreus Session Factory
   */
  public static AtreusSessionFactory buildFactory(String host, int port, String keySpace, String... scanPaths) {
    AtreusConfiguration configuration = new AtreusConfiguration(host, port, keySpace, scanPaths);
    return buildFactory(configuration);
  }

  /**
   * Builds a new Atreus Session Factory.
   *
   * @param hosts     an array of node host names in the Cassandra cluster
   * @param port      port of the CQL protocol for the Cassandra cluster
   * @param keySpace  the common key space in the Cassandra cluster
   * @param scanPaths the package path to scan for configuration of entities
   * @return a connected Atreus Session Factory
   */
  public static AtreusSessionFactory buildFactory(String hosts[], int port, String keySpace, String... scanPaths) {
    AtreusConfiguration configuration = new AtreusConfiguration(hosts, port, keySpace, scanPaths);
    return buildFactory(configuration);
  }

}
