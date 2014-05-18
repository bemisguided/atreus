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
package org.atreus.impl.core;

import org.atreus.core.AtreusManager;
import org.atreus.core.AtreusSession;
import org.atreus.core.ext.meta.AtreusMetaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements an Atreus Session Factory.
 *
 * @author Martin Crawford
 */
public class ManagerImpl implements AtreusManager {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(ManagerImpl.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final Environment environment;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public ManagerImpl(Environment environment) {
    this.environment = environment;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public void disconnect() {
    environment.getCassandraCluster().close();
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
  public AtreusMetaManager getMetaManager() {
    return environment.getMetaManager();
  }

  @Override
  public int getPort() {
    return environment.getConfiguration().getPort();
  }

  @Override
  public boolean isConnected() {
    return !environment.getCassandraCluster().isClosed();
  }

  @Override
  public AtreusSession openSession() {
    return new SessionImpl(environment);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class