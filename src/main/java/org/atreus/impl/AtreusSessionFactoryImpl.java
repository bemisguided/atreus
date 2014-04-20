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
package org.atreus.impl;

import com.datastax.driver.core.Cluster;
import org.atreus.core.AtreusConfiguration;
import org.atreus.core.AtreusSession;
import org.atreus.core.AtreusSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements an Atreus Session Factory.
 *
 * @author Martin Crawford
 */
public class AtreusSessionFactoryImpl implements AtreusSessionFactory {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(AtreusSessionFactoryImpl.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final AtreusEnvironment environment;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public AtreusSessionFactoryImpl(AtreusEnvironment environment) {
    this.environment = environment;
  }

  public AtreusSessionFactoryImpl(AtreusConfiguration configuration) {
    this.environment = new AtreusEnvironment(configuration);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void connect() {
    Cluster cluster = Cluster.builder()
        .addContactPoints(environment.getConfiguration().getHosts())
        .withPort(environment.getConfiguration().getPort())
        .build();
    cluster.connect();
    environment.setCluster(cluster);
  }

  @Override
  public void disconnect() {
    environment.getCluster().close();
  }

  @Override
  public String[] getHosts() {
    return environment.getConfiguration().getHosts();
  }

  @Override
  public String getKeySpace() {
    return environment.getConfiguration().getKeySpace();
  }

  @Override
  public int getPort() {
    return environment.getConfiguration().getPort();
  }

  @Override
  public boolean isConnected() {
    return !environment.getCluster().isClosed();
  }

  @Override
  public AtreusSession openSession() {
    return new AtreusSessionImpl(environment, environment.getCluster().newSession());
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class