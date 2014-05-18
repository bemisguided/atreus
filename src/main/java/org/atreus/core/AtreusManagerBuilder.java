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

import org.atreus.core.ext.plugins.AtreusPlugin;
import org.atreus.impl.core.Environment;
import org.atreus.impl.core.ManagerImpl;
import org.atreus.impl.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Atreus Session Factory Builder.
 *
 * @author Martin Crawford
 */
public class AtreusManagerBuilder {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private AtreusConfiguration configuration;
  private List<AtreusPlugin> plugins = new ArrayList<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  private AtreusManagerBuilder(AtreusConfiguration configuration) {
    this.configuration = configuration;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  /**
   * Builds the new Atreus Manager with the built configuration.
   *
   * @return a new Atreus Manager instance
   */
  public AtreusManager build() {

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

    // Validate Atreus configuration
    if (configuration.getScanPaths() == null || configuration.getScanPaths().length < 1) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_MISCONFIGURATION_AT_LEAST_ONE_SCAN_PATH_REQUIRED);
    }

    Environment environment = new Environment(configuration);
    ManagerImpl manager = new ManagerImpl(environment);
    environment.setManager(manager);
    environment.init();
    return manager;
  }

  /**
   * Creates a new instance of the Atreus Manager Builder.
   *
   * @return a new Atreus Manager Builder
   */
  public static AtreusManagerBuilder newInstance() {
    return new AtreusManagerBuilder(new AtreusConfiguration());
  }

  /**
   * Creates a new instance of the Atreus Manager Builder.
   *
   * @param configuration an Atreus Configuration object
   * @return a new Atreus Manager Builder
   */
  public static AtreusManagerBuilder newInstance(AtreusConfiguration configuration) {
    return new AtreusManagerBuilder(configuration);
  }

  /**
   * Creates a new instance of the Atreus Manager Builder.
   *
   * @param host      a node host name in the Cassandra cluster
   * @param port      port of the CQL protocol for the Cassandra cluster
   * @param keySpace  the common key space in the Cassandra cluster
   * @param scanPaths the package path to scan for configuration of entities
   * @return a new Atreus Manager Builder
   */
  public static AtreusManagerBuilder newInstance(String host, int port, String keySpace, String... scanPaths) {
    return newInstance().hosts(host).post(port).keySpace(keySpace).scanPaths(scanPaths);
  }

  /**
   * Creates a new instance of the Atreus Manager Builder.
   *
   * @param hosts     an array of node host names in the Cassandra cluster
   * @param port      port of the CQL protocol for the Cassandra cluster
   * @param keySpace  the common key space in the Cassandra cluster
   * @param scanPaths the package path to scan for configuration of entities
   * @return a new Atreus Manager Builder
   */
  public static AtreusManagerBuilder newInstance(String[] hosts, int port, String keySpace, String... scanPaths) {
    return newInstance().hosts(hosts).post(port).keySpace(keySpace).scanPaths(scanPaths);
  }

  public AtreusManagerBuilder hosts(String... hosts) {
    configuration.setHosts(hosts);
    return this;
  }

  public AtreusManagerBuilder post(int port) {
    configuration.setPort(port);
    return this;
  }

  public AtreusManagerBuilder keySpace(String keySpace) {
    configuration.setKeySpace(keySpace);
    return this;
  }

  public AtreusManagerBuilder scanPaths(String... scanPaths) {
    configuration.setScanPaths(scanPaths);
    return this;
  }

  public AtreusManagerBuilder withPlugin(AtreusPlugin plugin) {
    plugins.add(plugin);
    return this;
  }

}
